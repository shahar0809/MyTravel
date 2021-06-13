package com.example.mytravel;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/*
Service that will play background music while using the app.
 */
public class MusicService extends Service
{
    private MediaPlayer mPlayer;
    private final static int MAX_VOLUME = 100;
    public static int currVolume = MAX_VOLUME;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        mPlayer = MediaPlayer.create(this, R.raw.guitar_house);
        mPlayer.setLooping(true);

        final float volume = (float) (1 - (Math.log(MAX_VOLUME - currVolume) / Math.log(MAX_VOLUME)));
        mPlayer.setVolume(volume, volume);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mPlayer.start();
        return Service.START_NOT_STICKY;
    }

    public void onPause()
    {
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
        } else
        {
            mPlayer.start();
        }
    }


    @Override
    public void onDestroy()
    {
        mPlayer.stop();
        super.onDestroy();
    }
}
