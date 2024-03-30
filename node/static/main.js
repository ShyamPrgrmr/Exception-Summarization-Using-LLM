/**
 * WebSocket object for communication with the server.
 * @type {WebSocket}
 */
const socket = new WebSocket('ws://localhost:9000');
let key = '';

/**
 * Event listener for when the DOM content is loaded.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Add any necessary code here
});

/**
 * Event listener for when the socket connection is opened.
 * @param {Event} event - The event object.
 */
socket.addEventListener('open', function (event) {
    socket.send('get_key');
});

/**
 * Event listener for when a message is received from the server.
 * @param {MessageEvent} event - The message event object.
 */
socket.addEventListener('message', function (event) {
    if(event.data.includes("ExceptionName")){
        json_data = JSON.parse(event.data);
        appendCard(json_data);
    } else{
        key = event.data;
        onLoad();
    }  
});

/**
 * Event listener for when the socket connection is closed.
 * @param {CloseEvent} event - The close event object.
 */
socket.addEventListener('close', function (event) {
    console.log('Connection closed');
});

/**
 * Function called when the socket connection is established and the key is received.
 */
function onLoad(){
    console.log('Connected to Server with Key: ' + key);
}

/**
 * Function to append a card element to the main division.
 * @param {Object} data - The data object containing information for the card.
 */
function appendCard(data){
    let card = document.createElement('div');
    card.className = 'card';
    card.innerHTML = `
        <div class="card-body">
            <p class="exception_id"><span class="bold">Time:</span>&nbsp;${data.ExceptionDate}</p>
            <p class="exception_name"><span class="bold">Exception Name:</span>&nbsp;${data.ExceptionName}</p>
            <p class="exception_filename"><span class="bold">Exception File:</span>&nbsp;${data.ExceptionFile}</p>
            <p class="exception_functionname"><span class="bold">Exception Function:</span>&nbsp;${data.ExceptionMethod}&nbsp;<span>(lineNumber : ${data.ExceptionLine})</span></p>
            <p class="exception_summary"><span class="bold">Exception Summary:</span>&nbsp;${data.ExceptionSummary}</p>
        </div>`;
    document.getElementById('main-division-logs').appendChild(card);
}
