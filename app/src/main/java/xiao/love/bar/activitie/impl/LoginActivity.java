package xiao.love.bar.activitie.impl;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import butterknife.Bind;
import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.activitie.BaseFragmentActivity;
import xiao.love.bar.component.util.L;
import xiao.love.bar.component.util.T;

/**
 * Created by guochang on 2015/9/24.
 */

public class LoginActivity extends BaseFragmentActivity {
    @Bind(R.id.username)
    EditText mUserNameEdt;
    @Bind(R.id.password)
    EditText mPwdEdt;

    @Override
    protected int getLayout() {
        return R.layout.login;
    }

    @Override
    protected void initWidgets() {

    }

    @OnClick(R.id.login)
    public void onLogin() {
        String userName = mUserNameEdt.getText().toString().trim();
        String password = mPwdEdt.getText().toString().trim();
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(int i, String s) {
                L.d("xiao1", i + ":" + s);
                T.show(mContext, i + ":" + s, Toast.LENGTH_SHORT);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


}
