package com.aliyun.bitrateswitch;

import android.util.Log;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

public class BitrateHelper {

    private static final String TAG = "BitrateHelper";

    private final List<String> mBitrateList = new ArrayList<>();
    private final List<TrackInfo> mTrackInfoList = new ArrayList<>();

    /**
     * Build bitrate array
     * @param trackInfos Streaming information of media，Information returned by AliPlayer SDK
     */
    public void setTrackInfos(List<TrackInfo> trackInfos) {
        initAutoItem();

        for (TrackInfo trackInfo : trackInfos) {
            if(trackInfo.getType() == TrackInfo.Type.TYPE_VIDEO){
                int bitrate = trackInfo.getVideoBitrate();
                mTrackInfoList.add(trackInfo);
                mBitrateList.add(String.valueOf(bitrate));
                Log.e(TAG, "setOnTrackReadyListener: " + bitrate);
            }
        }
    }

    private void initAutoItem(){
        //auto bitrate
        TrackInfo trackInfo = new TrackInfo();
        trackInfo.mType = TrackInfo.Type.TYPE_VIDEO;
        trackInfo.videoBitrate = TrackInfo.AUTO_SELECT_INDEX;
        mTrackInfoList.add(trackInfo);

        mBitrateList.add("AUTO_SELECT_INDEX");
    }

    public Object[] getBitrateArr(){
        return mBitrateList.toArray();
    }

    public Object[] getTrackInfoArr(){
        return mTrackInfoList.toArray();
    }

    public String getBitrate(int index){
        return mBitrateList.get(index);
    }

    public TrackInfo getTrackInfo(int index){
        return mTrackInfoList.get(index);
    }
}
