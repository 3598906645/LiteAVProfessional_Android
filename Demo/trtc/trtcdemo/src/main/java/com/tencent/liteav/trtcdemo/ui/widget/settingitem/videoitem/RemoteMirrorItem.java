package com.tencent.liteav.trtcdemo.ui.widget.settingitem.videoitem;

import android.content.Context;

import com.blankj.utilcode.util.SizeUtils;
import com.tencent.liteav.trtcdemo.model.helper.SettingConfigHelper;
import com.tencent.liteav.trtcdemo.model.manager.TRTCCloudManager;
import com.tencent.liteav.trtcdemo.ui.widget.settingitem.base.AbsSwitchItem;

public class RemoteMirrorItem extends AbsSwitchItem {
    private TRTCCloudManager mTRTCCloudManager;

    public RemoteMirrorItem(TRTCCloudManager manager, Context context, String title) {
        super(context, false, title);
        mTRTCCloudManager = manager;
        setCheck(SettingConfigHelper.getInstance().getVideoConfig().isRemoteMirror());
        setPadding(0, SizeUtils.dp2px(8), 0, 0);
    }

    @Override
    public void onChecked() {
        SettingConfigHelper.getInstance().getVideoConfig().setRemoteMirror(getChecked());
        mTRTCCloudManager.enableVideoEncMirror(SettingConfigHelper.getInstance().getVideoConfig().isRemoteMirror());
    }
}
