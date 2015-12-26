package xiao.love.bar.component.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import me.drakeet.materialdialog.MaterialDialog;
import xiao.love.bar.R;

/**
 * Created by xiaoguochang on 2015/12/24.
 * 只有一个NumberPicker的选择框
 */
public abstract class OneNumberPickerDialog {
    protected View mView;
    protected NumberPicker mFirstPicker;
    protected MaterialDialog mMaterialDialog;
    private View.OnClickListener iListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_n) {
                onClickNegative();
            } else if (view.getId() == R.id.btn_p) {
                onClickPositive();
            }
        }
    };

    protected abstract void onClickPositive();

    protected abstract void onClickNegative();

    public OneNumberPickerDialog(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_one_number_picker, null, false);
        mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setContentView(mView)
                .setCanceledOnTouchOutside(true)
                .setNegativeButton("取消", iListener)
                .setPositiveButton("确定", iListener);

        mFirstPicker = (NumberPicker) mView.findViewById(R.id.picker_first);
    }

    protected void setFirstPickerValues(String[] displayedValues) {
        mFirstPicker.setMinValue(0);
        mFirstPicker.setMaxValue(displayedValues.length - 1);
        mFirstPicker.setDisplayedValues(displayedValues);
    }

    protected void setFirstPickerValue(int value) {
        mFirstPicker.setValue(value);
    }

    protected void setFirstPickerMaxValue(int value) {
        mFirstPicker.setMaxValue(value);
    }

    protected void setFirstPickerMinValue(int value) {
        mFirstPicker.setMinValue(value);
    }

    public void show() {
        mMaterialDialog.show();
    }

    public void dismiss() {
        mMaterialDialog.dismiss();
    }

}
