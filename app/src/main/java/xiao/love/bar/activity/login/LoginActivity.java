package xiao.love.bar.activity.login;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import butterknife.Bind;
import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.activity.BaseFragmentActivity;
import xiao.love.bar.activity.MainActivity;
import xiao.love.bar.component.util.L;
import xiao.love.bar.component.util.T;

/**
 * Created by guochang on 2015/9/24.
 */

public class LoginActivity extends BaseFragmentActivity {
    @Bind(R.id.edit_username)
    EditText mUserNameEdit;
    @Bind(R.id.edit_password)
    EditText mPwdEdit;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidgets() {

    }

    @OnClick(R.id.btn_login)
    public void onLogin() {
        String userName = mUserNameEdit.getText().toString().trim();
        String password = mPwdEdit.getText().toString().trim();
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(int i, String s) {
                L.d(i + ":" + s);
                T.show(mContext, i + ":" + s, Toast.LENGTH_SHORT);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


}
