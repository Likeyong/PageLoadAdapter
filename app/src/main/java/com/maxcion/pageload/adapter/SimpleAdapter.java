package com.maxcion.pageload.adapter;

import com.maxcion.pageload.R;
import com.maxcion.pageloadadapter.BaseViewHolder;
import com.maxcion.pageloadadapter.PageLoadRecyclerVewAdapter;

import java.util.List;

public class SimpleAdapter extends PageLoadRecyclerVewAdapter<String> {
    public SimpleAdapter(List<String> dataList) {
        super(dataList);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.text, item);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_simple;
    }
}
