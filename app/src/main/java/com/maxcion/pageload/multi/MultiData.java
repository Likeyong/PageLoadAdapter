package com.maxcion.pageload.multi;

import com.maxcion.pageloadadapter.IMultiItem;

public class MultiData implements IMultiItem {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    public int type;
    public String content;

    public MultiData(int type, String content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
