package com.zhxu.emojilibrary;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取表情配置文件
 * Created by xz on 2016/11/28.
 */
public class EmojiFileUtils {

    public static List<String> getEmojiFile(Context context){
        InputStream in = null ;
        BufferedReader br = null ;
        try {
            List<String> emojiList = new ArrayList<>() ;
            in = context.getAssets().open("emoji");
            br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String line = null ;
            while((line = br.readLine()) != null){
                emojiList.add(line);
            }
            return emojiList ;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(in != null) {
                    in.close();
                }
                if(br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null ;
    }
}
