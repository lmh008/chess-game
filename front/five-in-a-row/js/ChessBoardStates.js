/**
 * Created by Administrator on 2017/7/9.
 */

var InitState = function () {

    this.doState = function (chessBoard) {
        chessBoard.initElement();
        chessBoard.drawBoard();
    };

};

var StartStates = function () {

    this.color = null;
    this.doState = function (chessBoard) {
        chessBoard.color = this.color;
        var flashCount = 0;
        var draw = function () {
            chessBoard.reset();
            chessBoard.drawPieces(flashCount % 2 === 0);   //使得最后一个棋子闪烁
            setTimeout(function () {
                draw();
            }, 400);
            flashCount >= 1000 && (flashCount = 0);
            flashCount++;
        };
        draw();
        chessBoard.changeState(this.color === ChessPieces.black ? chessBoardStates.playStates : chessBoardStates.waitStates);
    }
};

var WaitStates = function () {

    this.doState = function (chessBoard) {
        chessBoard.boardCanvas.onclick = null;
        chessBoard.boardCanvas.onmousemove = null;
        chessBoard.boardCanvas.style.cursor = 'default';
    }
};

var PlayStates = function () {

    var _this = this;
    var flashCount = 0;
    this.chessBoard = null;
    this.callBack = null;

    this.doState = function (chessBoard) {
        this.chessBoard = chessBoard;
        this.bindEvent(chessBoard);
    };

    this.bindEvent = function (chessBoard) {
        var currentPoint = null;
        chessBoard.boardCanvas.onmousemove = function (event) {
            var point = chessBoard.checkPointIegal(event.offsetX, event.offsetY);
            if (point && !chessBoard.chesses[point.x][point.y]) {
                currentPoint = point;
                chessBoard.boardCanvas.style.cursor = 'pointer';
            } else {
                currentPoint = null;
                chessBoard.boardCanvas.style.cursor = 'default';
            }
        };
        chessBoard.boardCanvas.onclick = function () {
            if (currentPoint) {
                chessBoard.chesses[currentPoint.x][currentPoint.y] = chessBoard.color;
                chessBoard.lastChessPicePoint = currentPoint;
                if (_this.callBack && typeof _this.callBack === 'function') {
                    _this.callBack.call(this, currentPoint, _this.chesses[currentPoint.x][currentPoint.y]);
                }
                chessBoard.drawPieces(true);
                chessBoard.changeState(chessBoardStates.waitStates);
            }
        }
    };

    return {
        setCallBack: function (fn) {
            _this.callBack = fn;
        },
        doState: function () {
            _this.doState.apply(_this, arguments);
        }
    }
};

var chessBoardStates = {
    initStates: new InitState(),
    startStates: new StartStates(),
    waitStates: new WaitStates(),
    playStates: new PlayStates()
};

