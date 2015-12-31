package xiao.love.bar.component.dialog;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.component.util.L;
import xiao.love.bar.storage.db.dao.CityDB;
import xiao.love.bar.storage.db.dao.ProvinceDB;
import xiao.love.bar.storage.db.dao.ZoneDB;
import xiao.love.bar.storage.db.model.City;
import xiao.love.bar.storage.db.model.Province;
import xiao.love.bar.storage.db.model.Zone;

/**
 * Created by xiaoguochang on 2015/12/26.
 * 身高选择框
 */
public class HeigthPickerDialog {
    private static HeigthPickerDialog sInstance;
    private Context mContext;
    private OptionsPickerView mPickerView;
    private ArrayList<String> mHeigthList;
    private ArrayList<ArrayList<String>> mHeigthList1;
    private Callback iCallback;


    public static synchronized HeigthPickerDialog getInstance(Context context){
        if(sInstance == null){
            sInstance = new HeigthPickerDialog(context);
        }

        return sInstance;
    }

    private HeigthPickerDialog(){

    }

    private HeigthPickerDialog(Context context) {
        mContext = context;
        mHeigthList = new ArrayList<>();
        mPickerView = new OptionsPickerView(context);

        for (int i=150; i<=210; i++){
            mHeigthList.add(i + "");
        }

        mPickerView.setPicker(mHeigthList);

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
                    iCallback.onOptionsSelect(mHeigthList.get(options1));

                    L.d("xiao1", mHeigthList.get(options1));
                }
            }
        });
    }

    public void show(){
        mPickerView.show();
    }

    public HeigthPickerDialog setSelectListener(Callback callback){
        iCallback = callback;
        return this;
    }

    public interface Callback{
        public void onOptionsSelect(String heigth);
    }
}
