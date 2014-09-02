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
		
		//����ŵ㱳��������
		
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
		
		    //��һ���������ʾһ�����������˼ҿ�	
		    Babies babies = new Babies(); 
		    getFragmentManager().beginTransaction().replace(R.id.center, babies).commit(); //������Ƭ����������ͷ
		    //�ڶ�������ͼ����ʼץ�ƣ�����һ����Ϸ����ʾ������

		//��ʱ�������˰�ť
		endButton();

	}
	


/**
 * ������
 */
	private void endButton() {
		endBtn=(Button) findViewById(R.id.end);
		endBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int icon=randomBabiesImage();
				new AlertDialog.Builder(MainActivity.this)  
                .setIcon(icon)  
                .setTitle("��Ĳ�����,�㡰ȷ�����˳�")  
                .setPositiveButton("ȷ��",  
                        new DialogInterface.OnClickListener() {  
                            @Override  
                            public void onClick(DialogInterface dialog,  
                                    int which) { 

                            	cardDeck.reNewCards();//���׸���
                            	//Dalvik VM�ı��ط����˳���
                            	android.os.Process.killProcess(android.os.Process.myPid());    //��ȡPID
                            	System.exit(0); //����java��c#�ı�׼�˳���������ֵΪ0���������˳�
                            }  
                        }).setNegativeButton("ȡ��", null).create()  
                .show();
				
			}
		});
	}

	/**
	 * �����ʾ����ͼƬ��
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
