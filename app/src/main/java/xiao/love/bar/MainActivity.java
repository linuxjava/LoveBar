package xiao.love.bar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

import org.json.JSONException;
import org.json.JSONObject;

import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.util.L;
import xiao.love.bar.im.chat.ChatActivity;

public class MainActivity extends BaseActivity {
    private Button[] mTabs;
    // 未读消息textview
    private TextView mUnreadLabel;
    // 未读通讯录textview
    private TextView mUnreadContactLable;
    // 当前fragment的index
    private int mCurrentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        //initView();
    }

    private void test(){
        //测试相册
        //PhotoPickTest.test(this);
        //测试聊天
        EMChatManager.getInstance().login("xiaoguochang", "test", new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("userId", "xiaoguochang1");
                startActivity(intent);
            }

            @Override
            public void onError(int i, String s) {
                L.d("xiao1", i + ":" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        mUnreadLabel = (TextView) findViewById(R.id.unread_msg_number_tv);
        mUnreadContactLable = (TextView) findViewById(R.id.unread_contact_number_tv);
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.conversation_btn);
        mTabs[1] = (Button) findViewById(R.id.contact_btn);
        mTabs[2] = (Button) findViewById(R.id.setting_btn);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
    }

    public void onTabClicked(View v){
        int index = 0;
        switch (v.getId()) {
            case R.id.conversation_btn:
                index = 0;
                break;
            case R.id.contact_btn:
                index = 1;
                break;
            case R.id.setting_btn:
                index = 2;
                break;
        }

        if(mCurrentTabIndex == index){

        }else {
            mTabs[mCurrentTabIndex].setSelected(false);
            // 把当前tab设为选中状态
            mTabs[index].setSelected(true);
            mCurrentTabIndex = index;
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }
}
