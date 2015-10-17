package www.canterbury.ac.nz.mymusicplayer2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/10/12.
 */

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private String path;
    static int ISPLAYING = 1;
    static int ISPAUSED = 2;
    static int ISSTOPPED = 0;
    int playerStatus;
    int duration;
    int progress;
    Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends Binder implements IMusicService {
        public MusicService callGetMusicService() {
            return getMusicService();
        }
    }

    private MusicService getMusicService() {
        return this;
    }

    public void setMediaPath(String path) {
        this.path = path;
    }

    public void seekTo(int progress) {
        this.progress = progress;
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(this.progress);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            stop();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mediaPlayer.getDuration();
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.start();
                    addTimer();
                    playerStatus = ISPLAYING;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //TimeUnit.SECONDS.convert(duration - progress, TimeUnit.MILLISECONDS);
                progress = mediaPlayer.getCurrentPosition();
                Message msg = Message.obtain();
                msg.arg1 = duration;
                msg.arg2 = progress;
                MainActivity.handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 100, 500);
    }


    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playerStatus = ISPAUSED;
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playerStatus = ISPLAYING;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            playerStatus = ISSTOPPED;
            timer.cancel();
            timer.purge();
        }
    }
}

