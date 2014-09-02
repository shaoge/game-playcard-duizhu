package com.formum.duizhu.soundplay;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class BackGroundSoundPlay implements ISoundPlay {

    private static volatile BackGroundSoundPlay INSTANCE = null;


    private MediaPlayer mMediaPlayer;
    private Context context;
    private int sound=0;
    private BackGroundSoundPlay() {

    }
    public static BackGroundSoundPlay getInstance() {
	if (INSTANCE == null) {
	    synchronized (BackGroundSoundPlay.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new BackGroundSoundPlay();
		}
	    }
	}
	return INSTANCE;
    }
    @Override
    public void run() {
	// TODO Auto-generated method stub
	mMediaPlayer = MediaPlayer.create(context, sound);
	mMediaPlayer.setLooping(true);
	mMediaPlayer.start();
	mMediaPlayer.setVolume(1, 1);

	mMediaPlayer.setOnErrorListener(new OnErrorListener() {
	    @Override
	    public boolean onError(MediaPlayer mp, int what, int extra) {
		     mp.reset();
		     mp.start();
		     Log.i("tag", "出错了，清空对象");    
		return false;
	    }
	});
	
    }

    @Override
    public void play(Context context,int sound) {
	// TODO Auto-generated method stub
	this.context=context;
	this.sound=sound;

    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public int getSound() {
        return sound;
    }
    public void setSound(int sound) {
        this.sound = sound;
    }



}
