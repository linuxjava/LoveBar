package xiao.love.bar.storage.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
@DatabaseTable
public class Province {
    @DatabaseField(id = true, canBeNull = false)
    private int provinceID;
    @DatabaseField(canBeNull = false)
    private String province;

    public Province() {

    }

    public Province(int provinceID, String province) {
        this.provinceID = provinceID;
        this.province = province;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
