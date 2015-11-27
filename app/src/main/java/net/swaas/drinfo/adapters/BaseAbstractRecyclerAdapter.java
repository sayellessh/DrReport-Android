package net.swaas.drinfo.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.swaas.drinfo.views.DefaultRecyclerView;

import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public abstract class BaseAbstractRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private View mEmptyView;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private DefaultRecyclerView mRecyclerView;

    public void setEmptyView(View view) {
        mEmptyView = view;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setOnLoadMoreListener(DefaultRecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.mRecyclerView = recyclerView;
        if (this.mRecyclerView != null && this.mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading
                            && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (BaseAbstractRecyclerAdapter.this.onLoadMoreListener != null) {
                            BaseAbstractRecyclerAdapter.this.onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
