var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
        renderTable('intro');
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        // for broad cast
        stompClient.subscribe('/topic/public', onMessageReceived );

        // for send to some
        stompClient.subscribe('/user/topic/public', onMessageReceived );

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

function joinTable(tableNo) {
    var content = $('#gamemsg').val();
    stompClient.send("/app/game.req",
        {},
        JSON.stringify({content: 'join',num1:tableNo, type: 'GAME'})
    )
}

function sendChatMsg() {
    var content = $('#gamemsg').val();
    stompClient.send("/app/hello",
        {},
        JSON.stringify({content: content, type: 'CHAT'})
    )
}

function sendGameMsg() {
    var content = $('#gamemsg').val();
    stompClient.send("/app/game.req",
        {},
        JSON.stringify({content: content, type: 'GAME'})
    )
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type == 'JOIN') {
        showGreeting('Welcome ' + message.sender)
    } else if (message.type == 'LEAVE') {
        showGreeting(message.sender + 'left!')
    } else if(message.type == 'GAME'){
        processTableMessage(message);
        showGreeting(message.content);
    } else{
        showGreeting(message.content);
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendGameMsg(); });

    $( "#demo1" ).click(function() { joinTable(1) });
    $( "#demo2" ).click(function() { renderTable('background') });
    $( "#demo3" ).click(function() { renderTable('gameinit') });

});