package com.aliyun.playerview.java;

import com.aliyun.player.IPlayer;

interface DialogFragmentListener {
    void onVolumeChanged(float volume);
    void onLoopChanged(boolean isLoop);
    void onMuteChanged(boolean isMute);
    void onAccurateSeekChanged(boolean isAccurateSeek);
    void onMirrorModeChanged(IPlayer.MirrorMode mirrorMode);
    void onScaleModeChanged(IPlayer.ScaleMode scaleMode);
    void onRotateModeChanged(IPlayer.RotateMode rotateMode);
    void onSpeedChanged(float speed);
}
