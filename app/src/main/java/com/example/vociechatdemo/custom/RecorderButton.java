package com.example.vociechatdemo.custom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.vociechatdemo.R;

@SuppressLint("AppCompatCustomView")
public class RecorderButton extends Button implements AudioManager.AudioStateListener{


    private static final int MAX_HEIGHT = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;
    //是否触发longClick
    private boolean mReady = false;
    private DialogManager mDialogManager;
    private AudioManager mAudioMananger;
    private float mTime;
    public RecorderButton(Context context) {
        this(context,null);
    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        String dir = Environment.getExternalStorageState()+"/imooc_recorder_audios";
        mAudioMananger = AudioManager.getInstance(dir);
        mAudioMananger.setAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                //动态申请RECORDER_AUDIO权限
                mAudioMananger.prepareAudio();
                //verifyAudioPermissions(context.getA());
                return false;
            }
        });
    }

    //录音完成后的回调
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

         int action = event.getAction();
         int x = (int) event.getX();
         int y = (int) event.getY();
         switch (action){
                 case MotionEvent.ACTION_DOWN:
                     isRecording = true;
                     changeState(STATE_RECORDING);
                 break;
                 case MotionEvent.ACTION_MOVE:
                     if(isRecording){
                         if(wantToCancel(x,y)){
                           changeState(STATE_CANCEL);
                         }else {
                            changeState(STATE_RECORDING);
                         }
                     }
                 break;
                 case MotionEvent.ACTION_UP:
                     if(!mReady){
                         reSet();
                         return super.onTouchEvent(event);
                     }

                     if(!isRecording || mTime < 0.6f){
                         mDialogManager.tooShort();
                         mAudioMananger.cancel();
                         mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS,1300);
                     }else if(mCurrentState == STATE_RECORDING){
                        mAudioMananger.release();
                        if(mListener != null){
                            mListener.onFinish(mTime,mAudioMananger.getCurrentFilePath());
                        }
                  }else if(mCurrentState == STATE_CANCEL){
                     mAudioMananger.cancel();
                  }
                  reSet();
                 break;
                 default:
                 break;
         }

        return super.onTouchEvent(event);

    }

    /**
     * 恢复一些标志位
     */
    private void reSet() {
        mReady = false;
     isRecording = false;
     mTime = 0;
     changeState(STATE_NORMAL);
    }


    /**
     * 根绝x,y判断是否需要取消
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if(x < 0 || x > getWidth() ){

            return true;
        }

        if(y <  -MAX_HEIGHT || y > getHeight()+MAX_HEIGHT){
            return true;
        }

        return false;
    }

    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };

    /*
     * 申请录音权限*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void verifyAudioPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }else {

        }
    }



    private void changeState(int state) {
        if(mCurrentState != state){
            mCurrentState = state;
            switch (state){
                case STATE_NORMAL:
                    setBackground(getResources().getDrawable(R.drawable.btn_recorder_normal));
                    setText(R.string.str_recorder_normal);
                    mDialogManager.dismissDialog();
                    break;
                case STATE_RECORDING:

                    setBackground(getResources().getDrawable(R.drawable.btn_recording));
                    setText(R.string.str_recorder_playing);
                    if(isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_CANCEL:
                    setBackground(getResources().getDrawable(R.drawable.btn_recording));
                    setText(R.string.str_recorder_cancel);
                    mDialogManager.wantToCancel();
                    break;
                    default:
                        break;
            }
        }
    }
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS= 0X112;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                                mTime+= 0.1f;
                                mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();
                    break;
                case MSG_VOICE_CHANGED:
mDialogManager.updateVoiceLevel(mAudioMananger.getVoiceLevel(7));
                    break;

                case MSG_DIALOG_DIMISS:
                    mDialogManager.dismissDialog();
                    break;
                    default:
                        break;


            }
        }
    };

    @Override
    public void wellPrepared() {

    }
}
