package com.gitonway.lee.niftymodaldialogeffects.lib;


import android.content.Context;
import android.view.View;

/**
 * Created by guochang on 2015/8/11.
 * 提示对话框和loading对话框工具类
 */
public class DialogTool {
    private static NiftyAlertDialog alertDialog;
    private static NiftyLoadingDialog loadingDialog;

    public static void showAlertDialog(Context context) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }

        alertDialog = new NiftyAlertDialog(context);

        alertDialog
                .withTitle("测试")
                .withMessage("确定退出吗")
                .withIcon(null)
                .isCancelable(false)
                .isCancelableOnTouchOutside(false)
                .withDuration(300)
                .withEffect(Effectstype.Fadein)
                .withMultiButtonText("取消", "确定")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    public static void dismissAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public static void showLoadingDialog(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        loadingDialog = new NiftyLoadingDialog(context);
        loadingDialog.isMaterial(true).setMsg("加载中...").isCancelable(false).show();
    }

    public static void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}