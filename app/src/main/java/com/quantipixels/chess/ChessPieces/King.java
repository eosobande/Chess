package com.quantipixels.chess.ChessPieces;

import android.content.Context;

import com.quantipixels.chess.ChessBoard;
import com.quantipixels.chess.Move;
import com.quantipixels.chess.Position;
import com.quantipixels.chess.R;

import java.util.ArrayList;
import java.util.HashMap;

public class King extends ChessMan {

    public King(Context context, boolean isWhite) {
        super(context, R.drawable.ic_king, isWhite);
    }

    @Override
    public void advance(ChessMan[][] gameState, Position position) {

        if (position.column == mPosition.column - 2 && position.row == mPosition.row) {

            gameState[position.row][0].advance(gameState,
                    new Position(position.row, position.column + 1));

        } else if (position.column == mPosition.column + 2 && position.row == mPosition.row) {

            gameState[position.row][7].advance(gameState,
                    new Position(position.row, position.column - 1));

        }

        super.advance(gameState, position);
    }

    @Override
    protected void createMoves() {

        mMoves = new Move[8][2];
        mMoves[0][0] = new Move(1, 0);
        mMoves[1][0] = new Move(1, 1);
        mMoves[2][0] = new Move(1, -1);
        mMoves[3][0] = new Move(0, 1);
        mMoves[4][0] = new Move(0, -1);
        mMoves[5][0] = new Move(-1, 0);
        mMoves[6][0] = new Move(-1, 1);
        mMoves[7][0] = new Move(-1, -1);

    }

    @Override
    protected void specialMoves(ChessMan[][] gameState, HashMap<String, ArrayList<Position>> allChessMen) {

        if (!hasMoved) {

            ChessMan chessMan;
            ArrayList<Position> attackersPositions = allChessMen.get(mOtherColor);

            if (!gameState[mPosition.row][mPosition.column - 1].isChessMan() &&
                    !gameState[mPosition.row][mPosition.column - 2].isChessMan() &&
                    !gameState[mPosition.row][mPosition.column - 3].isChessMan() &&
                    gameState[mPosition.row][mPosition.column - 4].isRook() &&
                    !gameState[mPosition.row][mPosition.column - 4].hasMoved) {

                Position one = gameState[mPosition.row][mPosition.column - 1].mPosition,
                        two = gameState[mPosition.row][mPosition.column - 2].mPosition,
                        three = gameState[mPosition.row][mPosition.column - 3].mPosition;

                boolean longCastling = true;

                for (Position attacker : attackersPositions) {
                    chessMan = gameState[attacker.row][attacker.column];
                    if (chessMan.canAdvanceTo(one) || chessMan.canAdvanceTo(two) || chessMan.canAdvanceTo(three)) {
                        longCastling = false;
                        break;
                    }
                }

                if (longCastling) {
                    mAdvances.add(new Position(mPosition.row, mPosition.column - 2));
                }

            }

            if (!gameState[mPosition.row][mPosition.column + 1].isChessMan() &&
                    !gameState[mPosition.row][mPosition.column + 2].isChessMan() &&
                    gameState[mPosition.row][mPosition.column + 3].isRook() &&
                    !gameState[mPosition.row][mPosition.column + 3].hasMoved) {

                Position one = gameState[mPosition.row][mPosition.column + 1].mPosition,
                        two = gameState[mPosition.row][mPosition.column + 2].mPosition;

                boolean shortCastling = true;

                for (Position attacker : attackersPositions) {
                    chessMan = gameState[attacker.row][attacker.column];
                    if (chessMan.canAdvanceTo(one) || chessMan.canAdvanceTo(two)) {
                        shortCastling = false;
                        break;
                    }
                }

                if (shortCastling) {
                    mAdvances.add(new Position(row(), column() + 2));
                }

            }

        }

    }

