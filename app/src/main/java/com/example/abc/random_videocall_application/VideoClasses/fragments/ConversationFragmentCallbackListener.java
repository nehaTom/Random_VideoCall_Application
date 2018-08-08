package com.example.abc.random_videocall_application.VideoClasses.fragments;

import org.webrtc.CameraVideoCapturer;

/**
 * Created by tereha on 23.05.16.
 */
public interface ConversationFragmentCallbackListener {

    void addTCClientConnectionCallback(VideoConversationFragment clientConnectionCallbacks);
    void removeRTCClientConnectionCallback(VideoConversationFragment clientConnectionCallbacks);

    void addRTCSessionEventsCallback(VideoConversationFragment eventsCallback);
    void removeRTCSessionEventsCallback(VideoConversationFragment eventsCallback);

    void addCurrentCallStateCallback(BaseConversationFragment currentCallStateCallback);
    void removeCurrentCallStateCallback(BaseConversationFragment currentCallStateCallback);

    void addOnChangeAudioDeviceCallback(AudioConversationFragment onChangeDynamicCallback);
    void removeOnChangeAudioDeviceCallback(AudioConversationFragment onChangeDynamicCallback);

    void onSetAudioEnabled(boolean isAudioEnabled);

    void onSetVideoEnabled(boolean isNeedEnableCam);

    void onSwitchAudio();

    void onHangUpCurrentSession();

    void onStartScreenSharing();

    void onSwitchCamera(CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler);
}
