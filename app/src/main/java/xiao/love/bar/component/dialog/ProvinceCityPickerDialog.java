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
 * 省和市选择框
 */
public class ProvinceCityPickerDialog {
    private static ProvinceCityPickerDialog sInstance;
    private Context mContext;
    private OptionsPickerView mPickerView;
    private ArrayList<Province> mProvinceList;
    private ArrayList<ArrayList<City>> mCityList;
    private Callback iCallback;


    public static synchronized ProvinceCityPickerDialog getInstance(Context context){
        if(sInstance == null){
            sInstance = new ProvinceCityPickerDialog(context);
        }

        return sInstance;
    }

    private ProvinceCityPickerDialog(){

    }

    private ProvinceCityPickerDialog(Context context) {
        mContext = context;
        mCityList = new ArrayList<>();
        mPickerView = new OptionsPickerView(context);

        //得到所有省
        mProvinceList = (ArrayList<Province>) ProvinceDB.getInstance(context).queryForAll();
        if (mProvinceList != null && mProvinceList.size() > 0) {
            for (int i = 0; i < mProvinceList.size(); i++) {
                int provinceID = mProvinceList.get(i).getProvinceID();
                int cityID;
                ArrayList<City> list;

                //是否是直辖市
                if(provinceID != 1 && provinceID != 2 &&  provinceID != 9 && provinceID != 27){
                    list = (ArrayList<City>) CityDB.getInstance(mContext).queryByProvinceID(provinceID);
                }else {
                    /**
                     * 因直辖市是没有city的，只有区和县，因此需要先得到改直辖市的cityID，再通过cityID
                     * 在ZoneDB中获取其区和县，然后将区或县作为直辖市的一个city
                     */
                    if(provinceID == 1){//北京
                        cityID = 1;
                    }else if(provinceID == 2){//天津
                        cityID = 2;
                    }else if(provinceID == 9){//上海
                        cityID = 3;
                    }else {//重庆
                        cityID = 4;
                    }

                    List<Zone> zoneList = ZoneDB.getInstance(mContext).queryByCityID(cityID);
                    list = new ArrayList<>();
                    for (Zone zone : zoneList) {
                        list.add(new City(zone.getZoneID(), zone.getZone(), provinceID));
                    }
                }

                mCityList.add(list);
            }
        }

        mPickerView.setPicker(mProvinceList, mCityList, true);

        mPickerView.setTitle("籍贯");
        mPickerView.setCyclic(false, false, false);
        mPickerView.setCanceledOnTouchOutside(true);
        //设置默认选中的三级项目
        //监听确定选择按钮
        mPickerView.setSelectOptions(0, 0, 0);
        mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                if(iCallback != null){
                    Province province = mProvinceList.get(options1);
                    City city = mCityList.get(options1).get(options2);
                    iCallback.onOptionsSelect(province.getProvince(), province.getProvinceID(),
                            city.getCity(), city.getCityID());

                    L.d("xiao1", province.getProvince() + ":" + province.getProvinceID() + ":" +
                            city.getCity() + ":" + city.getCityID());
                }
            }
        });
    }

    public void show(){
        mPickerView.show();
    }

    public ProvinceCityPickerDialog setSelectListener(Callback callback){
        iCallback = callback;
        return this;
    }

    public interface Callback{
        public void onOptionsSelect(String province, int provinceID, String city, int cityID);
    }
}
