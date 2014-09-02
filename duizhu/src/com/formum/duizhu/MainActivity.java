package com.formum.duizhu;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.ICardDeck;
import com.formum.duizhu.soundplay.BackGroundSoundPlay;
import com.formum.duizhu.soundplay.ISoundPlay;
import com.formum.duizhu.soundplay.SoundPlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ICardDeck cardDeck=CardDeck.getInstance();
	private Button endBtn;
	private MediaPlayer mp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//随机放点背景音乐听
		
		Random r=new Random();
		int key = r.nextInt(6);
		ISoundPlay soundPlay=BackGroundSoundPlay.getInstance();
		switch (key) {
		case 0:
		   soundPlay.play(this, R.raw.music_anne);
		    break;
		case 1:
		    soundPlay.play(this, R.raw.music_children_memory);
		    break;
		case 2:
		    soundPlay.play(this, R.raw.music_hometown_landview);
		    break;
		case 3:
		    soundPlay.play(this, R.raw.music_pipa_tune);
		    break;
		case 4:
		    soundPlay.play(this, R.raw.music_qingsong);
		    break;
		case 5:
		    soundPlay.play(this, R.raw.music_xinqin);
		    break;
		default:
		    break;
		}
		new Thread(soundPlay).start();
		
		    //第一步，随机显示一个宝宝给老人家看	
		    Babies babies = new Babies(); 
		    getFragmentManager().beginTransaction().replace(R.id.center, babies).commit(); //加入碎片——宝宝开头
		    //第二步，点图，开始抓牌，进入一场游戏，显示不玩了

		//随时，不玩了按钮
		endButton();

	}
	


/**
 * 不玩了
 */
	private void endButton() {
		endBtn=(Button) findViewById(R.id.end);
		endBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int icon=randomBabiesImage();
				new AlertDialog.Builder(MainActivity.this)  
                .setIcon(icon)  
                .setTitle("真的不玩了,点“确定”退出")  
                .setPositiveButton("确定",  
                        new DialogInterface.OnClickListener() {  
                            @Override  
                            public void onClick(DialogInterface dialog,  
                                    int which) { 

                            	cardDeck.reNewCards();//牌套更新
                            	//Dalvik VM的本地方法退出：
                            	android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                            	System.exit(0); //常规java、c#的标准退出法，返回值为0代表正常退出
                            }  
                        }).setNegativeButton("取消", null).create()  
                .show();
				
			}
		});
	}

	/**
	 * 随机显示宝贝图片号
	 */
private int randomBabiesImage(){
	int result=0;
	Random random=new Random();
	int key=random.nextInt(3);
	switch (key) {
	case 0:
        result=R.drawable.shuangshuang2;
		break;
	case 1:
        result=R.drawable.peipei2;
		break;
	case 2:
        result=R.drawable.chuchu2;	
			break;
	default:
		break;
	}
	return result;
}



	public MediaPlayer getMp() {
	    return mp;
	}



	public void setMp(MediaPlayer mp) {
	    this.mp = mp;
	}



}
