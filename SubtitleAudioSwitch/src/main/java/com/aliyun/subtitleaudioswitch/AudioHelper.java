package com.aliyun.subtitleaudioswitch;

import android.util.Log;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

public class AudioHelper {

    private static final String TAG = "AudioHelper";

    private final List<String> mAudioList = new ArrayList<>();
    private final List<TrackInfo> mTrackInfoList = new ArrayList<>();

    /**
     * Build bitrate array
     * @param trackInfos Streaming information of media，Information returned by AliPlayer SDK
     */
    public void setTrackInfos(List<TrackInfo> trackInfos) {
        for (TrackInfo trackInfo : trackInfos) {
            if(trackInfo.getType() == TrackInfo.Type.TYPE_AUDIO){
                String audioLang = trackInfo.getAudioLang();
                mTrackInfoList.add(trackInfo);
                mAudioList.add(audioLang);
                Log.e(TAG, "setOnTrackReadyListener: " + audioLang);
            }
        }
    }


    public Object[] getAudioArr(){
        return mAudioList.toArray();
    }

    public Object[] getTrackInfoArr(){
        return mTrackInfoList.toArray();
    }

    public String getAudio(int index){
        return mAudioList.get(index);
    }

    public TrackInfo getTrackInfo(int index){
        return mTrackInfoList.get(index);
    }
}
