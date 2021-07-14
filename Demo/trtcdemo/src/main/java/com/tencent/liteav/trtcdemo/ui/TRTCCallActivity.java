package com.tencent.liteav.trtcdemo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.liteav.audiosettingkit.AudioEffectPanel;
import com.tencent.liteav.debug.GenerateTestUserSig;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.liteav.trtcdemo.R;
import com.tencent.liteav.trtcdemo.model.helper.SettingConfigHelper;
import com.tencent.liteav.trtcdemo.model.helper.RemoteUserConfigHelper;
import com.tencent.liteav.trtcdemo.model.manager.TRTCCloudManager;
import com.tencent.liteav.trtcdemo.model.bean.AudioConfig;
import com.tencent.liteav.trtcdemo.model.bean.OtherConfig;
import com.tencent.liteav.trtcdemo.model.bean.PkConfig;
import com.tencent.liteav.trtcdemo.model.bean.VideoConfig;
import com.tencent.liteav.trtcdemo.model.customcapture.AudioFrameReader;
import com.tencent.liteav.trtcdemo.model.customcapture.CustomRenderVideo;
import com.tencent.liteav.trtcdemo.model.customcapture.CustomCaptureVideo;
import com.tencent.liteav.trtcdemo.model.customcapture.VideoFrameReader;
import com.tencent.liteav.trtcdemo.model.customcapture.structs.TextureFrame;
import com.tencent.liteav.trtcdemo.model.customcapture.utils.CustomAudioCapturor;
import com.tencent.liteav.trtcdemo.model.manager.TRTCRemoteUserManager;
import com.tencent.liteav.trtcdemo.model.listener.TRTCCloudManagerListener;
import com.tencent.liteav.trtcdemo.ui.dialog.RemoteUserPanelDialogFragment;
import com.tencent.liteav.trtcdemo.ui.dialog.SettingPanelDialogFragment;
import com.tencent.liteav.trtcdemo.ui.widget.videolayout.TRTCVideoLayoutManager;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCStatistics;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_AUDIO_EARPIECEMODE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_AUDIO_INPUT_TYPE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_AUDIO_QUALITY;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_AUDIO_VOLUMETYPE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_CUSTOM_AUDIO_CAPTURE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_CUSTOM_CAPTURE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_MAIN_SCREEN_CAPTURE;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_ROOM_ID;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_ROOM_ID_STR;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_USER_ID;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_USE_STRING_ROOM_ID;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_VIDEO_FILE_PATH;
import static com.tencent.liteav.trtcdemo.model.bean.Constant.KEY_VIDEO_INPUT_TYPE;

