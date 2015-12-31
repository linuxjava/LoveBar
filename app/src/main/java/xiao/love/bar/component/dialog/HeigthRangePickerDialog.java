package xiao.love.bar.component.dialog;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;

import xiao.love.bar.component.util.L;

/**
 * Created by xiaoguochang on 2015/12/26.
 * 身高范围选择框
 */
public class HeigthRangePickerDialog {
    private static HeigthRangePickerDialog sInstance;
    private Context mContext;
    private OptionsPickerView mPickerView;
    private ArrayList<String> mMinHeigthList;
    private ArrayList<ArrayList<String>> mMaxHeigthList;
    private Callback iCallback;


    public static synchronized HeigthRangePickerDialog getInstance(Context context){
        if(sInstance == null){
            sInstance = new HeigthRangePickerDialog(context);
        }

        return sInstance;
    }

    private HeigthRangePickerDialog(){

    }

    private HeigthRangePickerDialog(Context context) {
        mContext = context;
        mMinHeigthList = new ArrayList<>();
        mMaxHeigthList = new ArrayList<>();
        mPickerView = new OptionsPickerView(context);

        for (int i=150; i<=210; i++){
            mMinHeigthList.add(i + "");
            ArrayList<String> list = new ArrayList<>();
            for (int j=i; j<=210; j++){
                list.add(j + "");
            }
            mMaxHeigthList.add(list);
        }

        mPickerView.setPicker(mMinHeigthList, mMaxHeigthList, true);

        mPickerView.setTitle("身高(cm)");
        mPickerView.setCyclic(false, false, false);
        mPickerView.setCanceledOnTouchOutside(true);
        //设置默认选中的三级项目
        //监听确定选择按钮
        mPickerView.setSelectOptions(0, 0, 0);
        mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                if(iCallback != null){
                    iCallback.onOptionsSelect(mMinHeigthList.get(options1), mMinHeigthList.get(options2));

                    L.d("xiao1", mMinHeigthList.get(options1) + "-" + mMaxHeigthList.get(options1).get(options2));
                }
            }
        });
    }

    public void show(){
        mPickerView.show();
    }

    public HeigthRangePickerDialog setSelectListener(Callback callback){
        iCallback = callback;
        return this;
    }

    public interface Callback{
        public void onOptionsSelect(String min, String max);
    }
}
