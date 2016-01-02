package xiao.love.bar.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by xiaoguochang on 2016/1/1.
 */
public class BmobZone extends BmobObject{
    private Integer zoneID;
    private String zone;
    private Integer cityID;

    public BmobZone() {
        setTableName("Town");
    }

    public Integer getZoneID() {
        return zoneID;
    }

    public void setZoneID(Integer zoneID) {
        this.zoneID = zoneID;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Integer getCityID() {
        return cityID;
    }

    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }
}
