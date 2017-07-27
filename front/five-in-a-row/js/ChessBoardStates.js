/**
 * Created by Administrator on 2017/7/9.
 * 使用状态设计模式控制棋盘状态
 * 棋盘的行为根据状态变化而变化
 */

var InitState = function () {
    this.name = 'init';
    this.doState = function (chessBoard) {
        $('#bt_ready').removeAttr('disabled');
        $('#bt_regret').attr('disabled', true);
        $('#bt_give_up').attr('disabled', true);
        chessBoard.initElement();
        chessBoard.drawBoard();
    };
};

var StartStates = function () {

    this.name = 'start';
    this.doState = function (chessBoard) {
        var flashCount = 1;
        var draw = function () {
            chessBoard.drawBoard();
            chessBoard.drawPieces(flashCount % 2 === 0);   //使得最后一个棋子闪烁
            setTimeout(function () {
                chessBoard.currentState !== chessBoardStates.stopStates.name && draw();
            }, 400);
            flashCount >= 1000 && (flashCount = 0);
            flashCount++;
        };
        draw();
        chessBoard.changeState(chessBoard.color === ChessPieces.black ? chessBoardStates.playStates : chessBoardStates.waitStates);
    }
};

var WaitStates = function () {

    this.name = 'wait';
    this.doState = function (chessBoard) {
        chessBoard.unBindEvent();
        $('#bt_ready').attr('disabled', true);
        $('#bt_regret').removeAttr('disabled');
        $('#bt_give_up').removeAttr('disabled');
    }
};

var PlayStates = function () {

    var _this = this;
    this.name = 'play';

    this.doState = function (chessBoard) {
        chessBoard.bindEvent();
        $('#bt_ready').attr('disabled', true);
        $('#bt_regret').attr('disabled', true);
        $('#bt_give_up').removeAttr('disabled');
    };
};

var StopStates = function () {

    this.name = 'stop';
    this.doState = function (chessBoard) {
        chessBoard.unBindEvent();
        chessBoard.reset();
        $('#bt_ready').removeAttr('disabled');
        $('#bt_regret').attr('disabled', true);
        $('#bt_give_up').removeAttr('disabled');
    }
};

var chessBoardStates = {
    initStates: new InitState(),
    startStates: new StartStates(),
    waitStates: new WaitStates(),
    playStates: new PlayStates(),
    stopStates: new StopStates()
};

