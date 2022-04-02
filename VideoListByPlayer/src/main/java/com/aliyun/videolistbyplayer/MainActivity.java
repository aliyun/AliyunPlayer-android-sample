package com.aliyun.videolistbyplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aliyun.videolistbyplayer.utils.GlobalSettings;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalSettings.CACHE_DIR = getExternalCacheDir().getAbsolutePath() + File.separator + "Preload";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }

        ListPlayerController.init(this);

        Log.e(TAG, "onCreate: " + GlobalSettings.CACHE_DIR);
        findViewById(R.id.btn_open_after_cached).setOnClickListener(v -> {
            Intent intent = new Intent(this,VideoListActivity.class);
            startActivity(intent);
        });

    }
}