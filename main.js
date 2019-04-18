"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var express_1 = __importDefault(require("express"));
var socket_io_1 = __importDefault(require("socket.io"));
var http_1 = __importDefault(require("http"));
var sqlite3_1 = __importDefault(require("sqlite3"));
var path_1 = __importDefault(require("path"));
sqlite3_1.default.verbose();
var Msg = /** @class */ (function () {
    function Msg(author, content) {
        if (author === void 0) { author = ""; }
        if (content === void 0) { content = ""; }
        this.author = author;
        this.content = content;
    }
    return Msg;
}());
var MSGsClass = /** @class */ (function (_super) {
    __extends(MSGsClass, _super);
    function MSGsClass() {
        var _this = _super.call(this) || this;
        Object.setPrototypeOf(_this, Object.create(MSGsClass.prototype));
        return _this;
    }
    MSGsClass.prototype.add = function (msg) {
        MSGs.push(msg);
        db.run('INSERT INTO msg (author, content) values(?, ?)', [msg.author, msg.content]);
    };
    MSGsClass.prototype.remove = function (i) {
        this.splice(i, 1);
        db.run('DELETE FROM msg WHERE id=?', [i + 1]);
    };
    return MSGsClass;
}(Array));
var app = express_1.default();
// @ts-ignore
var server = http_1.default.Server(app);
var io = socket_io_1.default(server);
var MSGs = new MSGsClass();
var db = new sqlite3_1.default.Database(path_1.default.join(__dirname, "/db/messages.db"));
db.run('CREATE TABLE IF NOT EXISTS `msg` (`author` TEXT NOT NULL,`content` TEXT NOT NULL,`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE);');
db.run('CREATE TABLE IF NOT EXISTS `acc` (`pseudo` TEXT NOT NULL,`password` TEXT NOT NULL,`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE);');
var users = 0;
db.each('SELECT * FROM msg ORDER BY id', function (err, row) {
    MSGs.push(new Msg(row.author, row.content));
}, function () {
    server.listen(6969);
    app.get('/emsgs', function (req, res) {
        res.contentType('application/json');
        res.send(MSGs);
    });
    io.on('connection', function (socket) {
        var id = users;
        console.log("new User ! ", id);
        users++;
        socket.emit("ID", { id: id });
        socket.on('newMSG', function (msg) {
            MSGs.add(msg);
            if (MSGs.length > 100)
                MSGs.remove(0);
            socket.broadcast.emit('newMSG', { author: msg.author, content: msg.content, updaterID: id });
        });
        socket.once('disconnect', function () {
            console.log("user departed. ", id);
            users--;
        });
    });
});
