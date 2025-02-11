package com.tencent.liteav.trtcdemo.model.helper;

import com.tencent.liteav.trtcdemo.model.bean.RemoteUserConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 远程用户管理的帮助类，单利模式可以让模块内部共享一个管理器
 *
 * @author : xander
 * @date : 2021/5/25
 */
public class RemoteUserConfigHelper {
    private List<RemoteUserConfig> mRemoteUserConfigList;

    private RemoteUserConfigHelper() {
        mRemoteUserConfigList = new ArrayList<>();
    }

    public static RemoteUserConfigHelper getInstance() {
        return SingletonHolder.instance;
    }

    public void clear() {
        mRemoteUserConfigList.clear();
    }

    /**
     * 当有用户开启上行模式时，增加一个用户
     *
     * @param remoteUserConfig
     */
    public void addRemoteUser(RemoteUserConfig remoteUserConfig) {
        mRemoteUserConfigList.add(remoteUserConfig);
    }

    /**
     * 当有用户退房的时候，通过username比对删除一个用户
     *
     * @param username
     */
    public void removeRemoteUser(String username) {
        Iterator<RemoteUserConfig> iterator = mRemoteUserConfigList.iterator();
        while (iterator.hasNext()) {
            RemoteUserConfig userInfo = iterator.next();
            if (username.equals(userInfo.getUserName())) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * 当有用户退房的时候，通过username比对删除一个用户
     *
     * @param username
     */
    public void removeRemoteUser(String username, int streamId) {
        Iterator<RemoteUserConfig> iterator = mRemoteUserConfigList.iterator();
        while (iterator.hasNext()) {
            RemoteUserConfig userInfo = iterator.next();
            if (username.equals(userInfo.getUserName()) && streamId == userInfo.getStreamType()) {
                iterator.remove();
                break;
            }
        }
    }

    public List<RemoteUserConfig> getRemoteUserConfigList() {
        return mRemoteUserConfigList;
    }

    /**
     * 根据userId 获取用户配置
     *
     * @param userId 用户id
     * @return 与userId匹配的用户配置
     */
    public RemoteUserConfig getRemoteUser(String userId) {
        Iterator<RemoteUserConfig> iterator = mRemoteUserConfigList.iterator();
        while (iterator.hasNext()) {
            RemoteUserConfig userInfo = iterator.next();
            if (userId.equals(userInfo.getUserName())) {
                return userInfo;
            }
        }
        return null;
    }

    /**
     * 根据userId 和 主/辅流获取用户配置
     *
     * @param userId    用户id
     * @param steamType 主流/辅流
     * @return 与userId、steamType同时匹配的用户配置
     */
    public RemoteUserConfig getRemoteUser(String userId, int steamType) {
        Iterator<RemoteUserConfig> iterator = mRemoteUserConfigList.iterator();
        while (iterator.hasNext()) {
            RemoteUserConfig userInfo = iterator.next();
            if (userId.equals(userInfo.getUserName()) && steamType == userInfo.getStreamType()) {
                return userInfo;
            }
        }
        return null;
    }

    /**
     * 根据userId 设置是否为音频并发播放用户
     *
     * @param userId     用户id
     * @param isMustPlay 是否为音频必定播放
     */
    public void setRemoteUserAudioParallelMustPlay(String userId, boolean isMustPlay) {
        Iterator<RemoteUserConfig> iterator = mRemoteUserConfigList.iterator();
        while (iterator.hasNext()) {
            RemoteUserConfig userInfo = iterator.next();
            if (userId.equals(userInfo.getUserName())) {
                userInfo.setAudioParallelMustPlay(isMustPlay);
                break;
            }
        }
    }


    private static class SingletonHolder {
        private static RemoteUserConfigHelper instance = new RemoteUserConfigHelper();
    }
}
