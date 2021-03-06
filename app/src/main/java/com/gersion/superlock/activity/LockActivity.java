package com.gersion.superlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gersion.superlock.R;
import com.gersion.superlock.base.BaseActivity;
import com.gersion.superlock.dao.MainKeyDao;
import com.gersion.superlock.utils.Md5Utils;
import com.gersion.superlock.utils.MyConstants;
import com.gersion.superlock.utils.SpfUtils;
import com.gersion.superlock.utils.ToastUtils;
import com.gersion.toastlibrary.TastyToast;

public class LockActivity extends BaseActivity implements View.OnClickListener {

    public static String IS_FIRST_TIME = "is_first_time";
    private boolean isfirstTime;
    private EditText mLoginPwd;
    private ImageView mLoginGo;
    private boolean mIsAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initEvent();
    }

    // 初始化控件
    private void initView() {
        mLoginPwd = (EditText) findViewById(R.id.activity_login_pwd);
        mLoginGo = (ImageView) findViewById(R.id.activity_login_go);
    }

    @Override
    public void onStart() {
        super.onStart();
        mIsAutoLogin = SpfUtils.getBoolean(this, MyConstants.IS_AUTO_LOGIN, false);
    }

    // 初始化监听事件
    private void initEvent() {
        mLoginGo.setOnClickListener(this);
        //当手机输入法按下确认键的时候执行登录
        mLoginPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 0) {
                    login();
                }
                return false;
            }
        });

        mLoginPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mIsAutoLogin) {
                    String pwd = mLoginPwd.getText().toString().trim();
                    int length = SpfUtils.getInt(LockActivity.this, MyConstants.LENGTH, 0);
                    if (pwd.length() == length) {
                        login();
                        return;
                    }
                }
            }
        });
    }

    // 初始化数据
    private void initData() {

    }

    // 登录
    private void login() {
        String pwd = Md5Utils.encodeTimes(MyConstants.ADD_SALT
                + mLoginPwd.getText().toString().trim() + MyConstants.ADD_SALT);
        MainKeyDao mkd = new MainKeyDao(this);
        String key = mkd.query();
        if (TextUtils.equals(pwd, key)) {
//            ToastUtils.showTasty(LockActivity.this,"登录成功", TastyToast.SUCCESS);
            finish();
        } else {
            ToastUtils.showTasty(this,"密码错误", TastyToast.ERROR);
            mLoginPwd.setText("");
        }
    }

    // 按返回键直接进入手机桌面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_login_go:
                login();
                break;
        }
    }
}
