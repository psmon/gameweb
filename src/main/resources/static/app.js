var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/public', onMessageReceived );

        var username=$("#name").val();
        if(username.length<1){username="Unknown"};
        // Tell your username to the server
        stompClient.send("/app/lobby.addUser",
            {},
            JSON.stringify({sender: username, type: 'JOIN'})
        )
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendGameMsg() {
    var content = $('#gamemsg').val();
    stompClient.send("/app/hello",
        {},
        JSON.stringify({content: content, type: 'CHAT'})
    )
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        showGreeting(message.sender + '-- joined!')
    } else if (message.type === 'LEAVE') {
        showGreeting(message.sender + 'left!')
    } else {
        showGreeting(message.content)
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendGameMsg(); });
});