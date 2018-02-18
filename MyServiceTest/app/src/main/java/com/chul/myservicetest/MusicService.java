package com.chul.myservicetest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by leeyc on 2018. 2. 19..
 */

public class MusicService extends Service {

    MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("abcTest","onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abcTest","onCreate");
    }

    @Override
    public void onDestroy() {

        Log.d("abcTest","onDestroy");
        mp.stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("abcTest","onStartCommand");
        mp = MediaPlayer.create(this, R.raw.sampleaudio);
        mp.setLooping(true);
        mp.start();
        return super.onStartCommand(intent, flags, startId);

    }

}
