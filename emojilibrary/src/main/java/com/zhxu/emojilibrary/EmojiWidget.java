package com.zhxu.emojilibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xz on 2016/11/28.
 */
public class EmojiWidget implements AdapterView.OnItemClickListener {

    private Context context ;
    private Activity activity ;

    /** 显示表情页的ViewPager */
    private ViewPager emoji_viewpage;
    /** 游标显示布局 */
    private LinearLayout emoji_cursor;
    /** 输入框*/
    private EditText et_sendmessage;
    /** 标志 */
    private int UI_FLAG;
    /** 更新UI */
    private Handler mUIHandler;

    /** 表情集合 */
    private List<List<EmojiEntry>> emojis;
    /** 表情页界面集合 */
    private ArrayList<View> emojiPageViews;
    /** 表情数据填充器 */
    private List<EmojiAdapter> emojiAdapters;
    /** 游标点集合 */
    private ArrayList<ImageView> emojiCursorViews;

    /** 当前表情页 */
    private int current = 0;

    public EmojiWidget(Activity activity , Context context , EditText et_sendmessage,int UI_FLAG,Handler mUIHandler){
        this.activity = activity ;
        this.context = context ;
        this.et_sendmessage = et_sendmessage ;
        this.UI_FLAG = UI_FLAG ;
        this.mUIHandler = mUIHandler ;

        emojis = EmojiConversionUtils.getInstance().emojiLists ;
        if(emojis == null || emojis.size() == 0){
            EmojiConversionUtils.getInstance().init(context);
            emojis = EmojiConversionUtils.getInstance().emojiLists ;
        }
        initView() ;
        initViewPage() ;
        initPoint() ;
        initData() ;
    }

    /** 初始化控件 */
    private void initView() {
        this.emoji_viewpage = (ViewPager) activity.findViewById(R.id.emoji_viewpage);
        this.emoji_cursor = (LinearLayout) activity.findViewById(R.id.emoji_cursor);
    }

    /** 初始化显示表情的ViewPage */
    private void initViewPage() {
        emojiPageViews = new ArrayList<>() ;
        // 左侧添加空页
        View nullViewLeft = new View(context);
        // 设置透明背景
        nullViewLeft.setBackgroundColor(Color.TRANSPARENT);
        emojiPageViews.add(nullViewLeft);
        // 中间添加表情页
        this.emojiAdapters = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            EmojiAdapter adapter = new EmojiAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            emojiAdapters.add(adapter);
            view.setOnItemClickListener(this);
            //设置列数
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            //设置垂直和水平的间距
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            //设置缩放模式
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            //去除拖动时的默认背景
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);

            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            this.emojiPageViews.add(view);
        }
        // 右侧添加空页面
        View nullViewRight = new View(context);
        // 设置透明背景
        nullViewRight.setBackgroundColor(Color.TRANSPARENT);
        this.emojiPageViews.add(nullViewRight);
    }

    /** 初始化游标 */
    private void initPoint() {
        this.emojiCursorViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < emojiPageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.mipmap.emoji_cursor_1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            emoji_cursor.addView(imageView, layoutParams);
            //此处是为了解决最左和最右侧空白区域显示游标的问题，如果没有空白区域则此处可以不要
            if (i == 0 || i == emojiPageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.mipmap.emoji_cursor_2);
            }
            this.emojiCursorViews.add(imageView);
        }
    }

    /** 初始化数据 */
    private void initData() {
        emoji_viewpage.setAdapter(new ViewPagerAdapter(this.emojiPageViews));
        emoji_viewpage.setCurrentItem(1);
        current = 0;
        this.emoji_viewpage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                refreshUI(PAGE_SELECTED, position); // 页面更新
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /** 刷新UI向主线程*/
    private void refreshUI(int arg1,Object obj) {
        Message msg = mUIHandler.obtainMessage();
        msg.what = UI_FLAG;
        msg.arg1 = arg1;
        msg.obj = obj;
        msg.sendToTarget();
    }

    public static final int PAGE_SELECTED = 0x1024;

    /** 用来对接UI的刷新,保证始终在主线程*/
    public void refreshWidgetUI(Message msg) {
        switch(msg.arg1) {
            case PAGE_SELECTED:onPageSelected((Integer)msg.obj);
        }
    }

    /** 当切换页面时会回调该方法*/
    private void onPageSelected(int position) {
        current = position - 1;
        // 描绘分页点
        drawPoint(emojiCursorViews, position);
        // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
        if (position == emojiCursorViews.size() - 1 || position == 0) {
            if (position == 0) {
                emoji_viewpage.setCurrentItem(position + 1);// 第二屏 会再次实现该回调方法实现跳转.
                emojiCursorViews.get(1).setBackgroundResource(R.mipmap.emoji_cursor_2);
            } else {
                emoji_viewpage.setCurrentItem(position - 1);// 倒数第二屏
                emojiCursorViews.get(position - 1).setBackgroundResource(R.mipmap.emoji_cursor_2);
            }
        }
    }

    /** 绘制游标背景 */
    private void drawPoint(ArrayList<ImageView> emojiCursorViews, int index) {
        for (int i = 1; i < emojiCursorViews.size(); i++) {
            if (index == i) {
                emojiCursorViews.get(i).setBackgroundResource(R.mipmap.emoji_cursor_2);
            } else {
                emojiCursorViews.get(i).setBackgroundResource(R.mipmap.emoji_cursor_1);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EmojiEntry emoji = (EmojiEntry) this.emojiAdapters.get(this.current).getItem(position);
        //点击删除按钮删除
        if (emoji.getId() == R.mipmap.emoji_item_delete) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {

            SpannableString spannableString = EmojiConversionUtils.getInstance()
                    .addFace(this.context, emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }
    }
}
