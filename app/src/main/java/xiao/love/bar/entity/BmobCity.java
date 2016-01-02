package xiao.love.bar.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by xiaoguochang on 2016/1/1.
 */
public class BmobCity extends BmobObject{
    private Integer cityID;
    private String city;
    private Integer provinceID;

    public BmobCity() {
        setTableName("City");
    }

    public Integer getCityID() {
        return cityID;
    }

    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }
}
