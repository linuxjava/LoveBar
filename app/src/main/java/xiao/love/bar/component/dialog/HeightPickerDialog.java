package xiao.love.bar.component.dialog;

import android.content.Context;
import android.widget.NumberPicker;

/**
 * Created by xiaoguochang on 2015/12/26.
 * 身高选择框
 */
public class HeightPickerDialog extends OneNumberPickerDialog{

    public HeightPickerDialog(Context context) {
        super(context);

        mMaterialDialog.setTitle("请选择身高");
        String[] province = new String[30];
        for(int i=0; i<30; i++){
            province[i] = i + "test";
        }

        setFirstPickerMinValue(150);
        setFirstPickerMaxValue(210);
        setFirstPickerValue(170);
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
