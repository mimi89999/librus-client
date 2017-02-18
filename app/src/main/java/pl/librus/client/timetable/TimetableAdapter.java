package pl.librus.client.timetable;

import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by szyme on 21.01.2017.
 * Child of FlexibleAdapter, used to properly achieve endless scrolling
 */

class TimetableAdapter extends FlexibleAdapter<IFlexible> {
    OnLoadMoreListener onLoadMoreListener = () -> {
    };

    TimetableAdapter(@Nullable List<IFlexible> items) {
        super(items);
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }
}
