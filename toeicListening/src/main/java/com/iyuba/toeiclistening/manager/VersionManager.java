package com.iyuba.toeiclistening.manager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.listener.AppUpdateCallBack;
import com.iyuba.toeiclistening.protocol.appUpdateRequest;
import com.iyuba.toeiclistening.protocol.appUpdateResponse;

/**
 * 鐗堟湰绠＄悊
 * 
 * @author lijingwei
 * 
 */
public class VersionManager {
	private static VersionManager instance;
	private static Context mContext;
	public int VERSION_CODE;
	private VersionManager(){};
	
	public static synchronized VersionManager Instace(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new VersionManager();
			//设置当前app版本号
			PackageManager manager=mContext.getPackageManager();
			PackageInfo packInfo;
			try {

				packInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
				if(packInfo!=null){
					instance.VERSION_CODE=packInfo.versionCode;
					
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	/**
	 * 妫?煡鏄惁鏈夋柊鐗堟湰
	 * @param version
	 * @param aucb AppUpdateCallBack aucb
	 */
	public void checkNewVersion(int version,final AppUpdateCallBack aucb){
		ClientSession.Instace().asynGetResponse(
				new appUpdateRequest(version),
				new IResponseReceiver() {
					
					@Override
					public void onResponse(BaseHttpResponse response, BaseHttpRequest request,
							int rspCookie) {
						// TODO Auto-generated method stub
						appUpdateResponse aur = (appUpdateResponse) response;
						if(aur.result.equals("NO")){
							// 鏈夋柊鐗堟湰
							if(aucb!=null){
//								1.2.3||http:// "+com.iyuba.toeiclistening.util.Constant.IYBHttpHead+"/soft.apk
								String data=aur.data.replace("||", "★");
								String[] appUpdateInfos=data.split("★");
								Log.e("^^^^^^^^^^^^", data);
								if(appUpdateInfos.length==2){
									Log.e("123123^^^^^^", "123");
									aucb.appUpdateSave(appUpdateInfos[0],appUpdateInfos[1]);
								}else{
									aucb.appUpdateFaild();
								}
							}
						}else{
							// 妫?煡澶辫触
							if(aucb!=null){
								aucb.appUpdateFaild();
							}
						}
					}
				}, null, null);		
	}
	
}
