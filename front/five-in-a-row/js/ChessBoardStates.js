/**
 * Created by Administrator on 2017/7/9.
 */

var InitState = function () {

    var _this = this;
    this.chessBoard = null;

    this.doStates = function (chessBoard) {
        this.chessBoard = chessBoard;
        this.drawBoard();
    };

    this.drawBoard = function () {
        this.chessBoard.boardCtx.fillStyle = '#D3B39A';
        this.chessBoard.boardCtx.fillRect(0, 0, this.chessBoard.boardCanvas.width, this.chessBoard.boardCanvas.height);
        this.drawLine();
        this.drawSpecialPoint();
        this.chessBoard.boardImgData = this.chessBoard.boardCtx.getImageData(0, 0, this.chessBoard.boardCanvas.width, this.chessBoard.boardCanvas.height); //保存棋盘图片
    };

    this.drawLine = function () {
        var maxPoint = this.chessBoard.boardCanvas.width - this.chessBoard.margin;
        var dl = function (fn) {
            var k = 0;
            for (var i = _this.chessBoard.margin; i <= maxPoint; i += _this.chessBoard.gap) {
                _this.chessBoard.boardCtx.beginPath();
                if (k % 4 === 0) {
                    _this.chessBoard.boardCtx.lineWidth = 1.3;
                } else {
                    _this.chessBoard.boardCtx.lineWidth = 0.8;
                }
                fn.call(_this, i);
                _this.chessBoard.boardCtx.stroke();
                _this.chessBoard.boardCtx.closePath();
                k++;
            }
        };
        dl(function (i) {
            _this.chessBoard.boardCtx.moveTo(i, _this.chessBoard.margin);
            _this.chessBoard.boardCtx.lineTo(i, maxPoint);
        });
        dl(function (i) {
            _this.chessBoard.boardCtx.moveTo(_this.chessBoard.margin, i);
            _this.chessBoard.boardCtx.lineTo(maxPoint, i);
        });
    };

    this.drawSpecialPoint = function () {
        var sixPart = this.chessBoard.lengthOfSide / 6;
        var drawPoint = function (x, y) {
            _this.chessBoard.boardCtx.beginPath();
            _this.chessBoard.boardCtx.fillStyle = "black";
            _this.chessBoard.boardCtx.arc(x, y, 4, 0, 2 * Math.PI);
            _this.chessBoard.boardCtx.fill();
            _this.chessBoard.boardCtx.closePath();
        };
        drawPoint(this.chessBoard.boardCanvas.width / 2, this.chessBoard.boardCanvas.height / 2);
        drawPoint(sixPart + this.chessBoard.margin, sixPart + this.chessBoard.margin);
        drawPoint(sixPart + this.chessBoard.margin, sixPart * 5 + this.chessBoard.margin);
        drawPoint(sixPart * 5 + this.chessBoard.margin, sixPart + this.chessBoard.margin);
        drawPoint(sixPart * 5 + this.chessBoard.margin, sixPart * 5 + this.chessBoard.margin);
    };

    return {
        doState: function () {
            _this.doState();
        }
    }
};

var StartStates = function () {

    this.color = null;
    this.doStates = function (chessBoard) {
        chessBoard.color = this.color;
        chessBoard.changeState(this.color === ChessPieces.black ? chessBoardStates.playStates : chessBoardStates.waitStates);
    }
};

var WaitStates = function () {

    this.doStates = function (chessBoard) {
        chessBoard.bindEvent();
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
    }
};

var PlayStates = function () {

    var _this = this;
    this.chessBoard = null;

    this.doStates = function (chessBoard) {
        this.chessBoard = chessBoard;
        this.chessBoard.boardCanvas.removeEvent();
        this.bindEvent();
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
    }

    this.start = function () {
        this.bindEvent();
        var draw = function () {
            _this.reset();
            _this.drawPieces(flashCount % 2 === 0);   //使得最后一个棋子闪烁
            setTimeout(function () {
                draw();
            }, 400);
            flashCount >= 1000 && (flashCount = 0);
            flashCount++;
        };
        draw();
    };

    this.bindEvent = function () {
        var currentPoint = null;
        this.boardCanvas.onmousemove = function (event) {
            var point = _this.checkPointIegal(event.offsetX, event.offsetY);
            if (point && !_this.chesses[point.x][point.y]) {
                currentPoint = point;
                _this.boardCanvas.style.cursor = 'pointer';
            } else {
                currentPoint = null;
                _this.boardCanvas.style.cursor = 'default';
            }
        };
        this.boardCanvas.onclick = function () {
            if (currentPoint) {
                _this.chesses[currentPoint.x][currentPoint.y] = _this.color;
                _this.lastChessPicePoint = currentPoint;
                if (_this.callBack && typeof _this.callBack === 'function') {
                    _this.callBack.call(this, currentPoint, _this.chesses[currentPoint.x][currentPoint.y]);
                }
                _this.drawPieces(true);
                flashCount = 0;
            }
        }
    };
};

var chessBoardStates = {
    initStates: new InitState(),
    startStates: new StartStates(),
    waitStates: new WaitStates(),
    playStates: new PlayStates()
};

