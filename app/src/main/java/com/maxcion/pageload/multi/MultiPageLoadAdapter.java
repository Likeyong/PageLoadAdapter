package com.maxcion.pageload.multi;

import com.maxcion.pageload.R;
import com.maxcion.pageloadadapter.BaseViewHolder;
import com.maxcion.pageloadadapter.PageLoadMultiRecyclerViewAdapter;

import java.util.List;

public class MultiPageLoadAdapter extends PageLoadMultiRecyclerViewAdapter<MultiData, BaseViewHolder> {
    public MultiPageLoadAdapter(List<MultiData> dataList) {
        super(dataList);
        addItemLayout(MultiData.TYPE_TEXT, R.layout.item_simple);
        addItemLayout(MultiData.TYPE_IMAGE, R.layout.item_multi_image);
        addItemLayout(MultiData.TYPE_VIDEO, R.layout.item_multi_video);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiData item) {
        switch (holder.getItemViewType()){
            case MultiData.TYPE_VIDEO:
                holder.setText(R.id.text, item.content);
                break;

            case MultiData.TYPE_IMAGE:
                holder.setText(R.id.text, item.content);
                break;

            case MultiData.TYPE_TEXT:
                holder.setText(R.id.text, item.content);
            default:
                break;
        }
    }
}
