import express from 'express';
import socketio from 'socket.io';
import http from 'http';
import sqlite from 'sqlite3';
import path from 'path';

sqlite.verbose();

class Msg {
    content: string;
    author: string;
    constructor(author: string = "", content: string = "") {
        this.author = author;
        this.content = content;
    }
}

class MSGsClass extends Array<Msg> {
    constructor() {
        super();
        Object.setPrototypeOf(this, Object.create(MSGsClass.prototype));
    }
    add(msg: Msg) {
        MSGs.push(msg);
        db.run('INSERT INTO msg (author, content) values(?, ?)', [msg.author, msg.content]);
    }
    remove(i: number) {
        this.splice(i, 1);
        db.run('DELETE FROM msg WHERE id=?', [i+1]);
    }
}

const app = express();
// @ts-ignore
const server = http.Server(app);
const io = socketio(server);
const MSGs: MSGsClass = new MSGsClass();
const db = new sqlite.Database(path.join(__dirname, "/db/messages.db"));
db.run('CREATE TABLE IF NOT EXISTS `msg` (`author` TEXT NOT NULL,`content` TEXT NOT NULL,`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE);');
db.run('CREATE TABLE IF NOT EXISTS `acc` (`pseudo` TEXT NOT NULL,`password` TEXT NOT NULL,`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE);');

let users: number = 0;

db.each('SELECT * FROM msg ORDER BY id', (err, row) => {
    MSGs.push(new Msg(row.author, row.content));
}, () => {
    server.listen(6969);

    app.get('/emsgs', (req, res) => {
        res.contentType('application/json');
        res.send(MSGs);
    });

    io.on('connection', socket => {
        let id = users;
        console.log("new User ! ", id)
        users++;
        socket.emit("ID", { id: id });
        socket.on('newMSG', msg => {
            MSGs.add(msg);
            if (MSGs.length > 100) MSGs.remove(0);
            socket.broadcast.emit('newMSG', { author: msg.author, content: msg.content, updaterID: id })
        });
        socket.once('disconnect', () => {
            console.log("user departed. ", id);
            users--;
        });
    });

});
