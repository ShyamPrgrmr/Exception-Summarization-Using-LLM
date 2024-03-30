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

from flask import Flask, request, jsonify

gemini_embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001")
vectorstore_disk = Chroma(persist_directory="./db", embedding_function=gemini_embeddings)
retriever = vectorstore_disk.as_retriever(search_kwargs={"k": 1})


def create_app(test_config=None):
    """
    Creates a Flask application for the Exception Summarizer.

    Args:
        test_config (dict, optional): Configuration dictionary for testing purposes. Defaults to None.

    Returns:
        Flask: The Flask application.
    """
    app = Flask("Exception Summarizer")

    @app.route("/exception-llm-summary")
    def get_exception():
        """
        Retrieves the summary of an exception.

        Returns:
            dict: The inference output containing the summary of the exception.
        """
        exception_name = request.args.get("name")
        inference_output = inference(exception_name)
        return jsonify(inference_output=inference_output)
 
    return app


def format_docs(docs):
    """
    Formats a list of documents into a single string.

    Args:
        docs (list): List of Document objects.

    Returns:
        str: The formatted string containing the page content of each document.
    """
    return "\n\n".join(doc.page_content for doc in docs)


def inference(exception_name):
    """
    Performs inference to generate a summary for the given exception.

    Args:
        exception_name (str): The name of the exception.

    Returns:
        str: The generated summary for the exception.
    """
    llm = ChatGoogleGenerativeAI(model="gemini-pro", temperature=0.2, top_p=0.50)
    llm_prompt_template = """You are production support engineer and you need to store summaries of exception you observed for user so that it will be helpful if that exception comes again.\n  
    Summarize this exception in one to two paragraphs. "AT LEAST 50 WORDS"\n
    Use below details to summarize the exception.\n
    Exception Details: {ExceptionDetails} \n
    ONLY PRINT THE SUMMARY OF THE EXCEPTION\n
    AVOID PRINTING EXCEPTIONID, EXCEPTIONNAME IN OUTPUT\n"""

    llm_prompt = PromptTemplate.from_template(llm_prompt_template)

    rag_chain = (
        {"ExceptionDetails": retriever | format_docs, "Task":RunnablePassthrough() }
        | llm_prompt
        | llm
        | StrOutputParser()
    )

    output = rag_chain.invoke(exception_name)
    return output


if __name__ == "__main__":
    app = create_app()
    app.run(port=5000)















