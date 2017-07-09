/**
 * Created by Administrator on 2017/7/9.
 * 使用状态设计模式控制棋盘状态
 * 棋盘的行为根据状态变化而变化
 */

var InitState = function () {
    this.name = 'init';
    this.doState = function (chessBoard) {
        chessBoard.initElement();
        chessBoard.drawBoard();
    };

};

var StartStates = function () {

    this.name = 'start';
    this.color = null;
    this.doState = function (chessBoard) {
        chessBoard.color = this.color;
        var flashCount = 0;
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
        chessBoard.changeState(this.color === ChessPieces.black ? chessBoardStates.playStates : chessBoardStates.waitStates);
    }
};

var WaitStates = function () {

    this.name = 'wait';
    this.doState = function (chessBoard) {
        chessBoard.unBindEvent();
    }
};

var PlayStates = function () {

    var _this = this;
    var flashCount = 0;
    this.callBack = null;
    this.name = 'play';

    this.doState = function (chessBoard) {
        chessBoard.callBack = function (currentPoint) {
            if (_this.callBack && typeof _this.callBack === 'function') {
                _this.callBack.call(this, currentPoint);
            }
        };
        chessBoard.changeState(chessBoardStates.waitStates);
        chessBoard.bindEvent();
    };
};

var StopStates = function () {

    this.name = 'stop';
    this.doState = function (chessBoard) {
        alert("stop");
        chessBoard.unBindEvent();
        chessBoard.reset();
    }
};

var chessBoardStates = {
    initStates: new InitState(),
    startStates: new StartStates(),
    waitStates: new WaitStates(),
    playStates: new PlayStates(),
    stopStates: new StopStates()
};

