package com.mall.game.fivechess.index;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mall.view.R;

public class IndexActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	public static boolean playerFirst = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fivechess);
        findButtonsAndAddListeners();
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_newgame:
			startNewGame();
			break;
		case R.id.btn_exit:
			quitGame();
			break;
		}
		
	}
	private void findButtonsAndAddListeners(){
        View btnNewGame = this.findViewById(R.id.btn_newgame);
        View btnExit = this.findViewById(R.id.btn_exit);
        btnNewGame.setOnClickListener(this);
        btnExit.setOnClickListener(this);
	}
    private void startNewGame(){
    	Intent intent = new Intent(this,BackgammonActivity.class);
    	startActivity(intent);    	
    }
    
    private void quitGame(){
    	finish();
    }
}