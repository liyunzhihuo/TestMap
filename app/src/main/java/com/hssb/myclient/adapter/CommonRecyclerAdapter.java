package com.hssb.myclient.adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Darren on 2016/12/28.
 * Description: 通用的Adapter
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private static final int REFRESH_COUNT = 9;
    public static final int RESULT_TYPE_MORE = 1;
    public static final int RESULT_TYPE_END = 2;


    // 基本的头部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_HEADER = 10000000;
    // 基本的底部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_FOOTER = 20000000;

    // 所有头部存放的集合
    protected SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    // 所有底部存放的集合
    protected SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    private OnLoadMoreListener mOnLoadMoreListener;

    protected Context mContext;
    protected LayoutInflater mInflater;
    //数据怎么办？
    protected List<T> mData;
    // 布局怎么办？
    private int mLayoutId;

    // 多布局支持
    private MultiTypeSupport mMultiTypeSupport;
    //默认可以加载更多

    private boolean mIsLoadMore = true;

    public CommonRecyclerAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    /**
     * 多布局支持
     */
    public CommonRecyclerAdapter(Context context, List<T> data, MultiTypeSupport<T> multiTypeSupport) {
        this(context, data, -1);
        this.mMultiTypeSupport = multiTypeSupport;
    }

    /**
     * 根据当前位置获取不同的viewType
     */
    @Override
    public int getItemViewType(int position) {

        if (isHeaderPosition(position)) {
            // 是头部
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)) {
            // 是底部
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        // 返回数据部分的viewType
//        return mRealAdapter.getItemViewType(position - getHeadersCount());

        // 多布局支持
        if (mMultiTypeSupport != null) {
            return mMultiTypeSupport.getLayoutId(mData.get(position - getHeadersCount()), position - getHeadersCount());
        }

        return super.getItemViewType(position - getHeadersCount());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderType(viewType)) {
            // 根据key获取View
            int headerPosition = mHeaderViews.indexOfKey(viewType);
            View headerView = mHeaderViews.valueAt(headerPosition);
            return createHeaderAndFooterViewHolder(headerView);
        }
        //如果是尾部
        if (isFooterType(viewType)) {
            // 根据key获取View
            int footerPosition = mFooterViews.indexOfKey(viewType);
            View footerView = mFooterViews.valueAt(footerPosition);
            return createHeaderAndFooterViewHolder(footerView);
        }
        // 多布局支持
        if (mMultiTypeSupport != null) {
            mLayoutId = viewType;
        }
        // 先inflate数据
        View itemView = mInflater.inflate(mLayoutId, parent, false);
        // 返回ViewHolder
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定怎么办？回传出去
        if (isHeaderPosition(position) || isFooterPosition(position)) {

        } else {
            int realPosition = position - getHeadersCount();
            convert(holder, mData.get(realPosition), realPosition);
        }
    }

    /**
     * 利用抽象方法回传出去，每个不一样的Adapter去设置
     *
     * @param item 当前的数据
     */
    public abstract void convert(ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return getRealItemCount() + getHeadersCount() + getFootersCount();
    }


    // 代表是头部位置
    public boolean isHeaderPosition(int position) {
        return position < getHeadersCount();
    }

    // 代表是底部位置
    public boolean isFooterPosition(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    /**
     * 获取数据的条数
     */
    public int getRealItemCount() {
        return mData.size();
    }

    /**
     * 获取头部条数
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取底部条数
     */
    public int getFootersCount() {
        return mFooterViews.size();
    }


    /**
     * 判断是不是底部类型
     */
    private boolean isFooterType(int viewType) {
        return mFooterViews.indexOfKey(viewType) >= 0;
    }

    /**
     * 判断是不是头部类型
     */
    private boolean isHeaderType(int viewType) {
        return mHeaderViews.indexOfKey(viewType) >= 0;
    }


    /**
     * 直接返回 只有一个View的ViewHolder
     */
    private ViewHolder createHeaderAndFooterViewHolder(View view) {
        return new ViewHolder(view);
    }

    // 添加头部
    public void addHeaderView(View view) {
        mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
        notifyDataSetChanged();
    }



    // 移除头部
    public void removeHeaderView(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index < 0) return;
        mHeaderViews.removeAt(index);
        notifyDataSetChanged();
    }

    public void removeFooterView(View view) {
        int index = mFooterViews.indexOfValue(view);
        if (index < 0) return;
        mFooterViews.removeAt(index);
        notifyDataSetChanged();
    }


    /**
     * 加载更多
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener, final RecyclerView view) {
        mOnLoadMoreListener = listener;

        if (view != null) {
            view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    //是否是最后一个显示的item位置
                    if (mOnLoadMoreListener != null && isBottom(view) && mIsLoadMore) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            });
        }
    }

    /**
     * 判断是否滑动到底部
     */
    private boolean isBottom(View view) {
        return !ViewCompat.canScrollVertically(view, 1);
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isFooterPosition(position) || isHeaderPosition(position) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }


    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }
}
