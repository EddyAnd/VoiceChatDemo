package com.example.vociechatdemo.custom;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepared = true;
    private static  AudioManager mInstance;
    private AudioManager(String dir){
        mDir = dir;
    }

    public AudioStateListener mListener;

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    public interface AudioStateListener{
        void wellPrepared();
    }

    public void setAudioStateListener(AudioStateListener audioStateListener){
        mListener = audioStateListener;
    }

    public static AudioManager getInstance(String dir){
        if(mInstance == null){
            synchronized (AudioManager.class){
                if(mInstance == null){
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return  mInstance;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void prepareAudio(){
        isPrepared = false;
        mCurrentFilePath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+generateFileName();
        Log.e("====mCurrentFilePath===",mCurrentFilePath);
        mMediaRecorder = new MediaRecorder();
        try {
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频的编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("====11111==e==",e.toString());
        }


        //准备结束
        isPrepared = true;

        if(mListener != null){
            mListener.wellPrepared();
        }


    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    public int getVoiceLevel(int maxLevel){
        if(isPrepared && mMediaRecorder != null){
            return (maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1);
        }
        return 1;
    }


    public void release(){
        if(mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void cancel(){
        release();
        if(mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }


}
