package com.quantipixels.chess.ChessPieces;

import android.content.Context;

import com.quantipixels.chess.Move;
import com.quantipixels.chess.R;

public class Queen extends ChessMan {

    public Queen(Context context, boolean isWhite) {
        super(context, R.drawable.ic_queen, isWhite);
    }

    @Override
    protected void createMoves() {

        mMoves = new Move[8][7];
        for (int i = 1; i < 8; i++) {
            // bishop moves
            mMoves[0][i-1] = new Move(i, i);
            mMoves[1][i-1] = new Move(i, -i);
            mMoves[2][i-1] = new Move(-i, i);
            mMoves[3][i-1] = new Move(-i, -i);

            // rook moves
            mMoves[4][i-1] = new Move(i, 0);
            mMoves[5][i-1] = new Move(-i, 0);
            mMoves[6][i-1] = new Move(0, i);
            mMoves[7][i-1] = new Move(0, -i);
        }

    }


}
