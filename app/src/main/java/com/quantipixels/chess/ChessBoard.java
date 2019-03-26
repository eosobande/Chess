package com.quantipixels.chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.quantipixels.chess.ChessPieces.ChessMan;
import com.quantipixels.chess.ChessPieces.King;

import java.util.ArrayList;
import java.util.HashMap;

public class ChessBoard extends View {

    public RuleKeeper mRuleKeeper;

    public static int NO_OF_COLUMNS = 8, NO_OF_ROWS = NO_OF_COLUMNS,
            dark, light, selected, white, black, size;

    public static LightingColorFilter whiteFilter, blackFilter, selectedFilter;

    public static String BLACK = "BLACK", WHITE = "WHITE";

    private int[][] mColors = new int[NO_OF_ROWS][NO_OF_COLUMNS];
    public ChessMan[][] mGameState = new ChessMan[NO_OF_ROWS][NO_OF_COLUMNS];

    public ChessMan mActivePiece;

    public HashMap<String, ArrayList<Position>> mAllChessMen = new HashMap<>();

    public ChessBoard(Context context) {
        super(context);
        init();
    }

    public ChessBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChessBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {

        mAllChessMen.put(BLACK, new ArrayList<Position>());
        mAllChessMen.put(WHITE, new ArrayList<Position>());

        mRuleKeeper = new RuleKeeper(this);

        dark = ContextCompat.getColor(getContext(), R.color.dark);
        light = ContextCompat.getColor(getContext(), R.color.light);
        selected = ContextCompat.getColor(getContext(), R.color.select);

        white = ContextCompat.getColor(getContext(), R.color.white);
        black = ContextCompat.getColor(getContext(), R.color.black);

        whiteFilter = new LightingColorFilter(white, white);
        blackFilter = new LightingColorFilter(black, black);

        selectedFilter = new LightingColorFilter(selected, selected);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        size = canvas.getWidth() / NO_OF_COLUMNS;

        if (mGameState[0][0] == null) {
            createDefaultGameState();
        }

        for (int row = 0; row < mGameState[0].length; row++) {
            for (int column = 0; column < mGameState[1].length; column++) {
                canvas.save();
                canvas.translate(size * column, size * row);
                mGameState[row][column].setBackgroundColor(mColors[row][column]);
                mGameState[row][column].draw(canvas);
                canvas.restore();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ChessActivity.isOrientationInPortraitMode(getContext())) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        performClick();

        if (!mRuleKeeper.GAME_OVER) {

            Position newPosition = new Position((int) Math.floor(event.getY() / size),
                    (int) Math.floor(event.getX() / size));

            if (newPosition.row < NO_OF_ROWS && newPosition.column < NO_OF_COLUMNS) {

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        mActivePiece = mGameState[newPosition.row][newPosition.column];
                        if (mActivePiece.isChessMan() && mRuleKeeper.checkTurn()) {
                            mActivePiece.showMoves(mGameState);
                        }
                        break;

                    case MotionEvent.ACTION_UP: // drag to move

                        mActivePiece.hideMoves(mGameState);
                        if (mRuleKeeper.checkTurn() && mActivePiece.canAdvanceTo(newPosition)) {

                            if (simulateAdvance(mActivePiece.mPosition, newPosition)) {

                                if (mGameState[newPosition.row][newPosition.column].isChessMan() &&
                                        !mGameState[newPosition.row][newPosition.column].isShadowPawn()) {
                                    ArrayList<Position> pieces = mAllChessMen.get(mGameState[newPosition.row][newPosition.column].mColor);
                                    pieces.remove(mGameState[newPosition.row][newPosition.column].mPosition);
                                }

                                ArrayList<Position> pieces = mAllChessMen.get(mActivePiece.mColor);
                                pieces.remove(mActivePiece.mPosition);
                                pieces.add(newPosition);

                                mActivePiece.advance(mGameState, newPosition);
                                mRuleKeeper.isGameInStalemate();

                                mRuleKeeper.changeTurn();
                                refreshAllPossibleMoves();
                                mRuleKeeper.notifyCheckedOrCheckmate();

                            } else {

                                if (mRuleKeeper.isGameInCheck) {
                                    Snackbar.make(this, getContext().getString(R.string.protect, mRuleKeeper.playing), Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(this, R.string.self_check, Snackbar.LENGTH_LONG).show();
                                }

                            }

                        }

                        break;
                }

                invalidate();

            }

        }

        return true;

    }

    protected King findCorrespondingKing() {

        for (int row = 0; row < mGameState[0].length; row++) {
            for (ChessMan chessMan : mGameState[row]) {
                if (chessMan.isKing() && chessMan.mColor.equalsIgnoreCase(mRuleKeeper.playing)) {
                    return (King) chessMan;
                }
            }
        }

        return null;

    }

    public void createDefaultGameState() {

        for (int row = 0; row < mGameState[0].length; row++) {
            for (int column = 0; column < mGameState[0].length; column++) {

                if (row % 2 == 0) {
                    mColors[row][column] = column % 2 == 0 ? light : dark;
                } else {
                    mColors[row][column] = column % 2 == 0 ? dark : light;
                }

                mGameState[row][column] = mRuleKeeper.getChessManForPosition(row, column);
                mGameState[row][column].setPosition(new Position(row, column));

                if (mGameState[row][column].isChessMan()) {

                    mAllChessMen.get(mGameState[row][column].mColor)
                            .add(mGameState[row][column].mPosition);

                }

            }
        }

        refreshAllPossibleMoves();

    }

    private void refreshAllPossibleMoves() {
        for (int row = 0; row < mGameState[0].length; row++) {
            for (ChessMan chessMan : mGameState[row]) {
                chessMan.refreshMoves(mGameState, mAllChessMen);
            }
        }
    }

    public boolean simulateAdvance(Position from, Position to) {

        ChessMan attacker = mGameState[from.row][from.column];
        ChessMan victim = mGameState[to.row][to.column];

        boolean hasMoved = attacker.hasMoved;

        attacker.setPosition(to);
        if (!hasMoved) {
            attacker.hasMoved = true;
        }
        mGameState[to.row][to.column] = attacker;

        mGameState[from.row][from.column] = new ChessMan(getContext());
        mGameState[from.row][from.column].setPosition(from);

        refreshAllPossibleMoves();

        King king = findCorrespondingKing();
        boolean checked = king != null && !king.isChecked(mGameState, mAllChessMen.get(king.mOtherColor));

        mGameState[to.row][to.column] = victim;
        attacker.setPosition(from);

        if (!hasMoved) {
            attacker.hasMoved = false;
        }
        mGameState[from.row][from.column] = attacker;

        refreshAllPossibleMoves();

        return checked;

    }

}
