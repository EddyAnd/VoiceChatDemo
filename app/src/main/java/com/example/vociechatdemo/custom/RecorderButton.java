package com.example.vociechatdemo.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class RecorderButton extends Button {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;

    public RecorderButton(Context context) {
        super(context);

    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

         int action = event.getAction();
         int x = (int) event.getX();
         int y = (int) event.getY();
         switch (action){
                 case MotionEvent.ACTION_DOWN:
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
    }


    /**
     * 根绝x,y判断是否需要取消
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        return false;
    }

    private void changeState(int stateNormal) {
    }
}