package xiao.love.bar.component.util;

import android.util.Log;

//Logcat统一管理类
public class L
{

	private L()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
	private static final String TAG = "speechgame";

	public static void d(String msg)
	{
		if (isDebug)
			Log.d(TAG, new Exception().getStackTrace()[1].getClassName()
                    + ":" + new Exception().getStackTrace()[1].getMethodName()
                    + "---" + msg);
	}

	public static void d(String tag, String msg)
	{
		if (isDebug)
			Log.d(tag, new Exception().getStackTrace()[1].getClassName()
                    + ":" + new Exception().getStackTrace()[1].getMethodName()
                    + "---" + msg);
	}
}