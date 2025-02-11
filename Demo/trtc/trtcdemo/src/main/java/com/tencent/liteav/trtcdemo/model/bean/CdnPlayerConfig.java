package com.tencent.liteav.trtcdemo.model.bean;

import com.tencent.rtmp.TXLiveConstants;

import java.io.Serializable;

/**
 * CDN Tab页 相关参数配置
 *
 * @author : xander
 * @date : 2021/5/25
 */
public class CdnPlayerConfig implements Serializable {
    public static final int   CACHE_STRATEGY_FAST   = 1;  //快速
    public static final int   CACHE_STRATEGY_SMOOTH = 2;  //平滑
    public static final int   CACHE_STRATEGY_AUTO   = 3;  //自动
    public static final float CACHE_TIME_FAST       = 1.0f;
    public static final float CACHE_TIME_SMOOTH     = 5.0f;

    private boolean mIsDebug               = false;
    private int     mCurrentRenderMode     = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION; // player 渲染模式
    private int     mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;      // player 渲染角度
    private int     mCacheStrategy         = CACHE_STRATEGY_AUTO;                           // player 缓存策略

    public boolean isDebug() {
        return mIsDebug;
    }

    public void setDebug(boolean debug) {
        mIsDebug = debug;
    }

    public int getCurrentRenderMode() {
        return mCurrentRenderMode;
    }

    public void setCurrentRenderMode(int currentRenderMode) {
        mCurrentRenderMode = currentRenderMode;
    }

    public int getCurrentRenderRotation() {
        return mCurrentRenderRotation;
    }

    public void setCurrentRenderRotation(int currentRenderRotation) {
        mCurrentRenderRotation = currentRenderRotation;
    }

    public int getCacheStrategy() {
        return mCacheStrategy;
    }

    public void setCacheStrategy(int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
    }
}
