/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    var chessBoard = new ChessBoard();
    var _this = this;
    var socket = null;
    var $div_name_input = $('#div_name_input');
    var $div_wait_queue = $('#div_wait_queue');
    var $div_game = $('#div_game');
    var $wait_queue_label = $('#wait_queue_label');
    var player = {};

    var start = function () {
        initSocket();
        intiEvent();
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
            player.name = nameInput;
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
                        chessBoard.changeState(chessBoardStates.initStates);
                        socket.send(JSON.stringify({
                            topic: 'game',
                            tag: 'playerReady'
                        }));
                        break;
                    case 'start':
                        chessBoard.changeColor(msg.data.color);
                        chessBoard.changeState(chessBoardStates.startStates);
                        break;
                    case 'underPawn':
                        chessBoard.addOppoChess({
                            x: msg.data.x,
                            y: msg.data.y
                        });
                        chessBoard.changeState(chessBoardStates.playStates);
                        break;
                    case 'regretSynchronized':
                        chessBoard.synchronized(msg.data);
                        chessBoard.changeState(chessBoardStates.waitStates);
                        break;
                    case 'responseRegret':
                        if(msg.data.result){
                            chessBoard.synchronized(msg.data.boardState);
                            chessBoard.changeState(chessBoardStates.playStates)
                            alert("对手同意悔棋！");
                        }else{
                            alert("对手不同意悔棋！");
                        }
                        break;
                    case 'chat':
                        alert(msg.data);
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