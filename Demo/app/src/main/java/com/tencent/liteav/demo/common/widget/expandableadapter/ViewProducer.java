/**
 * ViewProducer
 * https://github.com/hgDendi/ExpandableRecyclerView
 * Copyright (c) 2017 hg.dendi
 * MIT License
 * https://rem.mit-license.org/
 * email: hg.dendi@gmail.com
 * Date: 2017-10-18
 */

package com.tencent.liteav.demo.common.widget.expandableadapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface ViewProducer {
    int VIEW_TYPE_EMPTY  = 1 << 30;
    int VIEW_TYPE_HEADER = VIEW_TYPE_EMPTY >> 1;

    /**
     * equivalent to RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     *
     * @param parent
     * @return
     */
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    /**
     * equivalent to RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     *
     * @param holder
     */
    void onBindViewHolder(RecyclerView.ViewHolder holder);

    public static class DefaultEmptyViewHolder extends RecyclerView.ViewHolder {
        public DefaultEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
