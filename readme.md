
# Exception Summarization using LLM, RAG, and Kafka

This project aims to streamline exception handling in Spring Boot applications by automatically generating summaries for exceptions encountered during runtime. Leveraging cutting-edge technologies such as Language Model (LLM), Retrieval-Augmented Generation (RAG), Langchain, Kafka, Flask, NodeJS, and Spring Boot, the system offers a robust solution for efficient exception management.

## Project Overview

When a Spring Boot application encounters an exception, detailed information including the exception date, name, line number, and Java file is logged and sent to a Kafka topic. The NodeJS server consumes this data from Kafka, initiating the summarization process.

## Workflow:

1. Data Ingestion: Exception details are pushed to a Kafka topic by the Spring Boot application.
2. Consumption: The NodeJS server consumes exception details from the Kafka topic.
3. Summary Lookup: NodeJS checks if a summary for the encountered exception exists in MongoDB. If found, it retrieves the summary directly.
4. Summary Generation: If no summary is found, NodeJS requests the Flask server for summarization assistance.
5. Summarization Process: Flask server coordinates with Langchain, RAG, and ChromaDB to generate a summary for the exception.
6. Storage: The generated summary, along with exception details, is stored in MongoDB for future reference.
7. Feedback Loop: Subsequent encounters of the same exception can directly fetch the summary from MongoDB, reducing processing overhead.

## Technologies Used

- LLM (Language Model): Utilized for natural language processing and understanding.
- RAG (Retrieval-Augmented Generation): Employed for generating context-aware summaries.
- Langchain: Integrated for facilitating communication between Flask and LLM/RAG.
- Kafka: Used as a message broker for real-time data streaming.
- Flask: Provides the API endpoint for interaction with LLM, RAG, and ChromaDB.
- NodeJS: Responsible for consuming Kafka messages and coordinating summarization tasks.
- Spring Boot: Powers the Spring Boot application, generating and logging exceptions.

## Usage

To utilize this system for exception summarization in your Spring Boot applications:

1. Ensure Kafka is configured and running to facilitate message passing.
2. Set up MongoDB for storing exception details and summaries.
3. Deploy the NodeJS server to consume Kafka messages and orchestrate the summarization process.
4. Deploy the Flask server to handle summarization requests and interact with LLM, RAG, and ChromaDB.
5. Integrate exception logging in your Spring Boot application to publish exception details to the Kafka topic.

## Project Diagram

![alt text]([http://url/to/img.png](https://github.com/ShyamPrgrmr/Exception-Summarization-Using-LLM/blob/main/images/Exception-Summarization%20Small.jpg))


