
/**************************************************************************************
* [Project]
*       MyProgressDialog
* [Package]
*       com.lxd.widgets
* [FileName]
*       CustomProgressDialog.java
* [Copyright]
*       Copyright 2012 LXD All Rights Reserved.
* [History]
*       Version          Date              Author                        Record
*--------------------------------------------------------------------------------------
*       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
**************************************************************************************/
	
package com.utils;



import java.util.Timer;
import java.util.TimerTask;

import com.example.bluetooth.le.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;



public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	public static final String TAG = "ProgressDialog";
    private long mTimeOut = 0;// 榛樿timeOut涓?鍗虫棤闄愬ぇ
    private OnTimeOutListener mTimeOutListener = null;// timeOut鍚庣殑澶勭悊鍣?
    private Timer mTimer = null;// 瀹氭椂鍣?
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(mTimeOutListener != null){
                mTimeOutListener.onTimeOut(CustomProgressDialog.this);
            }
        }
    };
	
	
	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
//        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 锟斤拷锟斤拷
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitle(String strTitle){
    	((TextView) customProgressDialog.findViewById(R.id.title)).setText(strTitle);
    	return customProgressDialog;
    }
    public void setTitle(int strTitle){
    	((TextView) customProgressDialog.findViewById(R.id.title)).setText(strTitle);
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 锟斤拷示锟斤拷锟斤拷
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
    
    public void setTimeOut(long t, OnTimeOutListener timeOutListener) {
        mTimeOut = t;
        if (timeOutListener != null) {
            this.mTimeOutListener = timeOutListener;
        }
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mTimer != null) {

            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (mTimeOut != 0) {
            mTimer = new Timer();
            TimerTask timerTast = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                //    dismiss();
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(timerTast, mTimeOut);
        }

    }

    /**
     * 閫氳繃闈欐?Create鐨勬柟寮忓垱寤轰竴涓疄渚嬪璞?
     * 
     * @param context
     * @param time    
     *                 timeout鏃堕棿闀垮害
     * @param listener    
     *                 timeOutListener 瓒呮椂鍚庣殑澶勭悊鍣?
     * @return MyProgressDialog 瀵硅薄
     */
    public static CustomProgressDialog createProgressDialog(Context context,
            long time, OnTimeOutListener listener) {
//    	CustomProgressDialog progressDialog = new CustomProgressDialog(context);
    	customProgressDialog  = new CustomProgressDialog(context,R.style.CustomProgressDialog);
    	customProgressDialog.setContentView(R.layout.customprogressdialog);
    	customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        if (time != 0) {
        	customProgressDialog.setTimeOut(time, listener);
        }
        return customProgressDialog;
    }

    /**
     * 
     * 澶勭悊瓒呮椂鐨勭殑鎺ュ彛
     *
     */
    public interface OnTimeOutListener {
        
        /**
         * 褰損rogressDialog瓒呮椂鏃惰皟鐢ㄦ鏂规硶
         */
        abstract public void onTimeOut(CustomProgressDialog dialog);
    }
}
