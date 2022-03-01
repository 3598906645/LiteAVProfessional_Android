package com.tencent.liteav.trtcdemo.ui.widget.settingitem.moreitem;

import android.content.Context;

import com.blankj.utilcode.util.SizeUtils;
import com.tencent.liteav.trtcdemo.model.helper.SettingConfigHelper;
import com.tencent.liteav.trtcdemo.model.manager.TRTCCloudManager;
import com.tencent.liteav.trtcdemo.ui.widget.settingitem.base.AbsSwitchItem;

public class PriorSmallItem extends AbsSwitchItem {

    private TRTCCloudManager mTRTCCloudManager;

    public PriorSmallItem(TRTCCloudManager manager, Context context, String title) {
        super(context, true, title);
        mTRTCCloudManager = manager;
        setCheck(SettingConfigHelper.getInstance().getMoreConfig().isPriorSmall());
        setPadding(0, SizeUtils.dp2px(8), 0, 0);
    }

    @Override
    public void onChecked() {
        SettingConfigHelper.getInstance().getMoreConfig().setPriorSmall(getChecked());
        if (mTRTCCloudManager != null) {
            mTRTCCloudManager.setTRTCCloudParam();
            mTRTCCloudManager.enableGSensor(SettingConfigHelper.getInstance().getMoreConfig().isEnableGSensorMode());
        }
    }
}

