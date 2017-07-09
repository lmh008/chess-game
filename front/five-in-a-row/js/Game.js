/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    this.chessBoard = null;

    this.start = function () {
        this.chessBoard = new ChessBoard();
        this.chessBoard.init();
        this.chessBoard.start();
    };

    this.start();
})();