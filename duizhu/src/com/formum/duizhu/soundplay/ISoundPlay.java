package com.formum.duizhu.soundplay;

import android.content.Context;

public interface ISoundPlay extends Runnable{
/**
 * ���������в���
 * @param context
 * @param sound
 */
    public void play(Context context,int sound);
}
