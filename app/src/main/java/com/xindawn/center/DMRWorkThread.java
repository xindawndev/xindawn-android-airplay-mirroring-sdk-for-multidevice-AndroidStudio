package com.xindawn.center;

import android.content.Context;
import android.content.Intent;

import com.xindawn.RenderApplication;
import com.xindawn.jni.PlatinumJniProxy;
import com.xindawn.mediacodec.VideoDecoderActivity;
import com.xindawn.util.CommonLog;
import com.xindawn.util.CommonUtil;
import com.xindawn.util.LogFactory;

public class DMRWorkThread extends Thread implements IBaseEngine{


	private static final CommonLog log = LogFactory.createLog();
	
	private static final int CHECK_INTERVAL = 30 * 1000; 
	
	private Context mContext = null;
	private boolean mStartSuccess = false;
	private boolean mExitFlag = false;
	
	private String mFriendName = "";
	private String mUUID = "";	
	private RenderApplication mApplication;
	
	public DMRWorkThread(Context context){
		mContext = context;
		mApplication = RenderApplication.getInstance();
	}
	
	public void  setFlag(boolean flag){
		mStartSuccess = flag;
	}
	
	public void setParam(String friendName, String uuid){
		mFriendName = friendName;
		mUUID = uuid;
		mApplication.updateDevInfo(mFriendName, mUUID);
	}
	
	public void awakeThread(){
		synchronized (this) {
			notifyAll();
		}
	}
	
	public void exit(){
		mExitFlag = true;
		awakeThread();
	}

	@Override
	public void run() {

		log.e("DMRWorkThread run...");
		
		while(true)
		{
			if (mExitFlag){
				stopEngine();
				break;
			}
			refreshNotify();
			synchronized(this)
			{				
				try
				{
					wait(CHECK_INTERVAL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}								
			}
			if (mExitFlag){
				stopEngine();
				break;
			}
		}
		
		log.e("DMRWorkThread over...");
		
	}
	
	public void refreshNotify(){
		//if (!CommonUtil.checkNetworkState(mContext)){
		//	return ;
		//}
		
		if (!mStartSuccess){
			stopEngine();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean ret = startEngine();
			if (ret){
				mStartSuccess = true;
			}
		}

	}
	
	@Override
	public boolean startEngine() {
		if (mFriendName.length() == 0){
			return false;
		}
		
		
        int p1[] = new int[]{47101};
        int p2[] = new int[]{7101};


        int ret = CommonUtil.RegisterMyService(mContext, "test", p1, p2);
        log.e("registerMyService : " +  p1[0] + " " +   p2[0]);

        if(ret <= 0)
        {
            return false;
        }



		ret = PlatinumJniProxy.startMediaRender_Java(mFriendName,p1[0],p2[0]);
		boolean result = (ret == 0 ? true : false);
		mApplication.setDevStatus(result);
		
		
		Intent intent = new Intent();
		//intent.setClass(mContext, AirPlayerActivity.class);
		intent.setClass(mApplication, VideoDecoderActivity.class);
		//intent.setClass(mContext, AirPlayerActivityUI.class);
        //DlnaMediaModelFactory.pushMediaModelToIntent(intent, mediaInfo);		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mApplication.startActivity(intent);
		
		return result;
	}

	@Override
	public boolean stopEngine() {
		
       CommonUtil.closeMDNS();
       log.e("close mDNS finish~");
		
		PlatinumJniProxy.stopMediaRender();
		mApplication.setDevStatus(false);
		return true;
	}

	@Override
	public boolean restartEngine() {
		setFlag(false);
		awakeThread();
		return true;
	}

}
