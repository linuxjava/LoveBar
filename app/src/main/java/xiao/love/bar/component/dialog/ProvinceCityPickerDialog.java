package xiao.love.bar.component.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by xiaoguochang on 2015/12/26.
 * 省和市选择框
 */
public class ProvinceCityPickerDialog extends TwoNumberPickerDialog{

    public ProvinceCityPickerDialog(Context context) {
        super(context);

        mMaterialDialog.setTitle("请设置省份");
        String[] province = new String[30];
        for(int i=0; i<30; i++){
            province[i] = i + "test";
        }

        setFirstPickerValues(province);
        setFirstPickerValue(10);

        setSecondPickerValues(new String[]{"0", "1", "2"});

        mFirstPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                setSecondPickerValues(new String[]{i1 + "", i1 + 1 + "", i1 + 2 + ""});
            }
        });
    }

    @Override
    protected void onClickPositive() {
        dismiss();
    }

    @Override
    protected void onClickNegative() {
        dismiss();
    }
}
