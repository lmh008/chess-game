/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    var chessBoard = null;
    var _this = this;
    var socket = null;
    var div_name_input = null;
    var wait_queue = null;
    var div_chess_board = null;
    var wait_queue_label = null;

    var start = function () {
        div_name_input = document.getElementById('div_name_input');
        wait_queue = document.getElementById('wait_queue');
        div_chess_board = document.getElementById('div_chess_board');
        wait_queue_label = document.getElementById('wait_queue_label');
        initSocket();
        intiEvent();
        chessBoard = new ChessBoard();
    };

    var intiEvent = function () {
        document.getElementById('bt_confirm').onclick = function () {
            var nameInput = document.getElementById('name');
            if (nameInput && nameInput.value) {
                socket.send(JSON.stringify({
                    topic: 'base',
                    tag: 'setName',
                    data: nameInput.value
                }));
                div_name_input.style.display = 'none';
                wait_queue.style.display = '';
            }
        };
        document.getElementById('start_queue').onclick = function () {
            socket.send(JSON.stringify({
                topic: 'base',
                tag: 'startQueue'
            }));
            this.style.display = 'none';
        };
    };

    var initSocket = function () {
        socket = new WebSocket("ws://127.0.0.1:8080/five");
        socket.onopen = function () {
            console.log('socket connected!')
        };
        socket.onclose = function () {
            alert("lost connect!");
        };
        socket.onmessage = function (command) {
            var msg = JSON.parse(command.data);
            console.log(msg);
            chessBoardStates.playStates.callBack = function (point) {
                socket.send(JSON.stringify({
                    topic: 'game',
                    tag: 'underPawn',
                    data: {
                        x: point.x,
                        y: point.y
                    }
                }));
            };
            if (msg.topic && msg.topic == 'base') {
                switch (msg.tag) {
                    case 'playerInfos':
                        wait_queue_label.innerHTML = '共' + msg.data.online + '人在线，游戏队列人数:' + msg.data.onWait;
                        break;
                }
            }
            if (msg.topic && msg.topic == 'game') {
                switch (msg.tag) {
                    case 'prepareGame':
                        chessBoardStates.initStates.color = msg.data.color;
                        chessBoard.changeState(chessBoardStates.initStates);
                        socket.send(JSON.stringify({
                            topic: 'game',
                            tag: 'playerReady'
                        }));
                        break;
                    case 'start':
                        chessBoard.changeState(chessBoardStates.startStates);
                        break;
                    case 'underPawn':
                        chessBoardStates.opponentStates.point = {
                            x: msg.data.x,
                            y: msg.data.y
                        };
                        chessBoard.changeState(chessBoardStates.opponentStates);
                        break;
                    case 'win':
                        alert("you win!");
                        chessBoard.changeState(chessBoardStates.waitStates);
                        break;
                    case 'loss':
                        chessBoardStates.opponentStates.point = {
                            x: msg.data.x,
                            y: msg.data.y
                        };
                        chessBoard.changeState(chessBoardStates.opponentStates);
                        chessBoard.changeState(chessBoardStates.waitStates);
                        alert("you loss!");
                        break;
                    case 'giveUp':
                        break;
                }
            }
        }
    };

    start();
})();