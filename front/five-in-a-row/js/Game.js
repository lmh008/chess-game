function Game(ops) {

    var chessBoard = new ChessBoard();
    var option = $.extend({
        quit: null,
        ready: null,
        onUnderPawn: null,
        requestRegret: null,
        responseRegret: null,
        chat: null,
        giveUp: null
    }, ops || {});
    var player = {
        name: 'someBody',
        color: ChessPieces.black,
        winCount: 0,
        lossCount: 0,
        opponent: {
            name: 'opponent',
            color: ChessPieces.white,
            winCount: 0,
            lossCount: 0
        }
    };
    var $div_my_info = $("#div_my_info");
    var $div_opponent_info = $("#div_opponent_info");
    var $textarea_chat_info = $("#textarea_chat_info");
    var $game_chess_board = $('#game_chess_board');
    var $bt_ready = $('#bt_ready');
    var $bt_regret = $('#bt_regret');
    var $bt_give_up = $('#bt_give_up');
    var $bt_quit = $('#bt_quit');
    var canvas_my = $("#canvas_my").get(0);
    var canvas_opponent = $("#canvas_opponent").get(0);
    var myCanvasContext = canvas_my.getContext("2d");
    var opponentCanvasContext = canvas_opponent.getContext("2d");
    var r;
    var flashFlag = 0;

    chessBoard.bindOnUnderPawn(function (point) {
        option.onUnderPawn && $.isFunction(option.onUnderPawn) && option.onUnderPawn.call(this, point);
        opponentTurn();
    });

    var intiUi = function () {
        $textarea_chat_info.height($game_chess_board.height() * 0.82);
        $div_my_info.height($game_chess_board.width() * 0.43);
        $div_opponent_info.height($game_chess_board.width() * 0.43);
        canvas_my.width = $div_my_info.find(".panel-body").width();
        canvas_my.height = $div_my_info.find(".panel-body").height();
        canvas_opponent.width = $div_opponent_info.find(".panel-body").width();
        canvas_opponent.height = $div_opponent_info.find(".panel-body").height();
        r = canvas_my.height >= canvas_my.width ? canvas_my.width / 2 - 20 : canvas_my.height / 2 - 20;
        drawMyPieces();
        drawOpponentPieces();
        drawPieces();
    };

    var bindEvent = function () {
        $("#input_chat_content").on("keydown", function (event) {
            if (event.keyCode === 13) {
                var $this = $(event.currentTarget);
                var text = $this.val();
                if (text && option.chat && $.isFunction(option.chat)) {
                    option.chat.call(this, text);
                    $this.val('');
                    chatInfoAppend(player.name + ' ：' + text, true);
                }
            }
        });
        $bt_ready.unbind('click').on('click', function (event) {
            option.ready && $.isFunction(option.ready) && option.ready.call(this);
            $(this).attr('disabled', true);
        });
        $bt_give_up.unbind('click').on('click', function () {
            BootstrapDialog.confirm('确认投降吗？', function (result) {
                if (result) {
                    option.giveUp && $.isFunction(option.giveUp) && option.giveUp.call(this);
                    chessBoard.changeState(chessBoardStates.stopStates);
                }
            });
        });
        $bt_quit.unbind('click').on('click', function () {
            BootstrapDialog.confirm('确定要离开吗？', function (result) {
                if (result) {
                    chessBoard.changeState(chessBoardStates.stopStates);
                    option.quit && $.isFunction(option.quit) && option.quit.call(this);
                }
            });
        });
        $bt_regret.unbind('click').on('click', function () {
            option.requestRegret && $.isFunction(option.requestRegret) && option.requestRegret.call(this);
            BootstrapDialog.show({message: '正在征询' + player.opponent.name + '同意!'});
        });
    };

    var chatInfoAppend = function (text, type) {
        /*var oFont = document.createElement("FONT");
        var oText = document.createTextNode(text + '\r');
        oFont.style.color = type ? "#beffca" : "#1526ff";
        $textarea_chat_info.get(0).appendChild(oFont);
        oFont.appendChild(oText);*/
        $textarea_chat_info.append(text + "\r\n");
    };

    var drawOpponentPieces = function () {
        opponentCanvasContext.beginPath();
        opponentCanvasContext.fillStyle = player.opponent.color == ChessPieces.black ? 'black' : 'white';
        opponentCanvasContext.arc(canvas_opponent.width / 2, canvas_opponent.height / 2, r, 0, 2 * Math.PI);
        opponentCanvasContext.fill();
        opponentCanvasContext.closePath();
    };

    var drawMyPieces = function () {
        myCanvasContext.beginPath();
        myCanvasContext.fillStyle = player.color == ChessPieces.black ? 'black' : 'white';
        myCanvasContext.arc(canvas_my.width / 2, canvas_my.height / 2, r, 0, 2 * Math.PI);
        myCanvasContext.fill();
        myCanvasContext.closePath();
    };

    var drawPieces = function () {
        var flashCount = 1;
        var draw = function () {
            if (flashFlag === 1) {
                myCanvasContext.clearRect(0, 0, canvas_my.width, canvas_my.height);
                flashCount % 2 === 0 && drawMyPieces();
            } else if (flashFlag === -1) {
                opponentCanvasContext.clearRect(0, 0, canvas_opponent.width, canvas_opponent.height);
                flashCount % 2 === 0 && drawOpponentPieces();
            }
            flashCount >= 1000 && (flashCount = 0);
            flashCount++;
            setTimeout(function () {
                draw();
            }, 400);
        };
        draw();
    };

    var synchronizedPlayers = function (player) {
        var text = "比分：胜" + player.winCount + " 负" + player.lossCount;
        $div_my_info.find("#div_my_score").html(text);
        text = "比分：胜" + player.opponent.winCount + " 负" + player.opponent.lossCount;
        $div_opponent_info.find("#div_opponent_score").html(text);
        $div_my_info.find(".panel-title").html(player.name);
        $div_opponent_info.find(".panel-title").html(player.opponent.name);
    };

    var myTurn = function () {
        chessBoard.changeState(chessBoardStates.playStates);
        flashFlag = 1;
    };

    var opponentTurn = function () {
        chessBoard.changeState(chessBoardStates.waitStates);
        flashFlag = -1;
    };

    return {
        load: function (data) {
            console.log(data);
            $.extend(player, data);
            chessBoard.changeColor(player.color);
            chessBoard.changeState(chessBoardStates.initStates);
            intiUi();
            bindEvent();
            synchronizedPlayers(player);
        },
        opponentReady: function (data) {
            //todo
        },
        start: function (data) {
            $.extend(player, data);
            chessBoard.changeColor(player.color);
            flashFlag = player.color == ChessPieces.black ? 1 : -1;
            chessBoard.changeState(chessBoardStates.startStates);
            synchronizedPlayers(player);
        },
        underPawn: function (data) {
            chessBoard.addOppoChess({
                x: data.x,
                y: data.y
            });
            myTurn();
        },
        requestRegret: function (data) {
            BootstrapDialog.confirm(player.opponent.name + '请求悔棋, 是否同意?', function (result) {
                option.responseRegret && $.isFunction(option.responseRegret) && option.responseRegret.call(this, result);
            });
        },
        regretSynchronized: function (data) {
            chessBoard.synchronized(data);
            opponentTurn();
        },
        responseRegret: function (data) {
            if (data.result) {
                chessBoard.synchronized(data.boardState);
                myTurn();
                BootstrapDialog.show({message: player.opponent.name + "同意悔棋！"});
            } else {
                BootstrapDialog.show({message: player.opponent.name + "不同意悔棋！"});
            }
        },
        chat: function (data) {
            chatInfoAppend(player.opponent.name + ' ：' + data);
        },
        win: function (data) {
            chessBoard.changeState(chessBoardStates.stopStates);
            BootstrapDialog.show({message: '恭喜你获得胜利!'});
        },
        loss: function (data) {
            chessBoard.changeState(chessBoardStates.stopStates);
            BootstrapDialog.show({message: "你输了，再接再厉!"});
        },
        giveUp: function (data) {
            chessBoard.changeState(chessBoardStates.stopStates);
            BootstrapDialog.show({message: player.opponent.name + "认输了, 你赢了!"});
        },
        lostOpponent: function (data) {
            chessBoard.changeState(chessBoardStates.stopStates);
            BootstrapDialog.show({message: player.opponent.name + "离开了游戏!"});
            flashFlag = 0;
            option.quit && $.isFunction(option.quit) && option.quit.call(this);
        }
    }
}