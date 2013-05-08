var ws = require('websocket.io');
var url = require("url");
var http = require('http').createServer(onRequest).listen(3000);
var clients = [];
var server = ws.attach(http, {
	path: '/mywebsocket'
});

server.on('connection', function (socket) {
	clients.push(socket);
	console.log('new client');
	socket.on('message', function (msg) {
		console.log("msg recv: " + JSON.stringify(msg));
	});
	socket.on('close', function () {
		console.log('client leaving ');
		clients.splice(clients.indexOf(this), 1);
	});
});

function onRequest(req, resp) {
	var urlobj = url.parse(req.url, true);
	if (urlobj.query.activity) {
		var actObj = JSON.parse(urlobj.query.activity);
		if (actObj) {
			clients.forEach(function(client) {
				var msg = JSON.stringify(actObj);
				client.send(msg);
				console.log("NODE sending " + msg);
			});
		}
	}
	resp.end();

}

