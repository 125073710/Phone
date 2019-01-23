package telephony.internal.android.com.phoneui.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class RequestPermissionUtil {
	/**
	 *	@param permission Manifest.permission.***
	 * @return
	 */
	public static boolean requestPermission(Activity activity,String permission,int requestCode){
		//检查权限: 检查用户是不是已经授权
		int checkSelfPermission = ContextCompat.checkSelfPermission(activity.getApplication(), permission);
		//拒绝 : 检查到用户之前拒绝授权
		if(checkSelfPermission == PackageManager.PERMISSION_DENIED){
			//申请权限
			ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
		}else if(checkSelfPermission == PackageManager.PERMISSION_GRANTED){
			//已经授权
			return true;
		}else {
			ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
		}
		return false;
	}
}
