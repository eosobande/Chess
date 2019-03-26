package com.quantipixels.chess;

public class Position {

    public int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getPosition() {
        return (row * ChessBoard.NO_OF_COLUMNS) + column;
    }

}
