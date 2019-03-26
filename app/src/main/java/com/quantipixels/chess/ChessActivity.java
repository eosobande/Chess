package com.quantipixels.chess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ChessActivity extends AppCompatActivity {

    ChessBoard mChessBoard;
    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(isOrientationInPortraitMode(this) ? R.layout.activity_main_portrait :
                R.layout.activity_main_landscape);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mChessBoard.invalidate();
            }
        };

        mFilter = new IntentFilter("invalidate");

        mChessBoard = findViewById(R.id.chessboard);

        mChessBoard.mRuleKeeper.mPlayer1Name = findViewById(R.id.player1);
        mChessBoard.mRuleKeeper.mPlayer2Name = findViewById(R.id.player2);

        mChessBoard.mRuleKeeper.turn1 = findViewById(R.id.turn1);
        mChessBoard.mRuleKeeper.turn1.setText(R.string.turn);
        mChessBoard.mRuleKeeper.turn2 = findViewById(R.id.turn2);

        mChessBoard.mRuleKeeper.setPlayerNames();

    }

    public static boolean isOrientationInPortraitMode(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.reset) {
            if (!mChessBoard.mRuleKeeper.GAME_OVER) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.reset_dialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mChessBoard.mRuleKeeper.resetGame();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                mChessBoard.mRuleKeeper.resetGame();
            }
        }
        return super.onOptionsItemSelected(item);

    }
}
