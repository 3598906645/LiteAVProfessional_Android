package com.tencent.liteav.trtcdemo.ui.dialog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;

import com.blankj.utilcode.util.CollectionUtils;
import com.tencent.liteav.demo.common.AppRuntime;
import com.tencent.liteav.trtcdemo.R;
import com.tencent.liteav.trtcdemo.model.manager.TRTCCloudManager;
import com.tencent.liteav.trtcdemo.model.manager.TRTCRemoteUserManager;
import com.tencent.liteav.trtcdemo.ui.base.BaseSettingFragment;
import com.tencent.liteav.trtcdemo.ui.base.BaseTabDialogFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.AudioSettingFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.MoreSettingFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.PkSettingFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.SteamSettingFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.SubRoomFragment;
import com.tencent.liteav.trtcdemo.ui.fragment.VideoSettingFragment;
import com.tencent.liteav.trtcdemo.ui.widget.videolayout.TRTCVideoLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设置面板
 *
 * @author : xander
 * @date : 2021/5/25
 */
public class SettingPanelDialogFragment extends BaseTabDialogFragment {
    
    private VideoSettingFragment   mVideoSettingFragment;
    private AudioSettingFragment   mAudioSettingFragment;
    private SteamSettingFragment   mSteamSettingFragment;
    private PkSettingFragment      mPkSettingFragment;
    private MoreSettingFragment    mMoreSettingFragment;
    private SubRoomFragment        mSubRoomFragment;
    private List<Fragment>         mFragmentList;
    private TRTCCloudManager       mTRTCCloudManager;
    private TRTCRemoteUserManager  mTRTCRemoteUserManager;
    private TRTCVideoLayoutManager mLayoutManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }
    
    private void initFragment() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<>();
            mVideoSettingFragment = new VideoSettingFragment();
            mAudioSettingFragment = new AudioSettingFragment();
            mSteamSettingFragment = new SteamSettingFragment();
            mPkSettingFragment = new PkSettingFragment();
            mPkSettingFragment.setPkSettingListener(new PkSettingFragment.PkSettingListener() {
                @Override
                public void onPkSettingComplete() {
                    dismiss();
                }
            });
            mMoreSettingFragment = new MoreSettingFragment();
            mSubRoomFragment = new SubRoomFragment();
            mFragmentList.add(mVideoSettingFragment);
            mFragmentList.add(mAudioSettingFragment);
            mFragmentList.add(mSteamSettingFragment);
            mFragmentList.add(mPkSettingFragment);
            mFragmentList.add(mMoreSettingFragment);
            if (AppRuntime.get().isDebug()) {
                mFragmentList.add(mSubRoomFragment);
            }
            if (mTRTCCloudManager != null) {
                for (Fragment fragment : mFragmentList) {
                    if (fragment instanceof BaseSettingFragment) {
                        ((BaseSettingFragment) fragment).setTRTCCloudManager(mTRTCCloudManager);
                        ((BaseSettingFragment) fragment).setTRTCRemoteUserManager(mTRTCRemoteUserManager);
                        ((BaseSettingFragment) fragment).setmTRTCVideoLayoutManager(mLayoutManager);
                    }
                }
            }
        }
    }
    
    public void setTRTCCloudManager(TRTCCloudManager trtcCloudManager,
                                    TRTCRemoteUserManager trtcRemoteUserManager,
                                    TRTCVideoLayoutManager layoutManager) {
        mTRTCCloudManager = trtcCloudManager;
        mTRTCRemoteUserManager = trtcRemoteUserManager;
        mLayoutManager = layoutManager;
        if (!CollectionUtils.isEmpty(mFragmentList)) {
            for (Fragment fragment : mFragmentList) {
                if (fragment instanceof BaseSettingFragment) {
                    ((BaseSettingFragment) fragment).setTRTCCloudManager(mTRTCCloudManager);
                    ((BaseSettingFragment) fragment).setTRTCRemoteUserManager(mTRTCRemoteUserManager);
                    ((BaseSettingFragment) fragment).setmTRTCVideoLayoutManager(mLayoutManager);
                }
            }
        }
    }
    
    public void destroyAllSubRooms() {
        if (mSubRoomFragment != null) {
            mSubRoomFragment.destroyAllSubRooms();
        }
    }
    
    @Override
    protected List<Fragment> getFragments() {
        return mFragmentList;
    }
    
    @Override
    protected List<String> getTitleList() {
        return Arrays.asList(
                getString(R.string.trtcdemo_setting_tab_video),
                getString(R.string.trtcdemo_setting_tab_audio),
                getString(R.string.trtcdemo_setting_tab_cdn),
                getString(R.string.trtcdemo_setting_tab_pk),
                getString(R.string.trtcdemo_setting_tab_other),
                getString(R.string.trtcdemo_setting_tab_sub_room));
    }
    
    @Override
    protected int getHeight(DisplayMetrics dm) {
        return (int) (dm.heightPixels * 0.7);
    }
}
