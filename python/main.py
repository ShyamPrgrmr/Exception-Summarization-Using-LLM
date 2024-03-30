from langchain import PromptTemplate
from langchain import hub
from langchain.docstore.document import Document
from langchain_community.document_loaders.csv_loader import CSVLoader
from langchain.schema import StrOutputParser
from langchain.schema.prompt_template import format_document
from langchain.schema.runnable import RunnablePassthrough
from langchain.vectorstores import Chroma


loader = CSVLoader(file_path="F:/LLM_Projects/ExceptionRAG/data/csv-data/exception.csv", 
                   csv_args={
                        "delimiter": ",",
                        "fieldnames": ["ExceptionID","ExceptionName","ExceptionCause","ExceptionResolution"],
                    })
docs = loader.load()



from langchain_google_genai import GoogleGenerativeAIEmbeddings

gemini_embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001")

#vectorstore = Chroma.from_documents(documents=docs, embedding=gemini_embeddings, persist_directory="./db")

vectorstore_disk = Chroma(persist_directory="./db", embedding_function=gemini_embeddings)


retriever = vectorstore_disk.as_retriever(search_kwargs={"k": 1})


data = retriever.get_relevant_documents("InvalidProductVariant")

print(data)
