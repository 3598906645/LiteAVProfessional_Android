package com.tencent.liteav.trtcdemo.ui.widget.settingitem.enteritem;

import android.content.Context;

import com.blankj.utilcode.util.SizeUtils;
import com.tencent.liteav.trtcdemo.ui.widget.settingitem.base.AbsRadioButtonItem;

public class RoleItem extends AbsRadioButtonItem {
    
    private OnSelectListener mListener;
    
    public RoleItem(Context context, OnSelectListener listener, String title, String... textList) {
        super(context, false, title, textList);
        mListener = listener;
        setPadding(0, SizeUtils.dp2px(8), 0, 0);
    }

    @Override
    public void onSelected(final int index) {
        if (mListener != null) {
            mListener.onSelected(index);
        }
    }
    
    public interface OnSelectListener {
        void onSelected(final int index);
    }
}