public class TRTCCallActivity  extends AppCompatActivity implements View.OnClickListener,
        TRTCCloudManagerListener,
        TRTCCloudManager.IView,
        TRTCRemoteUserManager.IView,
        ITXLivePlayListener,
        CustomAudioCapturor.TXICustomAudioCapturorListener{

    private static final String TAG                                      = "TRTCLiveAnchorActivity";

    private TRTCCloudManager                        mTRTCCloudManager;
    private TRTCRemoteUserManager                   mTRTCRemoteUserManager;

    private TRTCVideoLayoutManager                  mTRTCVideoLayout;
    private TextView                                mTextRoomId;
    private SettingPanelDialogFragment              mSettingPanelFragmentDialog;
    private RemoteUserPanelDialogFragment           mRemoteUserManagerFragmentDialog;
    private BeautyPanel                             mPanelBeautyControl;
    private AudioEffectPanel                        mPanelAudioControl;
    private ImageView                               mImageSwitchCamera;
    private ImageView                               mImageEnableAudio;
    private ImageView                               mImageMore;

    private int                     mLogLevel                   = 0;
    private String                  mMainUserId                 = "";
    private boolean                 mIsCustomCapture            = false;
    private boolean                 mIsCustomLocalRender        = false;
    private boolean                 mIsCustomAudioCapture       = false;
    private String                  mVideoFilePath;
    private CustomCaptureVideo mCustomCapture;
    private CustomRenderVideo mCustomRender;
    private CustomAudioCapturor     mCustomAudioCapturor;

    private int                     mVolumeType                 = TRTCCloudDef.TRTCSystemVolumeTypeAuto;
    private int                     mAudioQuality               = TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT;
    private int                     mAppScene                   = TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;
    private boolean                 mIsAudioEarpieceMode        = false;
    private boolean                 mIsScreenCapture            = false;
    private int                     mRoomIdType                 = 0; //0:数字   1：字符串
    private int                     mRoomId;
    private String                  mRoomIdStr;
    private String                  mUserId;
    private int                     mVideoInputType             = 0;
    private int                     mAudioInputType             = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.BeautyTheme);
        initIntentData();
        setContentView(R.layout.trtcdemo_activity_call_room);
        initTRTCSDK();
        initViews();
        PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.MICROPHONE, PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(List<String> permissionsGranted) {
                enterRoom();
            }

            @Override
            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                ToastUtils.showShort(R.string.trtcdemo_permission_tips);
                finish();
            }
        }).request();
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void initIntentData() {
        Intent intent = getIntent();
        mUserId                 = intent.getStringExtra(KEY_USER_ID);
        mRoomIdType             = intent.getIntExtra(KEY_USE_STRING_ROOM_ID, 0);
        mIsCustomCapture        = intent.getBooleanExtra(KEY_CUSTOM_CAPTURE, false);
        mIsCustomAudioCapture   = intent.getBooleanExtra(KEY_CUSTOM_AUDIO_CAPTURE, false);
        mIsScreenCapture        = intent.getBooleanExtra(KEY_MAIN_SCREEN_CAPTURE, false);
        mVolumeType             = intent.getIntExtra(KEY_AUDIO_VOLUMETYPE, TRTCCloudDef.TRTCSystemVolumeTypeAuto);
        mAudioQuality           = intent.getIntExtra(KEY_AUDIO_QUALITY, TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);
        mIsAudioEarpieceMode    = intent.getBooleanExtra(KEY_AUDIO_EARPIECEMODE,false);
        mVideoInputType         = intent.getIntExtra(KEY_VIDEO_INPUT_TYPE, 0);
        mAudioInputType         = intent.getIntExtra(KEY_AUDIO_INPUT_TYPE, 0);
        mVideoFilePath          = getIntent().getStringExtra(KEY_VIDEO_FILE_PATH);

        if(mRoomIdType == 1){
            mRoomIdStr = intent.getStringExtra(KEY_ROOM_ID_STR);
        }else{
            mRoomId    = intent.getIntExtra(KEY_ROOM_ID, 0);
        }
        if (mIsCustomCapture) {
            mIsCustomLocalRender = true;
            mIsCustomAudioCapture = false;
        }
    }

    private void enterRoom() {
        if(mVideoInputType != 3){
            VideoConfig videoConfig = SettingConfigHelper.getInstance().getVideoConfig();
            startLocalPreview();
            videoConfig.setEnableVideo(true);
            videoConfig.setPublishVideo(true);
            Bitmap bitmap = decodeResource(getResources(), R.drawable.trtcdemo_mute_image);
            videoConfig.setMuteImage(bitmap);
        }

        mTRTCCloudManager.enterRoom();

        if(mAudioInputType != 2){
            AudioConfig audioConfig = SettingConfigHelper.getInstance().getAudioConfig();
            if (mIsCustomAudioCapture) {
                mCustomAudioCapturor.start(48000, 1);
            } else {
                mTRTCCloudManager.startLocalAudio();
            }
            audioConfig.setAudioCapturingStarted(true);
        }
    }

    private void exitRoom() {
        stopLocalPreview();
        if (mCustomAudioCapturor != null) {
            mCustomAudioCapturor.stop();
        }
        SettingConfigHelper.getInstance().getAudioConfig().setRecording(false);
        mTRTCCloudManager.exitRoom();
    }

    private void initViews() {
        mImageSwitchCamera = (ImageView) findViewById(R.id.trtc_iv_camera);
        mTextRoomId = (TextView) findViewById(R.id.trtc_tv_room_id);
        mImageMore = (ImageView) findViewById(R.id.trtc_iv_more);
        mImageEnableAudio = (ImageView) findViewById(R.id.trtc_iv_mic);

        findViewById(R.id.trtc_iv_beauty).setOnClickListener(this);
        findViewById(R.id.trtc_iv_log).setOnClickListener(this);
        findViewById(R.id.iv_camera_on_off).setOnClickListener(this);
        findViewById(R.id.trtc_iv_setting).setOnClickListener(this);
        findViewById(R.id.trtc_ib_back).setOnClickListener(this);
        findViewById(R.id.trtc_iv_music).setOnClickListener(this);
        mImageSwitchCamera.setOnClickListener(this);
        mImageEnableAudio.setOnClickListener(this);
        mImageMore.setOnClickListener(this);

        //音效设置面板
        mPanelAudioControl  = findViewById(R.id.anchor_audio_panel);
        mPanelAudioControl.setAudioEffectManager(mTRTCCloudManager.getAudioEffectManager());
        mPanelAudioControl.initPanelDefaultBackground();

        // 美颜面板
        mPanelBeautyControl = (BeautyPanel) findViewById(R.id.trtc_beauty_panel);
        mPanelBeautyControl.setBeautyManager(mTRTCCloudManager.getBeautyManager());

        // 成员列表面板
        mRemoteUserManagerFragmentDialog = new RemoteUserPanelDialogFragment();
        mRemoteUserManagerFragmentDialog.setTRTCRemoteUserManager(mTRTCRemoteUserManager);
        RemoteUserConfigHelper.getInstance().clear();

        // 界面视频 View 的管理类
        mTRTCVideoLayout = (TRTCVideoLayoutManager) findViewById(R.id.trtc_video_view_layout);

        // 更多设置面板
        mSettingPanelFragmentDialog = new SettingPanelDialogFragment();
        mSettingPanelFragmentDialog.setTRTCCloudManager(mTRTCCloudManager, mTRTCRemoteUserManager,
                mTRTCVideoLayout);
        mImageSwitchCamera.setImageResource(mTRTCCloudManager.isFontCamera() ? R.drawable.trtcdemo_ic_camera_back : R.drawable.trtcdemo_ic_camera_front);

        if(mRoomIdType == 0){
            mTextRoomId.setText(mRoomId + "");
        }else{
            mTextRoomId.setText(mRoomIdStr);
        }
    }

    private void initTRTCSDK() {
        TRTCCloudDef.TRTCParams mTRTCParams = new TRTCCloudDef.TRTCParams();
        mTRTCParams.sdkAppId    = GenerateTestUserSig.SDKAPPID;
        mTRTCParams.userId      = mUserId;
        mTRTCParams.userSig     = GenerateTestUserSig.genTestUserSig(mUserId);
        if(mRoomIdType == 0){
            mTRTCParams.roomId = mRoomId;
        }else{
            mTRTCParams.strRoomId = mRoomIdStr;
        }
        mTRTCCloudManager = new TRTCCloudManager(this, mTRTCParams, mAppScene);
        mTRTCCloudManager.setViewListener(this);
        mTRTCCloudManager.setTRTCListener(this);
        SettingConfigHelper.getInstance().getAudioConfig().setmAudioQulity(mAudioQuality);
        mTRTCCloudManager.initTRTCManager(mIsCustomCapture);
        mTRTCCloudManager.setSystemVolumeType(mVolumeType);
        mTRTCCloudManager.enableAudioHandFree(mIsAudioEarpieceMode);
        SettingConfigHelper.getInstance().getAudioConfig().setAudioEarpieceMode(mIsAudioEarpieceMode);

        mTRTCRemoteUserManager = new TRTCRemoteUserManager(this, this, mIsCustomCapture,
                TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_RGBA,
                TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_BYTE_ARRAY,
                false);
        mTRTCRemoteUserManager.setMixUserId(mUserId);

        if (mIsCustomCapture) {
            mCustomCapture = new CustomCaptureVideo(this, mVideoFilePath, true);
        }
        if (mIsCustomLocalRender) {
            mCustomRender = new CustomRenderVideo(mUserId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG,
                    false);
        }
        if (mIsCustomAudioCapture) {
            mCustomAudioCapturor = CustomAudioCapturor.getInstance();
            mCustomAudioCapturor.setCustomAudioCaptureListener(this);
            mTRTCCloudManager.enableCustomAudioCapture(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.trtc_ib_back) {
            finish();
        } else if (id == R.id.trtc_iv_beauty) {
            if(mPanelBeautyControl.isShown()){
                mPanelBeautyControl.setVisibility(View.GONE);
            }else{
                mPanelBeautyControl.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.trtc_iv_camera) {
            mTRTCCloudManager.switchCamera();
            ((ImageView) v).setImageResource(mTRTCCloudManager.isFontCamera() ? R.drawable.trtcdemo_ic_camera_back : R.drawable.trtcdemo_ic_camera_front);
        } else if (id == R.id.iv_camera_on_off) {
            if (SettingConfigHelper.getInstance().getVideoConfig().isEnableVideo()) {
                SettingConfigHelper.getInstance().getVideoConfig().setEnableVideo(false);
                mTRTCCloudManager.stopLocalPreview();
            } else {
                SettingConfigHelper.getInstance().getVideoConfig().setEnableVideo(true);
                mTRTCCloudManager.startLocalPreview(true, mTRTCVideoLayout);
            }
            ((ImageView) v).setImageResource( SettingConfigHelper.getInstance().getVideoConfig().isEnableVideo() ? R.drawable.trtcdemo_remote_video_enable : R.drawable.trtcdemo_remote_video_disable);
        } else if (id == R.id.trtc_iv_mic) {
            AudioConfig audioConfig = SettingConfigHelper.getInstance().getAudioConfig();
            audioConfig.setLocalAudioMuted(!audioConfig.isLocalAudioMuted());
            mTRTCCloudManager.muteLocalAudio(!audioConfig.isLocalAudioMuted());
            ((ImageView) v).setImageResource(audioConfig.isLocalAudioMuted() ? R.drawable.trtcdemo_mic_enable : R.drawable.trtcdemo_mic_disable);
        } else if (id == R.id.trtc_iv_log) {
            mLogLevel = (mLogLevel + 1) % 3;
            ((ImageView) v).setImageResource((0 == mLogLevel) ? R.drawable.trtcdemo_ic_trtc_log2 : R.drawable.trtcdemo_ic_trtc_log);
            mTRTCCloudManager.showDebugView(mLogLevel);
        } else if (id == R.id.trtc_iv_setting) {
            showDialogFragment(mSettingPanelFragmentDialog, "FeatureSettingFragmentDialog");
        } else if (id == R.id.trtc_iv_more || id == R.id.trtc_iv_more_audience) {
            showDialogFragment(mRemoteUserManagerFragmentDialog, "RemoteUserManagerFragmentDialog");
        } else if (id == R.id.trtc_iv_music) {
            mPanelAudioControl.setVisibility(View.VISIBLE);
            mPanelAudioControl.showAudioPanel();
        }
    }

    private void showDialogFragment(android.support.v4.app.DialogFragment dialogFragment, String tag) {
        if (dialogFragment != null) {
            if (dialogFragment.isVisible()) {
                try {
                    dialogFragment.dismissAllowingStateLoss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                if (!dialogFragment.isAdded()) {
                    dialogFragment.show(getSupportFragmentManager(), tag);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
        mTRTCCloudManager.destroy();
        mTRTCRemoteUserManager.destroy();
        TRTCCloud.destroySharedInstance();
        if (mPanelAudioControl != null) {
            mPanelAudioControl.reset();
            mPanelAudioControl.unInit();
            mPanelAudioControl = null;
        }
    }

    private void startLocalPreview() {
        if (mIsScreenCapture) {             //屏幕共享
            mTRTCCloudManager.startScreenCapture();
        } else if(mIsCustomCapture){        //自定义采集
            startCustomCapture();
        }else{                              //sdk采集
            mTRTCCloudManager.startLocalPreview(true, mTRTCVideoLayout);
        }
    }

    private void startCustomCapture() {
        if(mCustomRender != null){
            TXCloudVideoView localVideoView = mTRTCVideoLayout.allocCloudVideoView(mUserId,
                    TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
            mTRTCCloudManager.setLocalVideoRenderListener(
                    TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_RGBA,
                    TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_BYTE_ARRAY,
                    mCustomRender);
            TextureView textureView = new TextureView(this);
            localVideoView.addVideoView(textureView);
            mCustomRender.start(textureView);
        }

        if(mCustomCapture != null){
            mCustomCapture.start(mAudioFrameReadListener, mVideoFrameReadListener);
        }
    }

    private final AudioFrameReader.AudioFrameReadListener mAudioFrameReadListener = new AudioFrameReader.AudioFrameReadListener() {
        @Override
        public void onFrameAvailable(byte[] data, int sampleRate, int channel, long timestamp) {
            TRTCCloudDef.TRTCAudioFrame trtcAudioFrame = new TRTCCloudDef.TRTCAudioFrame();
            trtcAudioFrame.data = data;
            trtcAudioFrame.sampleRate = sampleRate;
            trtcAudioFrame.channel = channel;
            trtcAudioFrame.timestamp = mTRTCCloudManager.generateCustomPTS();

            mTRTCCloudManager.sendCustomAudioData(trtcAudioFrame);
        }
    };

    private final VideoFrameReader.VideoFrameReadListener mVideoFrameReadListener = new VideoFrameReader.VideoFrameReadListener() {
        @Override
        public void onFrameAvailable(TextureFrame frame) {
            // 将视频帧通过纹理方式塞给SDK
            TRTCCloudDef.TRTCVideoFrame videoFrame = new TRTCCloudDef.TRTCVideoFrame();
            videoFrame.texture = new TRTCCloudDef.TRTCTexture();
            videoFrame.texture.textureId = frame.textureId;
            videoFrame.texture.eglContext14 = frame.eglContext;
            videoFrame.width = frame.width;
            videoFrame.height = frame.height;
            videoFrame.pixelFormat = TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_Texture_2D;
            videoFrame.bufferType = TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_TEXTURE;
            videoFrame.timestamp = mTRTCCloudManager.generateCustomPTS();

            mTRTCCloudManager.sendCustomVideoData(videoFrame);
        }
    };

    private void stopLocalPreview() {
        if (mIsScreenCapture) {
            mTRTCCloudManager.stopScreenCapture();
        }
        if (!mIsCustomCapture) {
            mTRTCCloudManager.stopLocalPreview();
        } else {
            if (mCustomCapture != null) {
                mCustomCapture.stop();
            }
        }
        if (mCustomRender != null) {
            mCustomRender.stop();
        }
        mTRTCVideoLayout.recyclerCloudViewView(mUserId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
    }

    private void startLinkMicLoading() {
        FrameLayout layout = (FrameLayout) findViewById(R.id.trtc_fl_link_loading);
        layout.setVisibility(View.VISIBLE);

        ImageView imageView = (ImageView) findViewById(R.id.trtc_iv_link_loading);
        imageView.setImageResource(R.drawable.trtcdemo_linkmic_loading);
        AnimationDrawable animation = (AnimationDrawable) imageView.getDrawable();
        if (animation != null) {
            animation.start();
        }
    }

    private void stopLinkMicLoading() {
        FrameLayout layout = (FrameLayout) findViewById(R.id.trtc_fl_link_loading);
        layout.setVisibility(View.GONE);

        ImageView         imageView = (ImageView) findViewById(R.id.trtc_iv_link_loading);
        AnimationDrawable animation = (AnimationDrawable) imageView.getDrawable();
        if (animation != null) {
            animation.stop();
        }
    }


    private void onVideoChange(String userId, int streamType, boolean available) {
        if (available) {
            // 首先需要在界面中分配对应的TXCloudVideoView
            TXCloudVideoView renderView = mTRTCVideoLayout.findCloudVideoView(userId, streamType);
            if (renderView == null) {
                renderView = mTRTCVideoLayout.allocCloudVideoView(userId, streamType);
            }
            // 启动远程画面的解码和显示逻辑
            if (renderView != null) {
                mTRTCRemoteUserManager.remoteUserVideoAvailable(userId, streamType, renderView);
            }
            if (!userId.equals(mMainUserId)) {
                mMainUserId = userId;
            }
        } else {
            mTRTCRemoteUserManager.remoteUserVideoUnavailable(userId, streamType);
            if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB) {
                // 辅路直接移除画面，不会更新状态。主流需要更新状态，所以保留
                mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
                mTRTCRemoteUserManager.removeRemoteUser(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
            }
        }
        if (streamType == TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG) {
            // 根据当前视频流的状态，展示相关的 UI 逻辑。
            mTRTCVideoLayout.updateVideoStatus(userId, available);
        }
        if (SettingConfigHelper.getInstance().getVideoConfig().getCloudMixtureMode() == TRTCCloudDef.TRTC_TranscodingConfigMode_Manual) {
            mTRTCRemoteUserManager.updateCloudMixtureParams();
        }
    }

    @Override
    public void onEnterRoom(long elapsed) {
        if (elapsed >= 0) {
            Toast.makeText(this, getString(R.string.trtcdemo_ener_room_success_tips) + elapsed + getString(R.string.trtcdemo_mills), Toast.LENGTH_SHORT).show();
            // 发起云端混流
            mTRTCRemoteUserManager.updateCloudMixtureParams();
        } else {
            Toast.makeText(this, getString(R.string.trtcdemo_enter_room_fail_tips), Toast.LENGTH_SHORT).show();
            exitRoom();
        }
    }

    @Override
    public void onExitRoom(int reason) {

    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {
        Toast.makeText(this, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_LONG).show();
        Log.e(TAG, "onError: " + errMsg + "[" + errCode + "], exitRoom...");
        if (errCode != TXLiteAVCode.ERR_SERVER_CENTER_ANOTHER_USER_PUSH_SUB_VIDEO) {
            exitRoom();
            finish();
        }
    }

    @Override
    public void onRemoteUserEnterRoom(String userId) {
    }

    @Override
    public void onRemoteUserLeaveRoom(String userId, int reason) {
        mTRTCRemoteUserManager.removeRemoteUser(userId);
        mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
        mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);
        if (SettingConfigHelper.getInstance().getVideoConfig().getCloudMixtureMode() == TRTCCloudDef.TRTC_TranscodingConfigMode_Manual) {
            mTRTCRemoteUserManager.updateCloudMixtureParams();
        }
    }

    @Override
    public void onUserVideoAvailable(String userId, boolean available) {
        onVideoChange(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, available);
    }

    @Override
    public void onUserSubStreamAvailable(final String userId, boolean available) {
        onVideoChange(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB, available);
    }

    @Override
    public void onUserAudioAvailable(String userId, boolean available) {
    }

    @Override
    public void onFirstVideoFrame(String userId, int streamType, int width, int height) {
        Log.i(TAG, "onFirstVideoFrame: userId = " + userId + " streamType = " + streamType + " width = " + width + " height = " + height);
    }

    @Override
    public void onCameraDidReady() {
        OtherConfig config     = SettingConfigHelper.getInstance().getMoreConfig();
        if (config.isEnableFlash()) {
            mTRTCCloudManager.getDeviceManager().enableCameraTorch(config.isEnableFlash());
        }
    }

    @Override
    public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
        for (int i = 0; i < userVolumes.size(); ++i) {
            mTRTCVideoLayout.updateAudioVolume(userVolumes.get(i).userId, userVolumes.get(i).volume);
        }
    }

    @Override
    public void onStatistics(TRTCStatistics statics) {
    }

    @Override
    public void onConnectOtherRoom(final String userID, final int err, final String errMsg) {
        PkConfig pkConfig = SettingConfigHelper.getInstance().getPkConfig();
        stopLinkMicLoading();
        if (err == 0) {
            pkConfig.setConnected(true);
            Toast.makeText(this, getString(R.string.trtcdemo_pk_success_tips), Toast.LENGTH_LONG).show();
        } else {
            pkConfig.setConnected(false);
            Toast.makeText(this, getString(R.string.trtcdemo_pk_fail_tips), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDisConnectOtherRoom(final int err, final String errMsg) {
        PkConfig pkConfig = SettingConfigHelper.getInstance().getPkConfig();
        pkConfig.reset();
    }

    @Override
    public void onNetworkQuality(TRTCCloudDef.TRTCQuality localQuality, ArrayList<TRTCCloudDef.TRTCQuality> remoteQuality) {

    }

    @Override
    public void onAudioEffectFinished(int effectId, int code) {
        Toast.makeText(this, "effect id = " + effectId + getString(R.string.trtcdemo_play_end) + " code = " + code, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] message) {
        String msg = "";
        if (message != null && message.length > 0) {
            try {
                msg = new String(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ToastUtils.showLong(getString(R.string.trtcdemo_receive) + userId + getString(R.string.trtcdemo_message_end) + msg);
        }
    }

    @Override
    public void onRecvSEIMsg(String userId, byte[] data) {
        String msg = "";
        if (data != null && data.length > 0) {
            try {
                msg = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ToastUtils.showLong(getString(R.string.trtcdemo_receive) + userId +getString(R.string.trtcdemo_message_end) + msg);
        }
    }

    @Override
    public void onAudioVolumeEvaluationChange(boolean enable) {
        if (enable) {
            mTRTCVideoLayout.showAllAudioVolumeProgressBar();
        } else {
            mTRTCVideoLayout.hideAllAudioVolumeProgressBar();
        }
    }

    @Override
    public void onStartLinkMic() {
        startLinkMicLoading();
    }

    @Override
    public void onMuteLocalVideo(boolean isMute) {
        mTRTCVideoLayout.updateVideoStatus(mUserId, !isMute);
    }

    @Override
    public void onMuteLocalAudio(boolean isMute) {
        mImageEnableAudio.setImageResource(!isMute ? R.drawable.trtcdemo_mic_enable : R.drawable.trtcdemo_mic_disable);
    }

    @Override
    public void onSnapshotLocalView(final Bitmap bmp) {
        showSnapshotImage(bmp);
    }

    @Override
    public TXCloudVideoView getRemoteUserViewById(String roomId, String userId, int steamType) {
        TXCloudVideoView view = mTRTCVideoLayout.findCloudVideoView(userId, steamType);
        if (view == null) {
            view = mTRTCVideoLayout.allocCloudVideoView(userId, steamType);
        }
        return view;
    }

    @Override
    public void onRemoteViewStatusUpdate(String roomId, String userId, boolean enableVideo) {
        mTRTCVideoLayout.updateVideoStatus(userId, enableVideo);
    }

    @Override
    public void onSnapshotRemoteView(Bitmap bm) {
        showSnapshotImage(bm);
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            ToastUtils.showLong(getString(R.string.trtcdemo_play_success) + event);
        } else if (event == TXLiveConstants.PLAY_EVT_GET_MESSAGE) {
            if (param != null) {
                byte[] data       = param.getByteArray(TXLiveConstants.EVT_GET_MSG);
                String seiMessage = "";
                if (data != null && data.length > 0) {
                    try {
                        seiMessage = new String(data, "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ToastUtils.showLong(seiMessage);
            }
        } else if (event < 0) {
            ToastUtils.showLong(getString(R.string.trtcdemo_play_fail) + event);
        }
    }

    @Override
    public void onNetStatus(Bundle status) {

    }

    private void showSnapshotImage(final Bitmap bmp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bmp == null) {
                    ToastUtils.showLong(R.string.trtcdemo_capture_picture_fail);
                } else {
                    ImageView imageView = new ImageView(TRTCCallActivity.this);
                    imageView.setImageBitmap(bmp);
                    AlertDialog dialog = new AlertDialog.Builder(TRTCCallActivity.this)
                            .setView(imageView)
                            .setPositiveButton(R.string.trtcdemo_sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();

                    dialog.show();

                    final Button              positiveButton   = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    positiveButtonLL.gravity = Gravity.CENTER;
                    positiveButton.setLayoutParams(positiveButtonLL);
                }
            }
        });
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    @Override
    public void onAudioCapturePcm(byte[] data, int sampleRate, int channels, long timestampMs) {
        TRTCCloudDef.TRTCAudioFrame trtcAudioFrame = new TRTCCloudDef.TRTCAudioFrame();
        trtcAudioFrame.data         = data;
        trtcAudioFrame.sampleRate   = sampleRate;
        trtcAudioFrame.channel      = channels;

        mTRTCCloudManager.sendCustomAudioData(trtcAudioFrame);
    }
}