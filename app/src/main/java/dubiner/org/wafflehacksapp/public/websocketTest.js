let id = document.getElementById("text");

let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
ws.send("client test");