package com.example.vociechatdemo.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vociechatdemo.R;

public class DialogManager {
    private Dialog mDialog;
    private ImageView mMicrophone;
    private ImageView mVoice;
    private TextView mShowState;
    private Context  mContext;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog(){
           mDialog = new Dialog(mContext, R.style.AudioDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recoder,null);
           mDialog.setContentView(view);

           mMicrophone = mDialog.findViewById(R.id.microphone);
           mVoice = mDialog.findViewById(R.id.voice);
           mShowState = mDialog.findViewById(R.id.show_state);

           mDialog.show();
    }

    public void recording(){
        if(mDialog != null && mDialog.isShowing()){
            mMicrophone.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mShowState.setVisibility(View.VISIBLE);

            mMicrophone.setImageResource(R.mipmap.cancel);
            mShowState.setText("松开手指，取消发送");
        }
    }

    public void  wantToCancel(){
        if(mDialog != null && mDialog.isShowing()){
            mMicrophone.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mShowState.setVisibility(View.VISIBLE);

            mMicrophone.setImageResource(R.mipmap.cancel);
            mShowState.setText("松开手指，取消发送");
        }
    }
    public void tooShort(){
        if(mDialog != null && mDialog.isShowing()){
            mMicrophone.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mShowState.setVisibility(View.VISIBLE);

            mMicrophone.setImageResource(R.mipmap.voice_to_short);
            mShowState.setText("录音时间过短");
        }
    }


    public void dismissDialog(){
        if(mDialog != null && mDialog.isShowing()){
           mDialog.dismiss();
           mDialog = null;
        }
    }

    public void updateVoiceLevel(int level){
        if(mDialog != null && mDialog.isShowing()){
            mMicrophone.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mShowState.setVisibility(View.VISIBLE);

           int resId = mContext.getResources().getIdentifier("v"+level,"mipmap",mContext.getPackageName());
           mVoice.setImageResource(resId);
        }
    }
}
