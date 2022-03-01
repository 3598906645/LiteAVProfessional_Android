package com.tencent.liteav.trtcdemo.ui.widget.settingitem.enteritem;

import android.content.Context;

import com.blankj.utilcode.util.SizeUtils;
import com.tencent.liteav.trtcdemo.ui.widget.settingitem.base.AbsRadioButtonItem;

public class EncoderTypeItem extends AbsRadioButtonItem {

    public EncoderTypeItem(Context context, String title, String... textList) {
        super(context, true, title, textList);
        setSelect(1);
        setPadding(0, SizeUtils.dp2px(8), 0, 0);
    }

    @Override
    public void onSelected(final int index) {

    }
}