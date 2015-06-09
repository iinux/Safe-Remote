package data;

import general.Global;

public class TipString {
	public static String RUN_CMD_ERROR="命令执行错误！";
	public static String INPUT_USER_NAME="请输入用户名：";
	public static String INPUT_PASSWORD="请输入密码：";
	public static String LOGIN_SUCCESS="登录成功！";
	public static String LOGIN_FAIL="登录失败，请检查用户名和密码是否正确！";
	public static String KEY_UPDATE="密钥已更新！";
	public static String ONE_CLIENT_CONNECT="一个客户端已连接！";
	public static String ONE_CLIENT_DISCONNECT="一个客户端已断开连接！";
	public static String SERVER_START_SUCCESS="服务器启动成功！正在运行中......";
	public static String TIME_OUT="超时";
	public static String PASSWORD_ERROR_OVER="密码错误次数超过"+Global.passwordErrorCount+"次，"+Global.lockTime+"分钟内你将无法登录";
	public static String ADDED_TO_BACKLIST="被添加到黑名单中";
	public static String REMOVE_FROM_BLACKLIST="从黑名单中解除";
	public static String CHECK_SERVER_SIGNATURE="请验证服务器的指纹是否正确(如果你信任，请输入yes，否则请输入no)：";
}
