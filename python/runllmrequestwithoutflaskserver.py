from langchain import PromptTemplate
from langchain import hub
from langchain.docstore.document import Document
from langchain_community.document_loaders.csv_loader import CSVLoader
from langchain.schema import StrOutputParser
from langchain.schema.prompt_template import format_document
from langchain.schema.runnable import RunnablePassthrough
from langchain.vectorstores import Chroma
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings

""""
This is a sample code to run LLM request without using Flask server.

"""

loader = CSVLoader(file_path="F:/LLM_Projects/ExceptionRAG/data/csv-data/exception.csv", 
                   csv_args={
                        "delimiter": ",",
                        "fieldnames": ["ExceptionID","ExceptionName","ExceptionCause","ExceptionResolution"],
                    })
docs = loader.load()

gemini_embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001")

vectorstore = Chroma.from_documents(documents=docs, embedding=gemini_embeddings, persist_directory="./db")

vectorstore_disk = Chroma(persist_directory="./db", embedding_function=gemini_embeddings)

#K:1 means only one record will be retrived
retriever = vectorstore_disk.as_retriever(search_kwargs={"k": 1})
docs = retriever.get_relevant_documents("InvalidProductVariant")


llm = ChatGoogleGenerativeAI(model="gemini-pro", temperature=0.2, top_p=0.50)


llm_prompt_template = """You are production support engineer and you need to store summaries of exception you observed for user so that it will be helpful if that exception comes again.\n  
Summarize this exception in one to two paragraphs. "AT LEAST 50 WORDS"\n
Use below details to summarize the exception.\n
Exception Details: {ExceptionDetails} \n
ONLY PRINT THE SUMMARY OF THE EXCEPTION\n
AVOID PRINTING EXCEPTIONID, EXCEPTIONNAME IN OUTPUT
"""

llm_prompt = PromptTemplate.from_template(llm_prompt_template)


def format_docs(docs):
    return "\n\n".join(doc.page_content for doc in docs)

rag_chain = (
    {"ExceptionDetails": retriever | format_docs, "Task":RunnablePassthrough() }
    | llm_prompt
    | llm
    | StrOutputParser()
)

output = rag_chain.invoke("InvalidProductVariant")

print(output)





