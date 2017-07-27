/**
 * Created by Administrator on 2017/7/8.
 */
function ChessBoard() {

    var _this = this;
    var flashCount = 0;
    this.boardCanvas = null;
    this.boardCtx = null;
    this.chesses = null;
    this.container = null;
    this.lengthOfSide = null; //棋盘总宽度
    this.gap = null;    //每一行的间隔
    this.margin = 20;   //第一行离边界的间隔
    this.chessPiecesFactory = new ChessPiecesFactory();
    this.lastChessPicePoint = null;
    this.boardImgData = null;
    this.currentState = null;
    this.color = null;
    this.onUnderPawn = null;

    this.initElement = function () {
        this.initChesses();
        this.container = document.getElementById('game_chess_board');
        this.lengthOfSide = this.container.clientWidth * 0.8;
        this.gap = this.lengthOfSide / 24;
        if (!this.boardCanvas) {
            this.boardCanvas = document.createElement('canvas');
            this.container.insertBefore(this.boardCanvas, document.getElementById('div_button_group'));
        }
        this.boardCanvas.width = this.lengthOfSide + this.margin * 2;
        this.boardCanvas.height = this.lengthOfSide + this.margin * 2;
        this.boardCtx = this.boardCanvas.getContext("2d");
    };

    this.reset = function () {
        this.initChesses();
        this.drawBoard();
    };

    this.initChesses = function () {
        this.chesses = new Array(25);
        for (var i = 0; i < 25; i++) {
            this.chesses[i] = new Array(25);
        }
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
                if (_this.onUnderPawn && typeof _this.onUnderPawn === 'function') {
                    _this.onUnderPawn.call(this, currentPoint);
                }
                _this.drawPieces(true);
            }
        }
    };

    this.unBindEvent = function () {
        this.boardCanvas.onclick = null;
        this.boardCanvas.onmousemove = null;
        this.boardCanvas.style.cursor = 'default';
    };

    this.addOppoChess = function (point) {
        _this.chesses[point.x][point.y] = -_this.color;
        _this.lastChessPicePoint = point;
    };

    this.drawBoard = function () {
        if (this.boardImgData) {
            this.boardCtx.putImageData(this.boardImgData, 0, 0);
        } else {
            this.boardCtx.fillStyle = '#D3B39A';
            this.boardCtx.fillRect(0, 0, this.boardCanvas.width, this.boardCanvas.height);
            this.drawLine();
            this.drawSpecialPoint();
            this.boardImgData = this.boardCtx.getImageData(0, 0, this.boardCanvas.width, this.boardCanvas.height); //保存棋盘图片
        }
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
        clientX = clientX - this.margin;
        clientY = clientY - this.margin;
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

    this.changeColor = function (color) {
        _this.color = color || -_this.color;
    };

    return {
        constructor: ChessBoard,
        bindOnUnderPawn: function (fn) {
            _this.onUnderPawn = fn;
        },
        changeColor: function (color) {
            _this.changeColor.apply(_this, arguments);
        },
        addOppoChess: function (point) {
            _this.addOppoChess.apply(_this, arguments);
        },
        changeState: function (state) {
            _this.currentState = state.name;
            return _this.changeState.apply(_this, arguments);
        },
        synchronized: function (boardState) {
            _this.lastChessPicePoint = boardState.lastPoint;
            _this.chesses = boardState.chesses;
        },
        width: function () {
            return _this.lengthOfSide;
        },
        accept: function (visitor) {
            visitor && 'visitChessBoard' in visitor && typeof visitor.visitChessBoard === 'function' && visitor.visitChessBoard(_this);
        }
    }
}