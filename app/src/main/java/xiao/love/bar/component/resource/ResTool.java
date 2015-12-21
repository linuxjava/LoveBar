package xiao.love.bar.component.resource;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by xiaoguochang on 2015/12/14.
 */
public class ResTool {
    public static String getString(Context context, int resId){
        return context.getString(resId);
    }


    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout", paramContext.getPackageName());
    }

    public static int getStringId(Context context, String resourcesName) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(resourcesName, "string", context.getPackageName());
        if (id == 0) {
            throw new RuntimeException("资源文件读取不到！");
        }
        return id;
    }

    public static int getAnimId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "anim", paramContext.getPackageName());
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
    }

    public static int getRawId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "raw", paramContext.getPackageName());
    }

    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "style", paramContext.getPackageName());
    }

    public static int getDiyId(Context paramContext, String name, String type) {
        return paramContext.getResources().getIdentifier(name, type, paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "color", paramContext.getPackageName());
    }
}
