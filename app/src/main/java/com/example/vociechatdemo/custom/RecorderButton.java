package com.example.vociechatdemo.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.vociechatdemo.R;

@SuppressLint("AppCompatCustomView")
public class RecorderButton extends Button {


    private static final int MAX_HEIGHT = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;
    private DialogManager mDialogManager;
    public RecorderButton(Context context) {
        super(context);
        mDialogManager = new DialogManager(context);
    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO:真正显示应该在audio end prepared 以后
                mDialogManager.showRecordingDialog();
                isRecording = true;
                return false;
            }
        });
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
                  if(mCurrentState == STATE_RECORDING){

                  }else {

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
     isRecording = false;
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
}
