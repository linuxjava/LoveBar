package xiao.love.bar.im.hxlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.MainActivity;
import xiao.love.bar.im.hxlib.controller.HXSDKHelper;
import xiao.love.bar.im.hxlib.model.DefaultHXSDKModel;
import xiao.love.bar.im.hxlib.model.HXNotifier;

/**
 * Created by guochang on 2015/9/11.
 */
public class IMHelper extends HXSDKHelper {
    //用来记录foreground Activity
    private List<Activity> mActivityList = new ArrayList<Activity>();

    public IMHelper(){
        super();
    }

    @Override
    protected void initListener() {
        super.initListener();

        //注册消息事件监听
        EMChatManager.getInstance().registerEventListener(mEventListener);
    }

    public void pushActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 账号冲突
     */
    @Override
    protected void onConnectionConflict() {
//        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("conflict", true);
//        appContext.startActivity(intent);
    }

    /**
     * 账号被删除
     */
    @Override
    protected void onCurrentAccountRemoved() {
//        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
//        appContext.startActivity(intent);
    }

    /**
     * 事件监听
     */
    private EMEventListener mEventListener = new EMEventListener() {
        private BroadcastReceiver broadCastReceiver = null;

        @Override
        public void onEvent(EMNotifierEvent event) {
            EMMessage message = null;
            if (event.getData() instanceof EMMessage) {
                message = (EMMessage) event.getData();
            }

            switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if (mActivityList.size() <= 0) {
                        HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                    }
                    break;
                case EventOfflineMessage:
                    if (mActivityList.size() <= 0) {
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage: {
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action

                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    final String str = "收到透传：action：";

                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                    if (broadCastReceiver == null) {
                        broadCastReceiver = new BroadcastReceiver() {

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };

                        //注册广播接收者
                        appContext.registerReceiver(broadCastReceiver, cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str + action);
                    appContext.sendBroadcast(broadcastIntent, null);

                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    break;
                // add other events in case you are interested in
                default:
                    break;
            }

        }
    };

    @Override
    public void logout(final boolean unbindDeviceToken,final EMCallBack callback){
        super.logout(unbindDeviceToken,new EMCallBack(){

            @Override
            public void onSuccess() {
                //清除内存中的联系人，以便下次登录时重新加载
                ((DefaultHXSDKModel)getModel()).clearContact();
                //清除内存中的黑名单，以便下次登录时重新加载
                ((DefaultHXSDKModel)getModel()).clearBlackList();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                if(callback != null){
                    callback.onError(code, message);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    @Override
    public HXNotifier createNotifier() {
        return new HXNotifier() {
            public synchronized void onNewMsg(final EMMessage message) {
                if (EMChatManager.getInstance().isSlientMessage(message)) {
                    return;
                }

                String chatUsename = null;
                // 是否在黑名单中
                if (!((DefaultHXSDKModel) hxModel).isBlackList(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }

                    viberateAndPlayTone(message);
                }
            }
        };
    }

    /**
     * 自定义通知栏提示内容
     *
     * @return
     */
    @Override
    protected HXNotifier.HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotifier.HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = IMUtil.getMessageDigest(appContext, message);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                return message.getFrom() + ": " + ticker;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, MainActivity.class);
                //有电话时优先跳转到通话页面
                if (isVideoCalling) {
                    //intent = new Intent(appContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    //intent = new Intent(appContext, VoiceCallActivity.class);
                } else {
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                        //intent.putExtra("userId", message.getFrom());
                        //intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        //intent.putExtra("groupId", message.getTo());
                        if (chatType == EMMessage.ChatType.GroupChat) {
                            //intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        } else {
                            //intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                        }

                    }
                }

                return intent;
            }
        };
    }
}
