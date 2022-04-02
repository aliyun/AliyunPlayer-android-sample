package com.aliyun.videolistbyplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.videolistbyplayer.R;
import com.aliyun.videolistbyplayer.bean.ListVideoBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context mContext;
    private List<ListVideoBean> mData;
    public OnItemClickListener mListener;

    public RecyclerViewAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<ListVideoBean> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item, parent, false);
        return new RecyclerViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        ListVideoBean listVideoBean = mData.get(position);
        Glide.with(mContext).asBitmap()
                .dontAnimate()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565)
                .load(listVideoBean.getCoverURL())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mIvCover);

        ArrayList<ListVideoBean.HListVideoBean> horizontalVideoData = listVideoBean.getHorizontalVideoData();

        Glide.with(mContext).asBitmap()
                .priority(Priority.LOW)
                .dontAnimate()
                .thumbnail(0.3f)
                .format(DecodeFormat.PREFER_RGB_565)
                .load(horizontalVideoData.get(0).getUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mIvCover1);

        Glide.with(mContext).asBitmap()
                .priority(Priority.LOW)
                .dontAnimate()
                .thumbnail(0.3f)
                .format(DecodeFormat.PREFER_RGB_565)
                .load(horizontalVideoData.get(1).getUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mIvCover2);

        Glide.with(mContext).asBitmap()
                .priority(Priority.LOW)
                .dontAnimate()
                .thumbnail(0.3f)
                .format(DecodeFormat.PREFER_RGB_565)
                .load(horizontalVideoData.get(2).getUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mIvCover3);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private final FrameLayout mRootFrameLayout;
        private final ImageView mIvCover;
        private final ImageView mIvCover1;
        private final ImageView mIvCover2;
        private final ImageView mIvCover3;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mRootFrameLayout = itemView.findViewById(R.id.fm_root);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mIvCover1 = itemView.findViewById(R.id.iv_cover_1);
            mIvCover2 = itemView.findViewById(R.id.iv_cover_2);
            mIvCover3 = itemView.findViewById(R.id.iv_cover_3);

            mIvCover1.setOnClickListener(v -> {
                if(mListener != null){
                    int position = getAdapterPosition();
                    mListener.onItemClick(position,0);
                }
            });

            mIvCover2.setOnClickListener(v -> {
                if(mListener != null){
                    int position = getAdapterPosition();
                    mListener.onItemClick(position,1);
                }
            });

            mIvCover3.setOnClickListener(v -> {
                if(mListener != null){
                    int position = getAdapterPosition();
                    mListener.onItemClick(position,2);
                }
            });
        }

        public ImageView getCoverView() {
            return mIvCover;
        }

        public FrameLayout getFrameLayout(){
            return mRootFrameLayout;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position,int itemPosition);
    }
}
