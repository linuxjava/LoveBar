package xiao.love.bar.memory.db.model;

import com.easemob.chat.EMContact;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by guochang on 2015/9/10.
 * 联系人
 */
@DatabaseTable(tableName = "contact")
public class Contact extends EMContact {
    @DatabaseField(id = true, canBeNull = false)
    protected String username;
    @DatabaseField(canBeNull = false)
    protected String nick;
    @DatabaseField
    private int unreadMsgCount;
    @DatabaseField
    private String header;
    @DatabaseField
    private String avatar;
    @DatabaseField
    private String reverve;

    public Contact() {
    }

    public Contact(String username, String nick) {
        this.username = username;
        this.nick = nick;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Contact)) {
            return false;
        }
        return getUsername().equals(((Contact) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}
