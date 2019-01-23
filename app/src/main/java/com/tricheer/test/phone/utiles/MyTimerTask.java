package com.tricheer.test.phone.utiles;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask {
	private String TAG ="MyTimerTask";
	
	private Timer timer =null;  
	private long currentSecond = 0;//当前毫秒数
	private TextView tv;
	private MyTimer mMyTimer =null;
	
	public MyTimerTask(TextView tv){
		this.tv = tv;
		
	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				tv.setText(getFormatHMS(currentSecond));
				break;

		
			}
			
		}
	};
	
	
	
	public class MyTimer extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			currentSecond = currentSecond + 999;
	        Message message = new Message( );
	        message.what = 1;
	        handler.sendMessage(message);
		}
		
	}
	
	
	
	public void startTimer(){
		stopTimer();
		if(timer == null){
			timer = new Timer( );
		}
		 if(mMyTimer == null){
			 mMyTimer = new MyTimer();
		 }
		 timer.schedule(mMyTimer,0,1000);
		
	}
	
	
	public void stopTimer(){

		   if(timer!=null){
	            timer.cancel();
	            timer =null;
	    }
		   if(mMyTimer !=null){
			   mMyTimer.cancel();
			   mMyTimer = null;
		   }
		Log.e(TAG,"stopTimer");
		currentSecond =0;
		tv.setText(getFormatHMS(currentSecond));
	}		
	public static String getFormatHMS(long time){
	    time=time/1000;//总秒数
	    int s= (int) (time%60);//秒
	    int m= (int) (time/60);//分
	    return String.format("%d:%02d",m,s);
	}
}
