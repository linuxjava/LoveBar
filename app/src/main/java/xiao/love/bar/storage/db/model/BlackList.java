package xiao.love.bar.storage.db.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by guochang on 2015/9/11.
 * 黑名单
 */
public class BlackList {
    @DatabaseField(id = true, canBeNull = false)
    protected String username;

    public BlackList() {
    }

    public BlackList(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
