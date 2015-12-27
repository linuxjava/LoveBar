package xiao.love.bar.storage.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
@DatabaseTable
public class City {
    @DatabaseField(id = true, canBeNull = false)
    private int cityID;
    @DatabaseField(canBeNull = false)
    private String city;
    @DatabaseField(canBeNull = false)
    private int provinceID;

    public City() {

    }

    public City(int cityID, String city, int provinceID) {
        this.cityID = cityID;
        this.city = city;
        this.provinceID = provinceID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }
}
