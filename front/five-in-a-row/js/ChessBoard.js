/**
 * Created by Administrator on 2017/7/8.
 */
function ChessBoard(callBack) {

    var _this = this;
    var flashCount = 0;
    this.boardCanvas = null;
    this.boardCtx = null;
    this.chesses = null;
    this.container = null;
    this.lengthOfSide = 720;
    this.gap = 30;
    this.margin = 10;
    this.chessPiecesFactory = new ChessPiecesFactory();
    this.lastChessPicePoint = null;
    this.boardImgData = null;
    this.currentState = null;
    this.color = null;

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

    this.initElement = function () {
        var arrayLength = this.lengthOfSide / this.gap + 1;
        this.chesses = new Array(arrayLength);
        for (var i = 0; i < arrayLength; i++) {
            this.chesses[i] = new Array(arrayLength);
        }
        this.container = document.getElementById('div_chess_board');
        this.boardCanvas = document.createElement('canvas');
        this.boardCanvas.width = this.lengthOfSide + this.margin * 2;
        this.boardCanvas.height = this.lengthOfSide + this.margin * 2;
        this.container.appendChild(this.boardCanvas);
        this.boardCtx = this.boardCanvas.getContext("2d");
    };

    this.reset = function () {
        this.boardCtx.putImageData(this.boardImgData, 0, 0);
    };

    this.drawBoard = function () {
        this.boardCtx.fillStyle = '#D3B39A';
        this.boardCtx.fillRect(0, 0, this.boardCanvas.width, this.boardCanvas.height);
        this.drawLine();
        this.drawSpecialPoint();
        this.boardImgData = this.boardCtx.getImageData(0, 0, this.boardCanvas.width, this.boardCanvas.height); //保存棋盘图片
    };

    this.drawLine = function () {
        var maxPoint = this.boardCanvas.width - this.margin;
        var dl = function (fn) {
            var k = 0;
            for (var i = _this.margin; i <= maxPoint; i += _this.gap) {
                _this.boardCtx.beginPath();
                if (k % 4 === 0) {
                    _this.boardCtx.lineWidth = 1.3;
                } else {
                    _this.boardCtx.lineWidth = 0.8;
                }
                fn.call(_this, i);
                _this.boardCtx.stroke();
                _this.boardCtx.closePath();
                k++;
            }
        };
        dl(function (i) {
            _this.boardCtx.moveTo(i, _this.margin);
            _this.boardCtx.lineTo(i, maxPoint);
        });
        dl(function (i) {
            _this.boardCtx.moveTo(_this.margin, i);
            _this.boardCtx.lineTo(maxPoint, i);
        });
    };

    this.drawSpecialPoint = function () {
        var sixPart = this.lengthOfSide / 6;
        var drawPoint = function (x, y) {
            _this.boardCtx.beginPath();
            _this.boardCtx.fillStyle = "black";
            _this.boardCtx.arc(x, y, 4, 0, 2 * Math.PI);
            _this.boardCtx.fill();
            _this.boardCtx.closePath();
        };
        drawPoint(this.boardCanvas.width / 2, this.boardCanvas.height / 2);
        drawPoint(sixPart + this.margin, sixPart + this.margin);
        drawPoint(sixPart + this.margin, sixPart * 5 + this.margin);
        drawPoint(sixPart * 5 + this.margin, sixPart + this.margin);
        drawPoint(sixPart * 5 + this.margin, sixPart * 5 + this.margin);
    };

    this.drawPieces = function (drawLast) {
        var point = null;
        for (var i = 0; i < this.chesses.length; i++) {
            for (var j = 0; j < this.chesses[i].length; j++) {
                if (this.chesses[i][j]) {
                    point = {
                        x: i * _this.gap + _this.margin,
                        y: j * _this.gap + _this.margin
                    };
                    if (!drawLast && i === this.lastChessPicePoint.x && j === this.lastChessPicePoint.y) {
                        continue;
                    }
                    this.chessPiecesFactory.getChess(this.chesses[i][j]).draw(this.boardCtx, point);
                }
            }
        }
    };

    this.checkPointIegal = function (clientX, clientY) {
        clientX = clientX - 10;
        clientY = clientY - 10;
        if (clientY >= 0 && clientY >= 0) {
            clientX /= this.gap;
            clientY /= this.gap;
            var checkX = clientX - parseInt(clientX);
            var checkY = clientY - parseInt(clientY);
            if (( checkX < 0.33 || checkX > 0.66) && (checkY < 0.33 || checkY > 0.66)) {
                return {
                    x: Math.round(clientX),
                    y: Math.round(clientY)
                };
            }
        } else {
            return false;
        }
    };

    this.changeState = function (state) {
        if (state && 'doState' in state && typeof state.doState === 'function') {
            state.doState(this);
        }
    };

    return {
        constructor: ChessBoard,
        changeState: function (state) {
            _this.currentState = state;
            return _this.changeState.apply(_this, arguments);
        },
        accept: function (visitor) {
            visitor && 'visitChessBoard' in visitor && typeof visitor.visitChessBoard === 'function' && visitor.visitChessBoard(_this);
        }
    }
}