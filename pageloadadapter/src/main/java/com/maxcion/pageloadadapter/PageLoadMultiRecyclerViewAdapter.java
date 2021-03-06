package com.maxcion.pageloadadapter;

import android.util.SparseIntArray;
import android.view.ViewGroup;



import java.util.List;


/**
 * 如果要使用recyclerView的多布局  就继承自这个累,只用实现convert(BaseViewHolder holder, T item)
 *
 *
 * 数据源bean 必须实现 IMultiItem 接口
 *
 *
 *
 * 并在构造器中 进行 item type 与布局的绑定  通过 addItemLayout(int itemType,  int layoutId)
 *
 * 类似下面的代码
 *      public SelectedAdapter(List<FinalSelectedBean> dataList) {
 *         super(dataList);
 *
 *         addItemLayout(FinalSelectedBean.TYPE_TITLE, R.layout.item_selected_title_type);
 *         addItemLayout(FinalSelectedBean.TYPE_SINGLE_SELECTED, R.layout.item_selected_content);
 *         addItemLayout(FinalSelectedBean.TYPE_MULTIPLE_SELECTED, R.layout.item_selected_content);
 *     }
 *
 *
 *
 */

public abstract class PageLoadMultiRecyclerViewAdapter<T extends IMultiItem, V extends BaseViewHolder> extends PageLoadRecyclerVewAdapter<T> {

    private SparseIntArray mItemLayouts;

    public PageLoadMultiRecyclerViewAdapter(List<T> dataList) {
        super(dataList);
    }

    @Override
    protected abstract void convert(BaseViewHolder holder, T item);

    @Override
    protected int getItemLayoutId() {
        return 0;
    }

    protected void addItemLayout(int itemType,  int layoutId) {
        if (mItemLayouts == null){
            mItemLayouts = new SparseIntArray();
        }
        mItemLayouts.append(itemType, layoutId);
    }

    @Override
    protected BaseViewHolder getBaseViewHolderByType( ViewGroup parent, int viewType) {
        BaseViewHolder result = null;
        if (mItemLayouts != null){
            result = BaseViewHolder.getHolder(parent.getContext(), parent, mItemLayouts.get(viewType));
        }
        return result;

    }

    @Override
    protected int getDefItemViewType(int position) {
        return mDataList.get(position).getItemType();
    }
}
