package com.zhxu.emojilibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情工具类
 *      初始化表情
 *      解析表情
 *      将表情添加到输入框中
 * Created by xz on 2016/11/28.
 */
public class EmojiConversionUtils {

    /**
     * 保存于内存中的表情集合
     */
    private List<EmojiEntry> emojis = new ArrayList<EmojiEntry>();

    /**
     * 表情分页的结果集合
     */
    public List<List<EmojiEntry>> emojiLists = new ArrayList<List<EmojiEntry>>();

    /**
     * 保存于内存中的表情HashMap
     */
    private HashMap<String, String> emojiMap = new HashMap<String, String>();

    private final int pageSize = 20 ;

    private static EmojiConversionUtils instance ;
    private EmojiConversionUtils(){}

    public static EmojiConversionUtils getInstance(){
        if(instance == null){
            synchronized (EmojiConversionUtils.class){
                if(instance == null){
                    instance = new EmojiConversionUtils();
                }
            }
        }
        return instance ;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        parseData(EmojiFileUtils.getEmojiFile(context),context);
    }

    /**
     * 通过正则匹配字符串得到一个SpannableString对象
     * @param context
     * @param str
     * @return
     */
    public SpannableString getExpressionString(Context context, String str) {
        SpannableString spannableString = new SpannableString(str);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        String zhengze = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }

    /**
     * 添加表情
     *      将表情列表中的表情添加到输入框中
     * @param context
     * @param imgId
     * @param spannableString
     * @return
     */
    public SpannableString addFace(Context context, int imgId,
                                   String spannableString) {
        if (TextUtils.isEmpty(spannableString)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 对SpannableString进行正则判断，如果符合就用图片代替
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     */
    private void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws Exception{
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            String value = emojiMap.get(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            int resId = context.getResources().getIdentifier(value, "mipmap",context.getPackageName());
            if(resId == 0) {
                resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
            }
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                bitmap = Bitmap.createScaledBitmap(bitmap, getEmojiSize(context), getEmojiSize(context), true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                @SuppressWarnings("deprecation")
                ImageSpan imageSpan = new ImageSpan(bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }

        }
    }

    /** 解析数据 */
    private synchronized void parseData(List<String> data,Context context){
        System.out.println("emojiListts:"+emojiLists.size());
        if(emojiLists.size() > 0)return ;
        if(data == null){
            return ;
        }
        System.out.println("data.size:"+data.size());
        try {
            for (String str : data) {
                String[] emojiText = str.split(",");
                String fileName = emojiText[0]
                        .substring(0, emojiText[0].lastIndexOf("."));
                //此处将表情放入map集合中，key是描述文本（[大哭]），value是图片名称
                emojiMap.put(emojiText[1], fileName);
                int resID = context.getResources().getIdentifier(fileName, "mipmap", context.getPackageName());
                if (resID != 0) {
                    EmojiEntry emojiEntry = new EmojiEntry();
                    emojiEntry.setId(resID);
                    emojiEntry.setCharacter(emojiText[1]);
                    emojiEntry.setFaceName(fileName);
                    emojis.add(emojiEntry);
                }
            }
            //获取页数
            int pageCount = (int) Math.ceil(emojis.size() / pageSize);
            for (int i = 0; i < pageCount; i++) {
                //将每一页的表情集合放入集合中
                emojiLists.add(getData(i));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取每一页的表情集合 */
    private List<EmojiEntry> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }

        List<EmojiEntry> list = new ArrayList<EmojiEntry>();
        List<EmojiEntry> sublist = emojis.subList(startIndex, endIndex);
        list.addAll(sublist);
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                EmojiEntry object = new EmojiEntry();
                list.add(object);
            }
        }
        if (list.size() == pageSize) {
            EmojiEntry object = new EmojiEntry();
            object.setId(R.mipmap.emoji_item_delete);
            list.add(object);
        }
        return list;
    }

    //适配安卓屏幕分辨率
    private int getEmojiSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int size = wm.getDefaultDisplay().getHeight() * 200 / 1920;
        return size;

    }
}
