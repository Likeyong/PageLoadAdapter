package com.maxcion.pageload;


import com.maxcion.pageloadadapter.ALoadMoreView;

public class CommonLoadMoreView extends ALoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.common_load_more;
    }

    @Override
    public int getLoadingViewId() {
        return R.id.ll_load_more_loading;
    }


    @Override
    public int getLoadingEndViewId() {
        return R.id.ll_load_more_end;
    }

    @Override
    public int getLoadingFailViewId() {
        return R.id.ll_load_more_fail;
    }
}
