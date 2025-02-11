package com.tencent.liteav.login.model;

import android.text.TextUtils;
import android.app.Activity;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tencent.liteav.debug.GenerateTestUserSig;
import com.tencent.liteav.demo.common.UserModel;
import com.tencent.liteav.demo.common.UserModelManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final ProfileManager ourInstance = new ProfileManager();


    private final static String PER_DATA       = "per_profile_manager";
    private final static String PER_USER_MODEL = "per_user_model";
    private static final String PER_USER_ID    = "per_user_id";
    private static final String PER_TOKEN      = "per_user_token";
    private static final String PER_USER_DATE  = "per_user_publish_video_date";
    private static final String TAG            = "ProfileManager";

    private UserModel mUserModel;
    private String    mUserId;
    private String    mToken;
    private String    mUserPubishVideoDate;
    private boolean   isLogin = false;

    public static ProfileManager getInstance() {
        return ourInstance;
    }

    private ProfileManager() {
    }

    public boolean isLogin() {
        return isLogin;
    }

    public UserModel getUserModel() {
        if (mUserModel == null) {
            loadUserModel();
        }
        return mUserModel;
    }

    public String getUserId() {
        if (mUserId == null) {
            mUserId = SPUtils.getInstance(PER_DATA).getString(PER_USER_ID, "");
        }
        return mUserId;
    }

    private void setUserId(String userId) {
        mUserId = userId;
        SPUtils.getInstance(PER_DATA).put(PER_USER_ID, userId);
    }

    private void setUserModel(UserModel model) {
        mUserModel = model;
        saveUserModel();
    }

    public String getToken() {
        if (mToken == null) {
            loadToken();
        }
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
        SPUtils.getInstance(PER_DATA).put(PER_TOKEN, mToken);
    }

    private void loadToken() {
        mToken = SPUtils.getInstance(PER_DATA).getString(PER_TOKEN, "");
    }


    public String getUserPublishVideoDate() {
        if (mUserPubishVideoDate == null) {
            mUserPubishVideoDate = SPUtils.getInstance(PER_DATA).getString(PER_USER_DATE, "");
        }
        return mUserPubishVideoDate;
    }

    public void setUserPublishVideoDate(String date) {
        mUserPubishVideoDate = date;
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_DATE, mUserPubishVideoDate);
        } catch (Exception e) {
        }
    }

    public void getSms(String phone, final ActionCallback callback) {
        callback.onSuccess();
    }

    public void logoff(final ActionCallback callback) {
        logout(new ActionCallback() {
            @Override
            public void onSuccess() {
                // 注销登录成功后,将头像和昵称设为空
                mUserModel.userName = null;
                mUserModel.userAvatar = null;
                saveUserModel();
                callback.onSuccess();
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }

    public void logout(final ActionCallback callback) {
        setUserId("");
        isLogin = false;
        callback.onSuccess();
    }

    public void login(String userId, String username, final ActionCallback callback) {
        isLogin = true;
        setUserId(userId);
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.userName = username;
        userModel.phone = userId;
        userModel.userId = userId;
        userModel.userSig = GenerateTestUserSig.genTestUserSig(userModel.userId);
        userModel.userNameSig = GenerateTestUserSig.genTestUserSig(userModel.userName);
        setUserModel(userModel);
        callback.onSuccess();
    }

    public void autoLogin(String userId, String username, final ActionCallback callback) {
        isLogin = true;
        setUserId(userId);
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.userName = username;
        userModel.phone = userId;
        userModel.userId = userId;
        userModel.userSig = GenerateTestUserSig.genTestUserSig(userModel.userId);
        userModel.userNameSig = GenerateTestUserSig.genTestUserSig(userModel.userName);
        setUserModel(userModel);
        callback.onSuccess();
    }

    public NetworkAction getUserInfoByUserId(String userId, final GetUserInfoCallback callback) {
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.phone = userId;
        userModel.userId = userId;
        userModel.userName = userId;
        callback.onSuccess(userModel);
        return new NetworkAction();
    }

    public NetworkAction getUserInfoByPhone(String phone, final GetUserInfoCallback callback) {
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(phone);
        userModel.phone = phone;
        userModel.userId = phone;
        userModel.userName = phone;
        callback.onSuccess(userModel);
        return new NetworkAction();
    }

    public void getUserInfoBatch(List<String> userIdList, final GetUserInfoBatchCallback callback) {
        if (userIdList == null) {
            return;
        }
        List<UserModel> userModelList = new ArrayList<>();
        for (String userId : userIdList) {
            UserModel userModel = new UserModel();
            userModel.userAvatar = getAvatarUrl(userId);
            userModel.phone = userId;
            userModel.userId = userId;
            userModel.userName = userId;
            userModelList.add(userModel);
        }
        callback.onSuccess(userModelList);
    }

    private String getAvatarUrl(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        byte[] bytes = userId.getBytes();
        int index = bytes[bytes.length - 1] % 10;
        String avatarName = "avatar" + index + "_100";
        return "https://imgcache.qq.com/qcloud/public/static//" + avatarName + ".20191230.png";
    }

    public void setUserName(String userId, final String userName, final ActionCallback callback) {
        mUserModel.userName = userName;
        mUserModel.userNameSig = GenerateTestUserSig.genTestUserSig(userName);
        saveUserModel();
        callback.onSuccess();
    }

    public void setAvatar(final String avatar, final ActionCallback callback) {
        mUserModel.userAvatar = avatar;
        saveUserModel();
        callback.onSuccess();
    }

    private void saveUserModel() {
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, GsonUtils.toJson(mUserModel));
        } catch (Exception e) {
        }
    }

    private void loadUserModel() {
        try {
            String json = SPUtils.getInstance(PER_DATA).getString(PER_USER_MODEL);
            mUserModel = GsonUtils.fromJson(json, UserModel.class);
        } catch (Exception e) {
        }
    }

    public static class NetworkAction {

        public NetworkAction() {
        }

        public void cancel() {
        }
    }

    // 操作回调
    public interface ActionCallback {
        void onSuccess();

        void onFailed(int code, String msg);
    }

    // 通过userid/phone获取用户信息回调
    public interface GetUserInfoCallback {
        void onSuccess(UserModel model);

        void onFailed(int code, String msg);
    }

    // 通过userId批量获取用户信息回调
    public interface GetUserInfoBatchCallback {
        void onSuccess(List<UserModel> model);

        void onFailed(int code, String msg);
    }

    public void checkNeedShowSecurityTips(Activity activity) {

    }
}
