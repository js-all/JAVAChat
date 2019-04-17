import express from 'express';
import socketio from 'socket.io';
import http from 'http';

class Msg {
    content: string;
    author: string;
    constructor(author :string = "", content :string = "") {
        this.author = author;
        this.content = content;
    }
}

const app = express();
// @ts-ignore
const server = http.Server(app);
const io = socketio(server);
const MSGs :Msg[] = [];

let users :number = 0;

server.listen(6969);

app.get('/emsgs', (req, res) => {
    res.contentType('application/json');
    res.send(MSGs);
});

io.on('connection', socket => {
    let id = users;
    users++;
    socket.emit("ID", {id: id});
    socket.on('newMSG', msg => {
        MSGs.push(msg);
        socket.broadcast.emit('newMSG', {author: msg.author, content: msg.content, updaterID: id})
    })
})

