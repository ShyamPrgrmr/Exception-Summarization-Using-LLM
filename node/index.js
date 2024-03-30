/**
 * This is the main file for the ExceptionRAG project.
 * It sets up the server, connects to MongoDB, Kafka, and WebSocket,
 * and handles incoming exception messages from Kafka.
 */

const express = require('express');
const WebSocket = require('ws');
const env_var = require('dotenv').config()
const { MongoClient, ServerApiVersion } = require('mongodb');
const bodyParser = require('body-parser');
const cors = require('cors');

const { Kafka } = require('kafkajs')

const app = express();
const port = process.env.app_port || 3000;

// MongoDB configurations
const uri = `mongodb+srv://${process.env.db_username}:${process.env.db_password}@${process.env.db_cluster}/?retryWrites=true&w=majority&appName=${process.env.app_name}`
const client = new MongoClient(uri, {
    serverApi: {
        version: ServerApiVersion.v1,
        strict: true,
        deprecationErrors: true,
    }
});
const db_name = process.env.db_name;
const db_exception_collection = process.env.db_exception_collection;
const db_exception_summary_collection = process.env.db_exception_summary_collection;

// WebSocket configurations
const wss_port = process.env.app_websocket_port || 9000;
const wss = new WebSocket.Server({ port: wss_port });

wss.on('connection', function connection(ws) {
    /**
     * Event listener for WebSocket connection.
     * Sends the application key to the client when requested.
     * Logs the client connection and disconnection.
     * @param {WebSocket} ws - The WebSocket connection object.
     */
    ws.on('message', function incoming(message) {
        if (message == 'get_key') {
            ws.send(process.env.app_key);
            console.log('Client connected');
        }
    });

    ws.on('close', function () {
        console.log('Client disconnected');
    });
});

// LLM inference API configurations
const llm_api_url = process.env.llm_summary_url || 'http://localhost:5000/exception-llm-summary';

// Kafka configurations
const kafka_group = process.env.kafka_group_id || 'test-group';
const kafka_client_id = process.env.kafka_client_id || 'test-client';

const kafka = new Kafka({
    brokers: ["localhost:9092"],
    clientId: kafka_client_id,
});
const consumer = kafka.consumer({ groupId: `${kafka_group}` });

/**
 * Loads exception details from MongoDB cache or LLM API.
 * Sends the exception details to connected WebSocket clients.
 * @param {string} data - The exception details in a specific format.
 */
async function loadExceptionDetails(data) {
    let temp = data.split('|');

    let exception_name = temp[1];
    let exception_date = temp[0];
    let exception_file = temp[2];
    let exception_method = temp[3];
    let exception_line = temp[4];

    const exception_details = await client.db(db_name).collection(db_exception_summary_collection).find({ "ExceptionName": exception_name }).sort({ _id: -1 }).toArray();

    if (exception_details.length == 0) {
        console.log("Exception Data not found in MongoDB cache. Fetching from LLM API.")
        const exception_summary_url = `${llm_api_url}?name=${exception_name}`;
        const response = await fetch(exception_summary_url);
        const exception_summary_data = await response.json();
        const exception_summary = exception_summary_data.inference_output;

        data = {
            "ExceptionName": exception_name,
            "ExceptionDate": exception_date,
            "ExceptionFile": exception_file,
            "ExceptionMethod": exception_method,
            "ExceptionLine": exception_line,
            "ExceptionSummary": exception_summary
        }

        let ret = await client.db(db_name).collection(db_exception_summary_collection).insertOne(data);
        console.log(exception_name + " Exception Data inserted in MongoDB cache with ID: " + ret.insertedId)

        wss.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(JSON.stringify(data));
            }
        });

    } else {
        console.log("Exception Data found in MongoDB cache.")

        let data = {
            "ExceptionName": exception_name,
            "ExceptionDate": exception_date,
            "ExceptionFile": exception_file,
            "ExceptionMethod": exception_method,
            "ExceptionLine": exception_line,
            "ExceptionSummary": exception_details[0].ExceptionSummary
        }

        wss.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(JSON.stringify(data));
            }
        });
    }
}

/**
 * Connects to Kafka, subscribes to the 'exception-topic' topic,
 * and processes incoming exception messages.
 */
const run = async () => {
    await consumer.connect()
    await consumer.subscribe({ topics: ['exception-topic'], fromBeginning: false })
    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            console.log({ value: message.value.toString() });

            loadExceptionDetails(message.value.toString());

        }
    });
}

run().catch(e => console.error(`[example/consumer] ${e.message}`, e));

// Express configurations
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('static'));
app.get('/', (req, res) => {
    res.sendFile(__dirname + '/static/index.html')
});

// Start server
(async () => {
    try {
        await client.connect();
        app.listen(port, () => {
            console.log(`Server is running on port ${port}`);
        });
    }
    catch (err) {
        console.log(err);
    }
})();

