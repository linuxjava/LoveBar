/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xiao.love.bar.im.hxlib.model;

/**
 * UI Demo HX Model implementation
 */


import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xiao.love.bar.component.util.AppUtils;
import xiao.love.bar.storage.db.dao.BlackListDB;
import xiao.love.bar.storage.db.dao.ContactDB;
import xiao.love.bar.storage.db.model.BlackList;
import xiao.love.bar.storage.db.model.Contact;
import xiao.love.bar.storage.sp.impl.IMSp;

/**
 * HuanXin default SDK Model implementation
 *
 * @author easemob
 */
public class DefaultHXSDKModel extends HXSDKModel {
    private Context mContext;
    //联系人(key：username；value：Contact)
    private Map<String, Contact> mContactMap;
    //黑名单
    private List<BlackList> mBlackList;
    enum Key {
        NOTIFYCATION_ENABLE,
        NOTIFYCATION_VIBRATE_ENABLE,
        NOTIFYCATION_SOUND_ENABLE,
        VOICE_SPEAKER_ENABLE,
    }

    public DefaultHXSDKModel(Context context) {
        mContext = context;
        mContactMap = new HashMap<>();
        mBlackList = new ArrayList<>();
    }

    //状态值内存缓存
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();

    @Override
    public void setNotificationEnable(boolean paramBoolean) {
        IMSp.getInstance().putNotificationEnable(paramBoolean);
        valueCache.put(Key.NOTIFYCATION_ENABLE, paramBoolean);
    }

    @Override
    public boolean getNotificationEnable() {
        Object val = valueCache.get(Key.NOTIFYCATION_ENABLE);

        if (val == null) {
            val = IMSp.getInstance().getNotificationEnable(true);
            valueCache.put(Key.NOTIFYCATION_ENABLE, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    @Override
    public void setNotificationSound(boolean paramBoolean) {
        IMSp.getInstance().putNotificationSound(paramBoolean);
        valueCache.put(Key.NOTIFYCATION_SOUND_ENABLE, paramBoolean);
    }

    @Override
    public boolean getNotificationSound() {
        Object val = valueCache.get(Key.NOTIFYCATION_SOUND_ENABLE);

        if (val == null) {
            val = IMSp.getInstance().getNotificationSound(true);
            valueCache.put(Key.NOTIFYCATION_SOUND_ENABLE, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    @Override
    public void setNotificationVibrate(boolean paramBoolean) {
        IMSp.getInstance().putNotificationVibrate(paramBoolean);
        valueCache.put(Key.NOTIFYCATION_VIBRATE_ENABLE, paramBoolean);
    }

    @Override
    public boolean getNotificationVibrate() {
        Object val = valueCache.get(Key.NOTIFYCATION_VIBRATE_ENABLE);

        if (val == null) {
            val = IMSp.getInstance().getNotificationVibrate(true);
            valueCache.put(Key.NOTIFYCATION_VIBRATE_ENABLE, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    @Override
    public void setVoiceSpeaker(boolean paramBoolean) {
        IMSp.getInstance().putVoiceSpeaker(paramBoolean);
        valueCache.put(Key.VOICE_SPEAKER_ENABLE, paramBoolean);
    }

    @Override
    public boolean getVoiceSpeaker() {
        Object val = valueCache.get(Key.VOICE_SPEAKER_ENABLE);

        if (val == null) {
            val = IMSp.getInstance().getVoiceSpeaker(true);
            valueCache.put(Key.VOICE_SPEAKER_ENABLE, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    @Override
    public String getAppProcessName() {
        return AppUtils.getPackageName(mContext);
    }

    public void setGroupsSynced(boolean synced) {
        IMSp.getInstance().putGroupSynced(synced);
    }

    public boolean isGroupsSynced() {
        return IMSp.getInstance().getGroupSynced(false);
    }

    public void setContactSynced(boolean synced) {
        IMSp.getInstance().putContactSynced(synced);
    }

    public boolean isContactSynced() {
        return IMSp.getInstance().getContactSynced(false);
    }

    public void setBlacklistSynced(boolean synced) {
        IMSp.getInstance().putBalcklistSynced(synced);
    }

    public boolean isBacklistSynced() {
        return IMSp.getInstance().getBalcklistSynced(false);
    }

    /**
     * 添加联系人
     * @param contact
     */
    public void addContact(Contact contact) {
        ContactDB.getInstance(mContext).createOrUpdate(contact);
        mContactMap.put(contact.getUsername(), contact);
    }

    /**
     * 删除联系人
     * @param contact
     */
    public void delContact(Contact contact) {
        ContactDB.getInstance(mContext).delete(contact);
        mContactMap.remove(contact.getUsername());
    }

    /**
     * 清除内存中的联系人
     */
    public void clearContact() {
        mContactMap.clear();
    }

    /**
     * 设置联系人
     *
     * @param contactMap
     */
    public void setContactList(Map<String, Contact> contactMap) {
        List<Contact> list = new ArrayList<>();
        for (Contact c : contactMap.values()){
            list.add(c);
        }

        ContactDB.getInstance(mContext).insert(list);
        mContactMap.putAll(contactMap);
    }

    /**
     * 获得联系人
     * @return
     */
    public Map<String, Contact> getContactList(){
        if(mContactMap != null && mContactMap.size() == 0){
            List<Contact> list = ContactDB.getInstance(mContext).getAll();
            for (Contact c : list){
                mContactMap.put(c.getUsername(), c);
            }
        }

        return mContactMap;
    }

    /**
     * 添加黑名单
     * @param black
     */
    public void addBlackList(BlackList black) {
        BlackListDB.getInstance(mContext).createOrUpdate(black);
        mBlackList.add(black);
    }

    /**
     * 移除黑名单
     * @param black
     */
    public void delBlackList(BlackList black) {
        BlackListDB.getInstance(mContext).delete(black);
        mBlackList.remove(black);
    }

    /**
     * 清除内存中的黑名单
     */
    public void clearBlackList() {
        mBlackList.clear();
    }

    /**
     * 设置黑名单
     *
     * @param list
     */
    public void setBlackList(List<BlackList> list) {
        BlackListDB.getInstance(mContext).insert(list);
        mBlackList.addAll(list);
    }

    /**
     * 用户名是否在黑名单中
     *
     * @param userName
     * @return
     */
    public boolean isBlackList(String userName) {
        if (mBlackList != null && mBlackList.size() == 0) {
            mBlackList = BlackListDB.getInstance(mContext).getAll();
        }

        if (mBlackList != null) {
            for (BlackList b : mBlackList) {
                if (userName.equals(b.getUsername())) {
                    return true;
                }
            }
        }

        return false;
    }
}
