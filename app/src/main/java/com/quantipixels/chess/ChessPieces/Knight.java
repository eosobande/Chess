package com.quantipixels.chess.ChessPieces;

import android.content.Context;

import com.quantipixels.chess.Move;
import com.quantipixels.chess.R;

public class Knight extends ChessMan {

    public Knight(Context context, boolean isWhite) {
        super(context, R.drawable.ic_knight, isWhite);
    }

    @Override
    protected void createMoves() {

        mMoves = new Move[8][1];

        mMoves[0][0] = new Move(2, 1);
        mMoves[1][0] = new Move(2, -1);
        mMoves[2][0] = new Move(-2, 1);
        mMoves[3][0] = new Move(-2, -1);
        mMoves[4][0] = new Move(1, 2);
        mMoves[5][0] = new Move(-1, 2);
        mMoves[6][0] = new Move(1, -2);
        mMoves[7][0] = new Move(-1, -2);

    }


}
