package com.aliyun.playerconfig.view;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class MyAppCompatEditText extends AppCompatEditText {
    
    public MyAppCompatEditText(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MyAppCompatEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyAppCompatEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
