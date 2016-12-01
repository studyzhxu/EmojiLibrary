package com.zhxu.emoji;

import android.app.Application;

/**
 * Created by xz on 2016/11/28.
 */
public class EmojiApplication extends Application {

    private static EmojiApplication instance ;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this ;
    }

    public static EmojiApplication getInstance(){
        return instance ;
    }
}
