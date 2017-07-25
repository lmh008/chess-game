/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    var chessBoard = null;
    var _this = this;
    var socket = null;
    var $div_name_input = null;
    var $div_game = null;
    var $div_wait_queue = null;
    var $wait_queue_label = null;

    var start = function () {
        $div_name_input = $('#div_name_input');
        $div_wait_queue = $('#div_wait_queue');
        $div_game = $('#div_game');
        $wait_queue_label = $('#wait_queue_label');
        initSocket();
        intiEvent();
        chessBoard = new ChessBoard();
        chessBoard.bindOnUnderPawn(function (point) {
            socket.send(JSON.stringify({
                topic: 'game',
                tag: 'underPawn',
                data: {
                    x: point.x,
                    y: point.y
                }
            }));
            chessBoard.changeState(chessBoardStates.waitStates);
        });
        /*$div_game.show();
        $div_name_input.hide();
        chessBoard.changeState(chessBoardStates.initStates);*/
    };

    var intiEvent = function () {
        $('#bt_confirm').on('click', function () {
            var nameInput = $('#name').val();
            if (nameInput) {
                socket.send(JSON.stringify({
                    topic: 'base',
                    tag: 'setName',
                    data: nameInput
                }));
                $div_name_input.hide();
                $div_wait_queue.show();
            }
        });
        $('#start_queue').on('click', function () {
            socket.send(JSON.stringify({
                topic: 'base',
                tag: 'startQueue'
            }));
            $(this).hide();
        });
    };

    var initSocket = function () {
        socket = new WebSocket("ws://mrj.website:8080/five");
        socket.onopen = function () {
            console.log('socket connected!')
        };
        socket.onclose = function () {
            alert("lost connect!");
        };
        socket.onmessage = function (command) {
            var msg = JSON.parse(command.data);
            console.log(msg);
            if (msg.topic && msg.topic == 'base') {
                switch (msg.tag) {
                    case 'playerInfos':
                        $wait_queue_label.html('共' + msg.data.online + '人在线，游戏队列人数:' + msg.data.onWait);
                        break;
                }
            }
            if (msg.topic && msg.topic == 'game') {
                switch (msg.tag) {
                    case 'prepareGame':
                        $div_wait_queue.hide();
                        $div_game.show();
                        chessBoard.changeColor(msg.data.color);
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
                        chessBoard.addOppoChess({
                            x: msg.data.x,
                            y: msg.data.y
                        });
                        chessBoard.changeState(chessBoardStates.playStates);
                        break;
                    case 'win':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        alert("you win!");
                        break;
                    case 'loss':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        alert("you loss!");
                        break;
                    case 'giveUp':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        alert("opponent give up, you win!");
                        break;
                }
            }
        }
    };

    start();
})();