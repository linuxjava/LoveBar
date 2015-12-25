package com.gitonway.lee.niftymodaldialogeffects.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

/**
 * Created by xiaoguochang on 2015/12/24.
 * 年月日
 */
public class YearMonthDayDialog {
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private View mView;
    private NumberPicker mYearPicker;
    private NumberPicker mMonthPicker;
    private NumberPicker mDayPicker;
    private OnClickListener mDataListener;
    private DialogInterface.OnClickListener iListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                if(mDataListener != null) {
                    mDataListener.onClick(mYearPicker.getValue(), mMonthPicker.getValue(), mDayPicker.getValue());
                }
            } else if (which == AlertDialog.BUTTON_NEGATIVE) {

            }
        }
    };

    public  YearMonthDayDialog(Context context, int year, int month, int day) {
        mView = LayoutInflater.from(context).inflate(R.layout.year_month_day_pick_layout, null, false);
        mBuilder = new AlertDialog.Builder(context)
                .setView(mView)
                .setTitle("请选择日期")
                .setPositiveButton("确定", iListener)
                .setNegativeButton("取消", iListener);
        mAlertDialog = mBuilder.create();

        init(year, month, day);
    }

    private void init(int year, int month, int day) {
        mYearPicker = (NumberPicker) mView.findViewById(R.id.year);
        mMonthPicker = (NumberPicker) mView.findViewById(R.id.month);
        mDayPicker = (NumberPicker) mView.findViewById(R.id.day);

        mYearPicker.setMinValue(1980);
        mYearPicker.setMaxValue(1999);
        mYearPicker.setValue(year);
        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);
        mMonthPicker.setValue(month);
        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(31);
        mDayPicker.setValue(day);
    }

    public void setPositiveListener(OnClickListener listener){
        mDataListener = listener;
    }

    public void show() {
        mAlertDialog.show();
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    public interface OnClickListener{
        void onClick(int year, int month, int day);
    }
}
