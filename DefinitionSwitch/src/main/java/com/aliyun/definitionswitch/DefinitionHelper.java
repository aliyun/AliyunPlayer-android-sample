package com.aliyun.definitionswitch;

import android.util.Log;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

public class DefinitionHelper {

    private static final String TAG = "DefinitionHelper";

    private final List<String> mDefinitionList = new ArrayList<>();
    private final List<TrackInfo> mTrackInfoList = new ArrayList<>();

    /**
     * Build definition array
     * @param trackInfos Streaming information of media，Information returned by AliPlayer SDK
     */
    public void setTrackInfos(List<TrackInfo> trackInfos) {
        for (TrackInfo trackInfo : trackInfos) {
            if(trackInfo.getType() == TrackInfo.Type.TYPE_VOD){
                String vodDefinition = trackInfo.getVodDefinition();
                mTrackInfoList.add(trackInfo);
                mDefinitionList.add(vodDefinition);
                Log.e(TAG, "setOnTrackReadyListener: " + vodDefinition);
            }
        }
    }

    public Object[] getDefinitionArr(){
        return mDefinitionList.toArray();
    }

    public Object[] getTrackInfoArr(){
        return mTrackInfoList.toArray();
    }

    public String getDefinition(int index){
        return mDefinitionList.get(index);
    }

    public TrackInfo getTrackInfo(int index){
        return mTrackInfoList.get(index);
    }
}
