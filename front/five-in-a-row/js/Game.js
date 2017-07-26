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
        $div_game.show();
        $div_name_input.hide();
        chessBoard.changeState(chessBoardStates.initStates);
        $("#textarea_chat_info").height(chessBoard.width() - 100);
        var $div_my_info = $("#div_my_info");
        var $div_opponent_info = $("#div_opponent_info");
        $div_my_info.height(chessBoard.width() / 2);
        $div_opponent_info.height(chessBoard.width() / 2);
        var canvas_my = $("#canvas_my").get(0);
        var canvas_opponent = $("#canvas_opponent").get(0);
        canvas_my.width = $div_my_info.find(".panel-body").width();
        canvas_my.height = $div_my_info.find(".panel-body").height();
        canvas_opponent.width = $div_opponent_info.find(".panel-body").width();
        canvas_opponent.height = $div_opponent_info.find(".panel-body").height();
        var r = canvas_my.height >= canvas_my.width ? canvas_my.width / 2 - 20 : canvas_my.height / 2 - 20;
        var myCanvasContext = canvas_my.getContext("2d");
        myCanvasContext.beginPath();
        myCanvasContext.fillStyle = 'black';
        myCanvasContext.arc(canvas_my.width / 2, canvas_my.height / 2, r, 0, 2 * Math.PI);
        myCanvasContext.fill();
        myCanvasContext.closePath();
        var opponentCanvasContext = canvas_opponent.getContext("2d");
        opponentCanvasContext.beginPath();
        opponentCanvasContext.fillStyle = 'black';
        opponentCanvasContext.arc(canvas_opponent.width / 2, canvas_opponent.height / 2, r, 0, 2 * Math.PI);
        opponentCanvasContext.fill();
        opponentCanvasContext.closePath();

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
            } else {
                BootstrapDialog.show({message: '请输入一个名字！'});
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
            BootstrapDialog.show({message: "lost connect!"});
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
                        chessBoard.changeColor(msg.data);
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
                        if (msg.data.result) {
                            chessBoard.synchronized(msg.data.boardState);
                            chessBoard.changeState(chessBoardStates.playStates)
                            BootstrapDialog.show({message: "对手同意悔棋！"});
                        } else {
                            BootstrapDialog.show({message: "对手不同意悔棋！"});
                        }
                        break;
                    case 'chat':
                        BootstrapDialog.show({message: msg.data});
                        break;
                    case 'win':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        BootstrapDialog.show({message: '恭喜你获得胜利!'});
                        break;
                    case 'loss':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        BootstrapDialog.show({message: "你输了，再接再厉!"});
                        break;
                    case 'giveUp':
                        chessBoard.changeState(chessBoardStates.waitStates);
                        BootstrapDialog.show({message: "你的对手认输了, 你赢了!"});
                        break;
                    case 'lostOpponent':
                        chessBoard.changeState(chessBoardStates.stopStates);
                        BootstrapDialog.show({message: "你的对手离开了游戏!"});
                        break;
                }
            }
        }
    };

    start();
})();