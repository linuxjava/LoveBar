package xiao.love.bar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.util.L;
import xiao.love.bar.im.chat.ChatActivity;

/**
 * Created by guochang on 2015/9/24.
 */
@EActivity(R.layout.login)
public class LoginActivity extends BaseActivity{
    @ViewById(R.id.username)
    EditText mUserNameEdt;
    @ViewById(R.id.password)
    EditText mPwdEdt;

    @AfterViews
    void initViews(){

    }

    public void onClickLogin(View v){
        String userName = mUserNameEdt.getText().toString().trim();
        String password = mPwdEdt.getText().toString().trim();
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
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
}