    public boolean isChecked(ChessMan[][] gameState, ArrayList<Position> enemies) {
        for (Position enemy : enemies) {
            if (gameState[enemy.row][enemy.column].canAdvanceTo(mPosition)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckMated(ChessBoard chessBoard) {

        ChessMan attacker = null;
        for (Position enemy : chessBoard.mAllChessMen.get(mOtherColor)) {
            if (chessBoard.mGameState[enemy.row][enemy.column].canAdvanceTo(mPosition)) {
                attacker = chessBoard.mGameState[enemy.row][enemy.column];
                break;
            }
        }

        return attacker != null && !(canEvadeAttack(chessBoard) ||
                isAttackerBlockable(attacker, chessBoard) ||
                isAttackerCapturable(attacker.mPosition, chessBoard));

    }

    private boolean isAttackerBlockable(ChessMan attacker, ChessBoard chessBoard) {

        // Can the attacking piece's line of sight be blocked by an ally
        // moving in the way (does not work for a knight). This also assumes that
        // moving the ally does not expose the king to check.

        if (!(attacker.isKnight() && attacker.isPawn() && attacker.isKing())) {

            int startRow = (attacker.row() < row() ? attacker.row() : row()) + 1;
            int endRow = (attacker.row() < row() ? row() : attacker.row()) - 1;
            int startColumn = (attacker.column() < column() ? attacker.column() : column()) + 1;
            int endColumn = (attacker.column() < column() ? column() : attacker.column()) - 1;

            boolean sameRow = attacker.row() == row(), sameColumn = attacker.column() == column();

            ArrayList<Position> blockablePositions = new ArrayList<>();

            if (sameRow) {
                while (startColumn <= endColumn) {
                    blockablePositions.add(new Position(row(), startColumn));
                    startColumn++;
                }
            } else if (sameColumn) {
                while (startRow <= endRow) {
                    blockablePositions.add(new Position(startRow, column()));
                    startRow++;
                }
            } else {
                while (startRow <= endRow && startColumn <= endColumn) {
                    blockablePositions.add(new Position(startRow, startColumn));
                    startRow++;
                    startColumn++;
                }
            }

            if (blockablePositions.size() > 0) {
                ChessMan chessMan;
                for (Position ally : chessBoard.mAllChessMen.get(mColor)) {
                    chessMan = chessBoard.mGameState[ally.row][ally.column];
                    for (Position position : blockablePositions) {
                        if (chessMan.canAdvanceTo(position) &&
                                chessBoard.simulateAdvance(chessMan.mPosition, position)) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;

    }

    private boolean isAttackerCapturable(Position attackerPosition, ChessBoard chessBoard) {

        // Can the attacking piece be captured by an ally as long as the
        // movement of the ally does not expose the king to check.

        ChessMan chessMan;
        for (Position ally : chessBoard.mAllChessMen.get(mColor)) {
            chessMan = chessBoard.mGameState[ally.row][ally.column];
            if (chessMan.canAdvanceTo(attackerPosition) &&
                    chessBoard.simulateAdvance(chessMan.mPosition, attackerPosition)) {
                return true;
            }
        }
        return false;

    }

    public boolean isAnyChessManAdvanceable(ChessBoard chessBoard) {

        // Can any piece be moved without ending the king in check?

        ChessMan chessMan;
        for (Position ally : chessBoard.mAllChessMen.get(mColor)) {
            chessMan = chessBoard.mGameState[ally.row][ally.column];
            for (Position position : chessMan.mAdvances) {
                if (chessBoard.simulateAdvance(chessMan.mPosition, position)) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean canEvadeAttack(ChessBoard chessBoard) {

        // Can the king move to a square that is unchecked by opposing pieces. i.e. out of
        // line of sight of all opposing pieces

        if (mAdvances.size() > 0) {

            ArrayList<Integer> dangerousMoves = new ArrayList<>();

            ChessMan chessMan;
            for (Position enemy : chessBoard.mAllChessMen.get(mOtherColor)) {
                chessMan = chessBoard.mGameState[enemy.row][enemy.column];
                for (Position position : mAdvances) {
                    if (!dangerousMoves.contains(position.getPosition()) && chessMan.canAdvanceTo(position)) {
                        dangerousMoves.add(position.getPosition());
                    }
                    if (dangerousMoves.size() == mAdvances.size()) {
                        return false;
                    }
                }
            }
            return true;

        }
        return false;

    }

}
