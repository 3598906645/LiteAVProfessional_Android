package com.tencent.liteav.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.liteav.demo.common.UserModelManager;
import com.tencent.liteav.login.model.ProfileManager;
import com.tencent.liteav.login.ui.LoginActivity;

public class LogoffActivity extends Activity {

    private Context     mContext;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_log_off);
        mContext = this;
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView tvAccound = findViewById(R.id.tv_account);
        String phone = UserModelManager.getInstance().getUserModel().phone;
        tvAccound.setText(getString(R.string.app_logoff_cur_account, phone));

        Button mBtnLogoff = findViewById(R.id.btn_logoff);
        mBtnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOffDialog();
            }
        });
    }

    private void showLogOffDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this, R.style.common_alert_dialog)
                    .setMessage(mContext.getString(R.string.app_logoff_confirm))
                    .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                            //注销登录
                            logoff();
                        }
                    }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    private void logoff() {
        final ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.logoff(new ProfileManager.ActionCallback() {
            @Override
            public void onSuccess() {
                ToastUtils.showShort(getString(R.string.app_logoff_account_ok));
                // 注销登录
                startLoginActivity();
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}