package com.zhxu.emojilibrary;

/**
 * 表情对象
 * Created by xz on 2016/11/28.
 */
public class EmojiEntry {

    /** 表情资源图片对应的id */
    private int id ;

    /** 表情资源对应的文字描述 */
    private String character ;

    /** 表情对象对应的文件名 */
    private String faceName ;

    public void setId(int id) {
        this.id = id;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public int getId() {
        return id;
    }

    public String getCharacter() {
        return character;
    }

    public String getFaceName() {
        return faceName;
    }
}
