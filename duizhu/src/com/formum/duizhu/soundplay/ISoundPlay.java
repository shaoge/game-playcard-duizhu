package com.formum.duizhu.soundplay;

import android.content.Context;

public interface ISoundPlay extends Runnable{
/**
 * 在上下文中播放
 * @param context
 * @param sound
 */
    public void play(Context context,int sound);
}
