package com.formum.duizhu;

import java.util.Random;

import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.soundplay.ISoundPlay;
import com.formum.duizhu.soundplay.SoundPlay;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Babies extends Fragment {

    private ImageView baby_images;

    // 此碎片用于显示一个宝贝，并做为开始按键
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.babies, container, false);

	baby_images = (ImageView) v.findViewById(R.id.baby_image);
	randomBabyShow(baby_images);

	baby_images.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {

		// 显示一堆牌

		
		FetchCards fetchCards = new FetchCards();
		getFragmentManager().beginTransaction().replace(R.id.center, fetchCards, "fetchCards").commit();
		
		//语音提示：开始玩牌
		
		ISoundPlay soundPlay=SoundPlay.getInstance();
		soundPlay.play(getActivity(), R.raw.start_playing);
		new Thread(SoundPlay.getInstance()).start();
	    }
	});

	return v;

    }

    private void randomBabyShow(ImageView baby_images) {
	Random random = new Random();
	int key = random.nextInt(3);
	switch (key) {
	case 0:
	    baby_images.setImageResource(R.drawable.shuangshuang1);
	    break;
	case 1:
	    baby_images.setImageResource(R.drawable.peipei1);
	    break;
	case 2:
	    baby_images.setImageResource(R.drawable.chuchu1);
	    break;
	default:
	    break;
	}
    }
}
