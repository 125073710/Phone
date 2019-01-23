package telephony.internal.android.com.phoneui.permission;


public class Permission {
	/**
	 * 请求码
	 */
	public int requestCode;
	/**
	 *  Manifest.permission.***
	 */
	public String permission;
	/**
	 * 是否有权限
	 */
	public boolean hasPermission;

	public Permission(int requestCode, String permission, boolean hasPermission) {
		this.requestCode = requestCode;
		this.permission = permission;
		this.hasPermission = hasPermission;
	}
}
