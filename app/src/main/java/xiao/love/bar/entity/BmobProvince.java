package xiao.love.bar.entity;

import com.j256.ormlite.field.DatabaseField;

import cn.bmob.v3.BmobObject;

/**
 * Created by xiaoguochang on 2016/1/1.
 */
public class BmobProvince extends BmobObject{
    private Integer provinceID;
    private String province;

    public BmobProvince() {
        setTableName("Province");
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
