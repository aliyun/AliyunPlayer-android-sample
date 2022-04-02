package com.aliyun.videolistbyplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.videolistbyplayer.adapter.CustomLayoutManager;
import com.aliyun.videolistbyplayer.adapter.RecyclerViewAdapter;
import com.bumptech.glide.Glide;

public class VideoListActivity extends AppCompatActivity implements CustomLayoutManager.OnViewPagerListener,
        RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private CustomLayoutManager mCustomLayoutManager;
    private ListPlayerController mController;
    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.NoActionTheme);

        setContentView(R.layout.activity_video_list);

        mController = new ListPlayerController(this);
        initRecyclerView();
        initTextureView();
    }

    private void initRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(this);
        mCustomLayoutManager = new CustomLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mCustomLayoutManager.setItemPrefetchEnabled(true);
        mCustomLayoutManager.setOnViewPagerListener(this);
        mRecyclerView.setLayoutManager(mCustomLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setData(mController.getData());
        mRecyclerViewAdapter.setOnItemClickListener(this);
    }

    private void initTextureView(){
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                Surface surface = new Surface(surfaceTexture);
                mController.setRenderView(null);
                mController.setRenderView(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                mController.surfacChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) { }
        });
    }

    /**
     * hide Cover ImageView
     */
    public void hideCoverView(){
        int lastVisibleItemPosition = mCustomLayoutManager.findLastVisibleItemPosition();
        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
        if(viewHolderForAdapterPosition != null){
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.INVISIBLE);
        }
    }

    /**
     * show Cover ImageView
     */
    public void showCoverView(int position){
        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if(viewHolderForAdapterPosition != null){
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.VISIBLE);
        }
    }

    /**
     * add TextureView
     */
    private void removeAndAddView(int position){
        ViewParent parent = mTextureView.getParent();
        if (parent instanceof FrameLayout) {
            ((ViewGroup) parent).removeView(mTextureView);
        }
        RecyclerViewAdapter.RecyclerViewHolder holder = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
        if (holder != null) {
            holder.getFrameLayout().addView(mTextureView);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        Toast.makeText(this, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, int itemPosition) {
        mController.startPlay(position,itemPosition);
    }

    @Override
    public void onPageShow(int position) {
        showCoverView(position);
    }

    @Override
    public void onPageSelected(int position) {
        removeAndAddView(position);
        mController.start(position);
    }

    @Override
    public void onPageRelease() { }

    @Override
    protected void onDestroy() {
        Glide.get(VideoListActivity.this).clearMemory();
        new Thread(() -> Glide.get(VideoListActivity.this).clearDiskCache()).start();

        super.onDestroy();

        mController.destroy();
    }
}