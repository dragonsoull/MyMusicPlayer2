package www.canterbury.ac.nz.mymusicplayer2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sb.setMax(msg.arg1);
            sb.setProgress(msg.arg2);
        }
    };

    private IMusicService iMusicService;
    private MusicService musicService;
    private EditText etPath;
    private ImageButton ib;
    private static SeekBar sb;
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = (TextView) findViewById(R.id.tv_time);
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, new MyConn(), BIND_AUTO_CREATE);
        etPath = (EditText) findViewById(R.id.et_path);
        ib = (ImageButton) findViewById(R.id.play_pause);
        sb = (SeekBar) findViewById(R.id.sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seekTo(sb.getProgress());
            }
        });
    }

    public void playAndPause(View view) {
        String path = etPath.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        musicService.setMediaPath(path);
        if (musicService.playerStatus == MusicService.ISSTOPPED) {
            musicService.play();
            ib.setImageResource(R.mipmap.pause64x64);
        } else if (musicService.playerStatus == MusicService.ISPLAYING) {
            musicService.pause();
            ib.setImageResource(R.mipmap.play64x64);
        } else {
            musicService.resume();
            ib.setImageResource(R.mipmap.pause64x64);
        }
    }

    public void forward(View view) {
        musicService.seekTo(sb.getProgress() + 5000);
    }

    public void backward(View view) {
        int currentProgress = sb.getProgress();
        if (currentProgress - 5000 < 0) {
            musicService.seekTo(0);
        } else {
            musicService.seekTo(sb.getProgress() - 5000);
        }
    }

    public void stop(View view) {
        musicService.stop();
        sb.setProgress(0);
        ib.setImageResource(R.mipmap.play64x64);
    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMusicService = (IMusicService) service;
            musicService = iMusicService.callGetMusicService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
