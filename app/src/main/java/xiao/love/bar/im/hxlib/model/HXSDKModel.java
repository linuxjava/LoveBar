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
 * HX SDK app model which will manage the user data and preferences
 *
 * @author easemob
 */
public abstract class HXSDKModel {
    //是否接受消息通知
    public abstract void setNotificationEnable(boolean paramBoolean);

    public abstract boolean getNotificationEnable();

    //消息是否响铃
    public abstract void setNotificationSound(boolean paramBoolean);

    public abstract boolean getNotificationSound();

    //消息是否震动
    public abstract void setNotificationVibrate(boolean paramBoolean);

    public abstract boolean getNotificationVibrate();

    //播放语音时是否打开扬声器
    public abstract void setVoiceSpeaker(boolean paramBoolean);

    public abstract boolean getVoiceSpeaker();

    /**
     * 返回application所在的process name,默认是包名
     *
     * @return
     */
    public abstract String getAppProcessName();

    /**
     * 是否总是接收好友邀请
     *
     * @return
     */
    public boolean getAcceptInvitationAlways() {
        return true;
    }

    /**
     * 是否需要环信好友关系
     *
     * @return
     */
    public boolean getUseHXRoster() {
        return true;
    }

    /**
     * 是否需要已读回执
     *
     * @return
     */
    public boolean getRequireReadAck() {
        return true;
    }

    /**
     * 是否需要已送达回执
     *
     * @return
     */
    public boolean getRequireDeliveryAck() {
        return true;
    }

    /**
     * 是否允许聊天室Owner离开
     */
    public boolean getChatroomOwnerLeaveAllowed() {
        return true;
    }

    /**
     * 不要开启此设置位，否者或登录失败
     */
    public boolean isSandboxMode() {
        return false;
    }

    /**
     * 是否设置debug模式
     *
     * @return
     */
    public boolean isDebugMode() {
        return true;
    }

    //组是否同步
    public void setGroupsSynced(boolean synced) {
    }

    public boolean isGroupsSynced() {
        return false;
    }

    //通讯录是否已同步
    public void setContactSynced(boolean synced) {
    }

    public boolean isContactSynced() {
        return false;
    }

    //黑名单是否已同步
    public void setBlacklistSynced(boolean synced) {
    }

    public boolean isBacklistSynced() {
        return false;
    }
}
