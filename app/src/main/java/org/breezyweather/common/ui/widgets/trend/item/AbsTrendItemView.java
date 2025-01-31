package org.breezyweather.common.ui.widgets.trend.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.breezyweather.common.ui.widgets.trend.chart.AbsChartItemView;

public abstract class AbsTrendItemView extends ViewGroup {

    public AbsTrendItemView(Context context) {
        super(context);
    }

    public AbsTrendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsTrendItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbsTrendItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setChartItemView(AbsChartItemView view);
    public abstract AbsChartItemView getChartItemView();

    public abstract int getChartTop();
    public abstract int getChartBottom();
}
