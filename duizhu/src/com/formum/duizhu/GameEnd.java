package com.formum.duizhu;

import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GameEnd extends Fragment {

    private IRecorder recorder = Recorder.getInstance();
    private HumanPlayer human = HumanPlayer.getInstance(GeneralPlay.getInstance());
    private ImageView win_loss;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.game_end, container, false);

	win_loss = (ImageView) v.findViewById(R.id.win_loss_game_end);
	
	boolean humanWin = recorder.getCurrentBanker().equals(human);
	if (humanWin) {
	    // 语音提示：哇，好厉害，尖打成了，你赢了 final MediaPlayer
	    System.out.println("@GameEnd ImageView win_loss--------->"+win_loss);
	    
	    
	    win_loss.setImageResource(R.drawable.win);
	    MediaPlayer mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.you_game_win);
	    mp.start();
	} else {
	    //语音提示：哎呀，对家，尖打成了 。 再来，谁怕谁 final MediaPlayer
	    System.out.println("@GameEnd ImageView win_loss--------->"+win_loss);
	    
	    
	    
	    win_loss.setImageResource(R.drawable.loss);
	    MediaPlayer mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.offensive_game_win);
	    mp.start();
	}

	win_loss.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		doRestart(getActivity());
	    }
	});
	return v;
    }

    public static void doRestart(Context c) {
	try {
	    // check if the context is given
	    if (c != null) {
		// fetch the packagemanager so we can get the default launch
		// activity
		// (you can replace this intent with any other activity if you
		// want
		PackageManager pm = c.getPackageManager();
		// check if we got the PackageManager
		if (pm != null) {
		    // create the intent with the default start activity for
		    // your application
		    Intent mStartActivity = pm.getLaunchIntentForPackage(c.getPackageName());
		    if (mStartActivity != null) {
			mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// create a pending intent so the application is
			// restarted after System.exit(0) was called.
			// We use an AlarmManager to call this intent in 100ms
			int mPendingIntentId = 223344;
			PendingIntent mPendingIntent = PendingIntent.getActivity(c, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
			// kill the application
			System.exit(0);
		    } else {
			Log.e("tag", "Was not able to restart application, mStartActivity null");
		    }
		} else {
		    Log.e("tag", "Was not able to restart application, PM null");
		}
	    } else {
		Log.e("tag", "Was not able to restart application, Context null");
	    }
	} catch (Exception ex) {
	    Log.e("tag", "Was not able to restart application");
	}
    }
}
