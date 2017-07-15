/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    var chessBoard = null;
    var _this = this;
    var socket = null;

    function init() {

    }

    var start = function () {
        socket = new WebSocket("ws://localhost:8080/five");
        socket.onopen = function () {
            init();
        };
        socket.onclose = function () {
            alert("lost connect!");
        }
        /*chessBoard = new ChessBoard();
         chessBoard.changeState(chessBoardStates.initStates);
         chessBoardStates.startStates.color = ChessPieces.black;
         chessBoard.changeState(chessBoardStates.startStates);
         setTimeout(function () {
         chessBoard.changeState(chessBoardStates.stopStates);
         }, 30000);*/
    };

    var init = function () {
        socket.send(JSON.stringify({
            topic: 'base',
            tag: 'setName',
            data: {
                name: "asd",
                f1: "aswww",
                f2: "2",
                f3: "2",
                strs: ["12", 2, 3, "aw"]
            }
        }));

        socket.onmessage = function (msg) {
            console.log(msg);
        }
    };

    start();
})();