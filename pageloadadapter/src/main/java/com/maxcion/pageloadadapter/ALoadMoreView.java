package com.maxcion.pageloadadapter;


public abstract class ALoadMoreView {
    public static final int STATE_LOAD_MORE_DEFAULT = 0;
    public static final int STATE_LOAD_MORE_LOADING = 1;
    public static final int STATE_LOAD_MORE_FAIL = 2;
    public static final int STATE_LOAD_MORE_END = 3;

    private int state = STATE_LOAD_MORE_DEFAULT;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void convert(BaseViewHolder holder) {
        switch (state) {
            case STATE_LOAD_MORE_DEFAULT:
            case STATE_LOAD_MORE_LOADING:
                holder.setGone(getLoadingViewId(), false);
                holder.setGone(getLoadingEndViewId(), true);
                holder.setGone(getLoadingFailViewId(), true);
                break;

            case STATE_LOAD_MORE_FAIL:
                holder.setGone(getLoadingViewId(), true);
                holder.setGone(getLoadingEndViewId(), true);
                holder.setGone(getLoadingFailViewId(), false);
                break;

            case STATE_LOAD_MORE_END:
                holder.setGone(getLoadingViewId(), true);
                holder.setGone(getLoadingEndViewId(), false);
                holder.setGone(getLoadingFailViewId(), true);
                break;

        }

    }

    public abstract int getLayoutId();

    public abstract int getLoadingViewId();

    public abstract int getLoadingEndViewId();

    public abstract int getLoadingFailViewId();


}
