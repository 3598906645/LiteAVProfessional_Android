package com.tencent.liteav.demo.livepusher.camerapush.ui.model;

import java.io.Serializable;

public class SettingInfo implements Serializable {

    public static final int RENDER_TYPE_CLOUD_VIDEO_VIEW = 0;
    public static final int RENDER_TYPE_TEXTURE_VIEW = 1;
    public static final int RENDER_TYPE_SURFACE_VIEW = 2;
    public static final int RENDER_TYPE_SURFACE = 3;

    public int renderType;
}
