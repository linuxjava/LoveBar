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
 * 市和区选择框，城市id如下
 * 北京=1、上海=3、深圳=200、广州=190、
 * 西安=297、厦门=105、武汉=159、南京=63、杭州=78
 */
public class CityZonePickerDialog {
    private static CityZonePickerDialog sInstance;
    public static final int[] mCityIDArray = {1, 3, 200, 190, 297, 105, 159, 63, 78};
    private Context mContext;
    private OptionsPickerView mPickerView;
    private ArrayList<City> mCityList;
    private ArrayList<ArrayList<Zone>> mZoneList;
    private Callback iCallback;

    public static synchronized CityZonePickerDialog getInstance(Context context){
        if(sInstance == null){
            sInstance = new CityZonePickerDialog(context);
        }

        return sInstance;
    }

    private CityZonePickerDialog(){

    }

    private CityZonePickerDialog(Context context) {
        mContext = context;
        mCityList = new ArrayList<>();
        mZoneList = new ArrayList<>();
        mPickerView = new OptionsPickerView(context);

        for (int cityID : mCityIDArray) {
            City city = CityDB.getInstance(mContext).queryById(cityID);
            if(city != null) {
                //加载城市
                String cityName = city.getCity();
                String lastStr = cityName.substring(city.getCity().length() - 1);
                if ("市".equals(lastStr)) {
                    city.setCity(cityName.substring(0, city.getCity().length() - 1));
                }
                mCityList.add(city);

                //加载区县
                ArrayList<Zone> zoneList = (ArrayList<Zone>) ZoneDB.getInstance(mContext).queryByCityID(cityID);
                if (zoneList != null) {
                    mZoneList.add(zoneList);
                }
            }
        }

        mPickerView.setPicker(mCityList, mZoneList, true);

        mPickerView.setTitle("居住地");
        mPickerView.setCyclic(false, false, false);
        mPickerView.setCanceledOnTouchOutside(true);
        //设置默认选中的三级项目
        //监听确定选择按钮
        mPickerView.setSelectOptions(0, 0, 0);
        mPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                if(iCallback != null){
                    City city = mCityList.get(options1);
                    Zone zone = mZoneList.get(options1).get(options2);
                    iCallback.onOptionsSelect(city.getCity(), city.getCityID()
                    , zone.getZone(), zone.getZoneID());

                    L.d("xiao1", zone.getZone() + ":" + zone.getZoneID() + ":" +
                            city.getCity() + ":" + city.getCityID());
                }
            }
        });
    }

    public void show(){
        mPickerView.show();
    }

    public CityZonePickerDialog setSelectListener(Callback callback){
        iCallback = callback;
        return this;
    }

    public interface Callback{
        public void onOptionsSelect(String city, int cityID, String zone, int zoneID);
    }
}
