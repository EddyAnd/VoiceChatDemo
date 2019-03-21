


package com.example.vociechatdemo.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vociechatdemo.R;
import com.example.vociechatdemo.adapter.RecorderAdapter;
import com.example.vociechatdemo.custom.AudioManager;
import com.example.vociechatdemo.custom.MediaManager;
import com.example.vociechatdemo.custom.RecorderButton;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    public ListView mlistview;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<>();
    private RecorderButton mRecorderButton;
     View mAnimView ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecorderButton = findViewById(R.id.recorder_button);
        recodeAudioAndStorageTask();
        mRecorderButton.setAudioFinishRecorderListener(new RecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds,filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mlistview.setSelection(mDatas.size() - 1);
            }
        });

        mlistview = findViewById(R.id.test);
        mAdapter = new RecorderAdapter(this,mDatas);
        mlistview.setAdapter(mAdapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAnimView != null){
                    mAnimView.setBackgroundResource(R.mipmap.adj);
                    mAnimView = null;
                }
                mAnimView = view.findViewById(R.id.recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable drawable = (AnimationDrawable) mAnimView.getBackground();
                drawable.start();
                MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });

    }


    /*
     * 申请录音权限*/
    private static final int RC_AUDIO_STORAGE = 22;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @AfterPermissionGranted(RC_AUDIO_STORAGE)
    public void recodeAudioAndStorageTask() {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getApplicationContext(), perms)) {

        } else {
            EasyPermissions.requestPermissions(this, "对讲需要使用麦克风，是否允许？",
                    RC_AUDIO_STORAGE, perms);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    public  class Recorder{
        float time ;
        String filePath;

        public Recorder(float time, String filePath) {
            this.time = time;
            this.filePath = filePath;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

}
