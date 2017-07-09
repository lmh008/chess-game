/**
 * Created by Administrator on 2017/7/8.
 * 享元设计模式
 * 棋子的黑白颜色是棋子的内部状态,坐标可以做外部状态
 */

/**
 * 享元工厂,创建并管理享元对象。
 * @returns {{constructor: ChessPiecesFactory, getChess: getChess}}
 * @constructor
 */
function ChessPiecesFactory() {

    var _this = this;
    this.chessPieces = {};
    this.chessPieces[ChessPieces.black] = null;
    this.chessPieces[ChessPieces.white] = null;

    this.getChess = function (color) {
        if (color) {
            !this.chessPieces[color] && (this.chessPieces[color] = new ChessPieces(color));
            return this.chessPieces[color];
        }
    };

    return {
        constructor: ChessPiecesFactory,
        getChess: function () {
            return _this.getChess.apply(_this, arguments);
        }
    }

}

/**
 * 享元对象
 * @param color
 * @returns {{constructor: ChessPieces, draw: drawBoard}}
 * @constructor
 */
function ChessPieces(color) {

    var _this = this;
    this.color = color;

    this.drawBoard = function (ctx, point) {
        if (point && point.x && point.y) {
            var radius = 13;
            ctx.beginPath();
            ctx.fillStyle = this.color === ChessPieces.white ? 'white' : 'black';
            ctx.arc(point.x, point.y, radius, 0, 2 * Math.PI);
            ctx.fill();
            ctx.closePath();
        }
    };

    return {
        constructor: ChessPieces,
        draw: function () {
            return _this.drawBoard.apply(_this, arguments);
        }
    };

}
ChessPieces.black = -1;
ChessPieces.white = 1;