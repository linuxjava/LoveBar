package xiao.love.bar.fragment.main;

import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.component.dialog.CityZonePickerDialog;
import xiao.love.bar.component.dialog.HeigthPickerDialog;
import xiao.love.bar.component.dialog.HeigthRangePickerDialog;
import xiao.love.bar.component.dialog.PhotoPickerManager;
import xiao.love.bar.component.dialog.ProvinceCityPickerDialog;
import xiao.love.bar.fragment.BaseFragment;
import xiao.love.bar.mvppresenter.BaseFragmentPresenter;

/**
 * Created by xiaoguochang on 2015/12/15.
 */
public class HomeFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidgets() {

    }

    @OnClick(R.id.btn_test)
    public void onClickTest(){
        ProvinceCityPickerDialog.getInstance(mContext).
                setSelectListener(new ProvinceCityPickerDialog.Callback() {
                    @Override
                    public void onOptionsSelect(String province, int provinceID, String city, int cityID) {

                    }
                }).show();
    }

    @OnClick(R.id.btn_test1)
    public void onClickTest1(){
        CityZonePickerDialog.getInstance(mContext).
                setSelectListener(new CityZonePickerDialog.Callback() {
                    @Override
                    public void onOptionsSelect(String city, int cityID, String zone, int zoneID) {

                    }
                }).show();
    }

    @OnClick(R.id.btn_test2)
    public void onClickTest2(){
        HeigthPickerDialog.getInstance(mContext).
                setSelectListener(new HeigthPickerDialog.Callback() {
                    @Override
                    public void onOptionsSelect(String heigth) {

                    }
                }).show();
    }

    @OnClick(R.id.btn_test3)
    public void onClickTest3(){
        HeigthRangePickerDialog.getInstance(mContext).
                setSelectListener(new HeigthRangePickerDialog.Callback() {
                    @Override
                    public void onOptionsSelect(String min, String max) {

                    }
                }).show();
    }

    @OnClick(R.id.btn_test4)
    public void onClickTest4(){
        PhotoPickerManager.getInstance(mContext).showPickerDialog();
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }
}
