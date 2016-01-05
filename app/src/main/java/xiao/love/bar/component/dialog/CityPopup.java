package xiao.love.bar.component.dialog;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.R;
import xiao.love.bar.component.util.DensityUtils;
import xiao.love.bar.storage.db.dao.CityDB;
import xiao.love.bar.storage.db.model.City;

/**
 * Created by xiaoguochang on 2016/1/4.
 * 城市切换弹出菜单样式，只有文字
 */
public class CityPopup {
    private ListPopupWindow mListPopupWindow;
    private Callback iCallback;
    private List<City> mCityList = new ArrayList<>();
    private List<String> mCityNameList = new ArrayList<>();

    public CityPopup(Context context, View anchorView) {
        mListPopupWindow = new ListPopupWindow(context);

        for (int cityID : CityZonePickerDialog.mCityIDArray) {
            City city = CityDB.getInstance(context).queryById(cityID);
            if (city != null) {
                //加载城市
                String cityName = city.getCity();
                String lastStr = cityName.substring(city.getCity().length() - 1);
                if ("市".equals(lastStr)) {
                    cityName = cityName.substring(0, city.getCity().length() - 1);
                    city.setCity(cityName);
                }
                mCityNameList.add(cityName);
                mCityList.add(city);
            }
        }

        mListPopupWindow.setAdapter(new ArrayAdapter<String>(context, R.layout.popup_text, mCityNameList));
        mListPopupWindow.setWidth(DensityUtils.dp2px(context, 100));
        mListPopupWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        //设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPopupWindow.setAnchorView(anchorView);
        mListPopupWindow.setModal(true);//设置是否是模式

        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();
                if (iCallback != null) {
                    City city = mCityList.get(position);
                    iCallback.onOptionsSelect(city.getCityID(), city.getCity());
                }
            }
        });
    }

    public void show() {
        mListPopupWindow.show();
    }

    public void setSelectListener(Callback callback) {
        iCallback = callback;
    }

    public interface Callback {
        void onOptionsSelect(int cityID, String cityName);
    }

//    public CityPopup(Context context, View anchorView) {
//        TextView vv = new TextView(context);
//        vv.setText("sadfasdfa");
//        View view = View.inflate(context, R.layout.popup_text, null);
//
//        mListView = (ListView) view.findViewById(R.id.listView);
//        List<String> list = new ArrayList<>();
//        list.add("12");
//        list.add("34");
//        list.add("56");
//        //PopupAdapter adapter = new PopupAdapter(context, R.layout.item_popup_text, list);
//        //mListView.setAdapter(adapter);
//
//        mPopup = new BubblePopup(context, view);
//        mPopup.anchorView(anchorView)
//                .showAnim(null)
//                .dismissAnim(null)
//                .setTopOffset(15)
//                .setBottomOffset(15)
//                .bubbleColor(ResTool.getColor(context, android.R.color.white));
//    }
//
//    public void show(){
//        mPopup.show();
//    }
//
//    private class PopupAdapter extends ArrayAdapter<String>{
//
//        public PopupAdapter(Context context, int resource, List<String> objects) {
//            super(context, resource, objects);
//        }
//
//        public PopupAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
//            super(context, resource, textViewResourceId, objects);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            return super.getView(position, convertView, parent);
//        }
//    }
}
