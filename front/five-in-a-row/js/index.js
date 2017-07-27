/**
 * Created by Administrator on 2017/7/8.
 */
$(function () {
    var _this = this;
    var socket = new WebSocket("ws://mrj.website:8080/five");
    var $div_name_input = $('#div_name_input');
    var $div_wait_queue = $('#div_wait_queue');
    var $div_game = $('#div_game');
    var $wait_queue_label = $('#wait_queue_label');

    var game = new Game({
        socket: socket,
        quit: function () {
            sendMessage('game', 'quit');
            $('#start_queue').show();
            showWait();
        },
        ready: function () {
            sendMessage('game', 'playerReady');
        },
        onUnderPawn: function (point) {
            sendMessage('game', 'underPawn', {
                x: point.x,
                y: point.y
            });
        },
        requestRegret: function () {
            sendMessage('game', 'requestRegret');
        },
        responseRegret: function (result) {
            sendMessage('game', 'responseRegret', !!result);
        },
        chat: function (message) {
            sendMessage('game', 'chat', message);
        },
        giveUp: function () {
            sendMessage('game', 'giveUp');
        }
    });

    function start() {
        initSocket();
        intiEvent();
        showMain();
        // showGame();
    }

    function intiEvent() {
        $('#bt_confirm').on('click', function () {
            var nameInput = $('#name').val();
            if (nameInput) {
                sendMessage('base', 'setName', nameInput);
                showWait();
            } else {
                BootstrapDialog.show({message: '请输入一个名字！'});
            }
        });
        $('#start_queue').on('click', function () {
            sendMessage('base', 'startQueue');
            $(this).hide();
        });
    }

    function sendMessage(topic, tag, data) {
        socket.send(JSON.stringify({
            topic: topic,
            tag: tag,
            data: data === undefined ? null : data
        }));
    }

    function showMain() {
        $div_name_input.show();
        $div_wait_queue.hide();
        $div_game.hide();
    }

    function showWait() {
        $div_name_input.hide();
        $div_wait_queue.show();
        $div_game.hide();
    }

    function showGame(data) {
        $div_name_input.hide();
        $div_wait_queue.hide();
        $div_game.show();
        game.load(data);
    }

    function initSocket() {
        socket.onopen = function () {
            console.log('socket connected!')
        };
        socket.onclose = function () {
            BootstrapDialog.show({
                type: BootstrapDialog.TYPE_DANGER,
                message: "lost connect!"
            });
        };
        socket.onmessage = function (command) {
            var msg = JSON.parse(command.data);
            console.log(msg);
            if (msg.topic && msg.topic == 'base') {
                switch (msg.tag) {
                    case 'playerInfos':
                        $wait_queue_label.html('共' + msg.data.online + '人在线，游戏队列人数:' + msg.data.onWait);
                        break;
                    case 'prepareGame':
                        showGame(msg.data);
                        break;
                }
            } else if (msg.topic && msg.topic == 'game') {
                var tag = msg.tag;
                $.isFunction(game[tag]) && (game[tag].call(game, msg.data));
            } else {
                BootstrapDialog.show({
                    type: BootstrapDialog.TYPE_DANGER,
                    message: 'un support tag!'
                });
            }
        }
    }

    start();
});