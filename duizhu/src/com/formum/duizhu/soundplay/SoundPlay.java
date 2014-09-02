package com.formum.duizhu.soundplay;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class SoundPlay implements ISoundPlay {

    private static volatile SoundPlay INSTANCE = null;
    private MediaPlayer mMediaPlayer;
    private Context context;
    private int sound=0;
    private SoundPlay() {

    }
    public static SoundPlay getInstance() {
	if (INSTANCE == null) {
	    synchronized (SoundPlay.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new SoundPlay();
		}
	    }
	}
	return INSTANCE;
    }
    @Override
    public void run() {
	mMediaPlayer = MediaPlayer.create(context, sound);
	mMediaPlayer.start();
	mMediaPlayer.setVolume(10, 10);
	
	
	mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
		    mp.release();
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
