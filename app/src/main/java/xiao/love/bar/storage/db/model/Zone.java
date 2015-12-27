package xiao.love.bar.storage.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
@DatabaseTable
public class Zone {
    @DatabaseField(id = true, canBeNull = false)
    private int zoneID;
    @DatabaseField(canBeNull = false)
    private String zone;
    @DatabaseField(canBeNull = false)
    private int cityID;

    public Zone() {

    }

    public Zone(int zoneID, String zone, int cityID) {
        this.zoneID = zoneID;
        this.zone = zone;
        this.cityID = cityID;
    }

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
