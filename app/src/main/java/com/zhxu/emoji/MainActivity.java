package com.zhxu.emoji;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhxu.emojilibrary.EmojiWidget;


public class MainActivity extends AppCompatActivity {

    private TextView tv_send ;
    private EditText et_content ;
    private ImageView iv_emoji ;
    private ListView chat_listview ;
    private LinearLayout ll_emoji ;

    private EmojiWidget emojiWidget;

    private final static int ON_EMOJI_CHANGE = 0xc1;

    /**
     * 更新的Handler
     */
    private Handler mUIHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ON_EMOJI_CHANGE: { // 监听表情界面的变化
                    emojiWidget.refreshWidgetUI(msg);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        initView();

        emojiWidget = new EmojiWidget(this,this,et_content,ON_EMOJI_CHANGE,mUIHandler);
    }

    public void initView(){
        tv_send = (TextView) findViewById(R.id.tv_send);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        chat_listview = (ListView) findViewById(R.id.chat_listview);
        ll_emoji = (LinearLayout) findViewById(R.id.ll_emoji);

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        iv_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_emoji.getVisibility() == View.VISIBLE){
                    ll_emoji.setVisibility(View.GONE);
                }else{
                    ll_emoji.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void sendMessage(){
        String content = et_content.getText().toString();

    }
}
