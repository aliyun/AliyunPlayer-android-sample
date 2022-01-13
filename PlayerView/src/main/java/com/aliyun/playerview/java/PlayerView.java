package com.aliyun.playerview.java;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.playerview.R;

import java.lang.ref.WeakReference;

public class PlayerView extends ConstraintLayout implements View.OnClickListener {
    private static final int WHAT_HIDE = 0;
    private static final int DELAY_TIME = 5 * 1000;

    private Group mGroup;
    private Context mContext;
    private AliPlayer mAliPlayer;
    private ImageView mMoreImageView;
    private AppCompatSeekBar mSeekBar;
    private boolean mIsPlaying = false;
    private TextView mDurationTextView;
    private ProgressBar mLoadingProgress;
    private ImageView mPlayStateImageView;
    private boolean mIsAccurateSeek = false;
    private TextView mCurrentPositionTextView;
    private ConstraintLayout mConstraintLayoutRootView;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = null;
    private final MoreDialogFragment mMoreDialogFragment = new MoreDialogFragment();

    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<PlayerView> mPlayerViewWeakReference;

        public MyHandler(PlayerView controlView) {
            mPlayerViewWeakReference = new WeakReference<>(controlView);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerView playerView = mPlayerViewWeakReference.get();
            if(playerView != null && playerView.mGroup.getVisibility() == View.VISIBLE){
                playerView.mGroup.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    }

    public PlayerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        autoHideGroup();

        this.mContext = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);

        mGroup = inflate.findViewById(R.id.group);
        mSeekBar = inflate.findViewById(R.id.seekBar);
        mMoreImageView = inflate.findViewById(R.id.iv_more);
        mDurationTextView = inflate.findViewById(R.id.tv_duration);
        mPlayStateImageView = inflate.findViewById(R.id.iv_play_state);
        mLoadingProgress = inflate.findViewById(R.id.loading_progress);
        mConstraintLayoutRootView = inflate.findViewById(R.id.root_view);
        mCurrentPositionTextView = inflate.findViewById(R.id.tv_current_position);

        initListener();
    }

    private void autoHideGroup(){
        mHandler.removeMessages(WHAT_HIDE);
        mHandler.sendEmptyMessageDelayed(WHAT_HIDE, DELAY_TIME);
    }

    private void initListener(){
        mPlayStateImageView.setOnClickListener(this);
        mMoreImageView.setOnClickListener(this);
        mConstraintLayoutRootView.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mOnSeekBarChangeListener != null){
                    mOnSeekBarChangeListener.onProgressChanged(seekBar,progress,fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(WHAT_HIDE);
                if(mOnSeekBarChangeListener != null){
                    mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                autoHideGroup();
                mAliPlayer.seekTo(seekBar.getProgress(),mIsAccurateSeek ? IPlayer.SeekMode.Accurate : IPlayer.SeekMode.Inaccurate);
                if(mOnSeekBarChangeListener != null){
                    mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });

        mMoreDialogFragment.setDialogFragmentListener(new DialogFragmentListener() {
            @Override
            public void onVolumeChanged(float volume) {
                mAliPlayer.setVolume(volume);
            }

            @Override
            public void onLoopChanged(boolean isLoop) {
                mAliPlayer.setLoop(isLoop);
            }

            @Override
            public void onMuteChanged(boolean isMute) {
                mAliPlayer.setMute(isMute);
            }

            @Override
            public void onAccurateSeekChanged(boolean isAccurateSeek) {
                mIsAccurateSeek = isAccurateSeek;
            }

            @Override
            public void onMirrorModeChanged(IPlayer.MirrorMode mirrorMode) {
                mAliPlayer.setMirrorMode(mirrorMode);
            }

            @Override
            public void onScaleModeChanged(IPlayer.ScaleMode scaleMode) {
                mAliPlayer.setScaleMode(scaleMode);
            }

            @Override
            public void onRotateModeChanged(IPlayer.RotateMode rotateMode) {
                mAliPlayer.setRotateMode(rotateMode);
            }

            @Override
            public void onSpeedChanged(float speed) {
                mAliPlayer.setSpeed(speed);
            }
        });
    }

    private void initSurfaceView(){
        SurfaceView mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if(mAliPlayer != null){
                    mAliPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                if(mAliPlayer != null){
                    mAliPlayer.surfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if(mAliPlayer != null){
                    mAliPlayer.setDisplay(null);
                }
            }
        });
        addRenderView(mSurfaceView);
    }

    private void initTextureView(){
        TextureView mTextureView = new TextureView(mContext);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                if(mAliPlayer != null){
                    Surface surface = new Surface(surfaceTexture);
                    mAliPlayer.setSurface(surface);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                if(mAliPlayer != null){
                    mAliPlayer.surfaceChanged();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                if(mAliPlayer != null){
                    mAliPlayer.setSurface(null);
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) { }
        });
        addRenderView(mTextureView);
    }

    private void addRenderView(View view){
        mConstraintLayoutRootView.addView(view,0);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftToLeft = R.id.root_view;
        layoutParams.topToTop = R.id.root_view;
        layoutParams.rightToRight = R.id.root_view;
        layoutParams.bottomToBottom = R.id.root_view;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        if(v == mMoreImageView){
            Context context = getContext();
            if(context instanceof FragmentActivity){
                FragmentManager supportFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                mMoreDialogFragment.show(supportFragmentManager,"aliyun-player-dialogFragment");
            }

        }else if(v == mPlayStateImageView){
            if(mIsPlaying){
                mAliPlayer.pause();
            }else{
                mAliPlayer.start();
            }
            isPlaying(!mIsPlaying);
        }else if(v == mConstraintLayoutRootView){
            int visibility = mGroup.getVisibility();
            if(visibility == View.VISIBLE){
                mGroup.setVisibility(View.GONE);
            }else{
                mGroup.setVisibility(View.VISIBLE);
                autoHideGroup();
            }
        }
    }

    public void bind(@NonNull AliPlayer aliPlayer,RenderType renderType){
        this.mAliPlayer = aliPlayer;
        if(renderType == RenderType.SURFACE_VIEW){
            initSurfaceView();
        }else{
            initTextureView();
        }
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        this.mOnSeekBarChangeListener = listener;
    }

    public void isPlaying(boolean isPlaying){
        this.mIsPlaying = isPlaying;
        mPlayStateImageView.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    public void isLoading(boolean isLoading) {
        mLoadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    public void setBufferPosition(long bufferPosition){
        mSeekBar.setSecondaryProgress((int) bufferPosition);
    }

    public void setCurrentPosition(long currentPosition){
        mSeekBar.setProgress((int) currentPosition);
        mCurrentPositionTextView.setText(TimeFormater.formatMs(currentPosition));
    }

    public void setDuration(long duration){
        mSeekBar.setMax((int) duration);
        mDurationTextView.setText(TimeFormater.formatMs(duration));
    }

    public long getDuration(){
        return mSeekBar.getMax();
    }
}
