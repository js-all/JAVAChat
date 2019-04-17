"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var express_1 = __importDefault(require("express"));
var socket_io_1 = __importDefault(require("socket.io"));
var http_1 = __importDefault(require("http"));
var Msg = /** @class */ (function () {
    function Msg(author, content) {
        if (author === void 0) { author = ""; }
        if (content === void 0) { content = ""; }
        this.author = author;
        this.content = content;
    }
    return Msg;
}());
var app = express_1.default();
// @ts-ignore
var server = http_1.default.Server(app);
var io = socket_io_1.default(server);
var MSGs = [];
var users = 0;
server.listen(6969);
app.get('/emsgs', function (req, res) {
    res.contentType('application/json');
    res.send(MSGs);
});
io.on('connection', function (socket) {
    var id = users;
    users++;
    socket.emit("ID", { id: id });
    socket.on('newMSG', function (msg) {
        MSGs.push(msg);
        socket.broadcast.emit('newMSG', { author: msg.author, content: msg.content, updaterID: id });
    });
});
