package com.quantipixels.chess;

public class Move {

    public int row;
    public int column;
    public boolean isOnlyAllowedFromDefaultState;
    public boolean isOnlyAllowedIfOccupied;
    public boolean isOnlyAllowedIfNotOccupied;

    public Move(int row, int column) {
        init(row, column);
    }

    public Move(int row, int column, boolean ifDefaultState, boolean ifOccupied) {
        init(row, column, ifDefaultState, ifOccupied);
    }

    public Move(int row, int column, boolean ifDefaultState, boolean ifOccupied, boolean ifNotOccupied) {
        init(row, column, ifDefaultState, ifOccupied, ifNotOccupied);
    }

    private void init(int row, int column) {
        this.row = row;
        this.column = column;
    }

    private void init(int row, int column, boolean ifDefaultState) {
        init(row, column);
        isOnlyAllowedFromDefaultState = ifDefaultState;
    }

    private void init(int row, int column, boolean ifDefaultState, boolean ifOccupied) {
        init(row, column, ifDefaultState);
        isOnlyAllowedIfOccupied = ifOccupied;
    }

    private void init(int row, int column, boolean ifDefaultState, boolean ifOccupied, boolean ifNotOccupied) {
        init(row, column, ifDefaultState, ifOccupied);
        isOnlyAllowedIfNotOccupied = ifNotOccupied;
    }

}
