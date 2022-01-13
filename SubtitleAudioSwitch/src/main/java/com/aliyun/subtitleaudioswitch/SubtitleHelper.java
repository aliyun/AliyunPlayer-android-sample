package com.aliyun.subtitleaudioswitch;

import android.util.Log;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

public class SubtitleHelper {

    private static final String TAG = "SubtitleHelper";

    private final List<String> mSubtitleList = new ArrayList<>();
    private final List<TrackInfo> mTrackInfoList = new ArrayList<>();

    /**
     * Build bitrate array
     * @param trackInfos Streaming information of media，Information returned by AliPlayer SDK
     */
    public void setTrackInfos(List<TrackInfo> trackInfos) {
        for (TrackInfo trackInfo : trackInfos) {
            if(trackInfo.getType() == TrackInfo.Type.TYPE_SUBTITLE){
                String subtitleLang = trackInfo.getSubtitleLang();
                mTrackInfoList.add(trackInfo);
                mSubtitleList.add(subtitleLang);
                Log.e(TAG, "setOnTrackReadyListener: " + subtitleLang);
            }
        }
    }


    public Object[] getSubtitleArr(){
        return mSubtitleList.toArray();
    }

    public Object[] getTrackInfoArr(){
        return mTrackInfoList.toArray();
    }

    public String getSubtitle(int index){
        return mSubtitleList.get(index);
    }

    public TrackInfo getTrackInfo(int index){
        return mTrackInfoList.get(index);
    }
}
