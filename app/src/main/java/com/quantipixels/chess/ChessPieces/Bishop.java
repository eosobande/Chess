package com.quantipixels.chess.ChessPieces;

import android.content.Context;

import com.quantipixels.chess.Move;
import com.quantipixels.chess.R;

public class Bishop extends ChessMan {

    public Bishop(Context context, boolean isWhite) {
        super(context, R.drawable.ic_bishop, isWhite);
    }

    @Override
    protected void createMoves() {

        mMoves = new Move[4][7];

        for (int i = 1; i < 8; i++) {
            mMoves[0][i-1] = new Move(i, i);
            mMoves[1][i-1] = new Move(i, -i);
            mMoves[2][i-1] = new Move(-i, i);
            mMoves[3][i-1] = new Move(-i, -i);
        }

    }


}
