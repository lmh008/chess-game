/**
 * Created by Administrator on 2017/7/8.
 */
(function Game() {

    this.chessBoard = null;

    this.start = function () {
        this.chessBoard = new ChessBoard();
        this.chessBoard.changeState(chessBoardStates.initStates);
        chessBoardStates.startStates.color = ChessPieces.black;
        this.chessBoard.changeState(chessBoardStates.startStates);
    };

    this.start();
})();