package org.breezyweather.main.adapters.main;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;

import java.util.List;
import java.util.TimeZone;

import org.breezyweather.common.basic.GeoActivity;
import org.breezyweather.common.basic.models.Location;
import org.breezyweather.common.basic.models.weather.Alert;
import org.breezyweather.common.basic.models.weather.Weather;
import org.breezyweather.common.utils.helpers.IntentHelper;
import org.breezyweather.main.MainActivity;
import org.breezyweather.R;
import org.breezyweather.common.utils.DisplayUtils;
import org.breezyweather.main.utils.MainThemeColorProvider;

public class FirstCardHeaderController
        implements View.OnClickListener {

    private final GeoActivity mActivity;
    private final View mView;
    private final String mFormattedId;

    private @Nullable LinearLayout mContainer;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public FirstCardHeaderController(@NonNull GeoActivity activity, @NonNull Location location) {
        mActivity = activity;
        mView = LayoutInflater.from(activity).inflate(R.layout.container_main_first_card_header, null);
        mFormattedId = location.getFormattedId();

        AppCompatImageView timeIcon = mView.findViewById(R.id.container_main_first_card_header_timeIcon);
        TextView refreshTime = mView.findViewById(R.id.container_main_first_card_header_timeText);
        TextView localTime = mView.findViewById(R.id.container_main_first_card_header_localTimeText);
        TextView alert = mView.findViewById(R.id.container_main_first_card_header_alert);
        View line = mView.findViewById(R.id.container_main_first_card_header_line);

        if (location.getWeather() != null) {
            Weather weather = location.getWeather();
            List<Alert> currentAlertList = weather.getCurrentAlertList();

            mView.setOnClickListener(v -> ((MainActivity) activity).setManagementFragmentVisibility(true));

            if (currentAlertList.size() == 0) {
                timeIcon.setEnabled(false);
                timeIcon.setImageResource(R.drawable.ic_time);
            } else {
                timeIcon.setEnabled(true);
                timeIcon.setImageResource(R.drawable.ic_alert);
            }
            timeIcon.setContentDescription(
                    activity.getString(R.string.content_desc_weather_alert_button)
                            .replace("$", "" + currentAlertList.size())
            );
            ImageViewCompat.setImageTintList(
                    timeIcon,
                    ColorStateList.valueOf(
                            MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
                    )
            );
            timeIcon.setOnClickListener(this);

            refreshTime.setText(
                    activity.getString(R.string.refresh_at)
                            + " "
                            + DisplayUtils.getTime(activity, weather.getBase().getUpdateDate(), location.getTimeZone())
            );
            refreshTime.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText));

            if (TimeZone.getDefault().getRawOffset() == location.getTimeZone().getRawOffset()) {
                // same time zone.
                localTime.setVisibility(View.GONE);
            } else {
                localTime.setVisibility(View.VISIBLE);
                localTime.setText(
                        DisplayUtils.getFormattedDate(
                                weather.getBase().getUpdateDate(),
                                TimeZone.getDefault(),
                                activity.getString(R.string.date_format_widget_long) + (DisplayUtils.is12Hour(activity) ? ", h:mm aa" : ", HH:mm")
                        )
                );
                localTime.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorCaptionText));
            }

            if (currentAlertList.size() == 0) {
                alert.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
            } else {
                alert.setVisibility(View.VISIBLE);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < currentAlertList.size(); i++) {
                    builder.append(currentAlertList.get(i).getDescription());
                    if (currentAlertList.get(i).getStartDate() != null) {
                        String startDateDay = DisplayUtils.getFormattedDate(currentAlertList.get(i).getStartDate(),
                                location.getTimeZone(),
                                activity.getString(R.string.date_format_long));
                        builder.append(", ")
                                .append(startDateDay)
                                .append(", ")
                                .append(DisplayUtils.getFormattedDate(currentAlertList.get(i).getStartDate(),
                                        location.getTimeZone(),
                                        (DisplayUtils.is12Hour(activity)) ? "h:mm aa" : "HH:mm"));
                        if (currentAlertList.get(i).getEndDate() != null) {
                            builder.append("-");
                            String endDateDay = DisplayUtils.getFormattedDate(currentAlertList.get(i).getEndDate(),
                                    location.getTimeZone(),
                                    activity.getString(R.string.date_format_long));
                            if (!startDateDay.equals(endDateDay)) {
                                builder.append(endDateDay)
                                        .append(", ");
                            }
                            builder.append(DisplayUtils.getFormattedDate(currentAlertList.get(i).getEndDate(),
                                    location.getTimeZone(),
                                    (DisplayUtils.is12Hour(activity)) ? "h:mm aa" : "HH:mm"));
                        }
                    }
                    if (i != currentAlertList.size() - 1) {
                        builder.append("\n");
                    }
                }
                alert.setText(builder.toString());
                alert.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText));

                line.setVisibility(View.VISIBLE);
                line.setBackgroundColor(MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorSurface));
            }
            alert.setOnClickListener(this);
        }
    }

    public void bind(LinearLayout firstCardContainer) {
        mContainer = firstCardContainer;
        mContainer.addView(mView, 0);
    }

    public void unbind() {
        if (mContainer != null) {
            mContainer.removeViewAt(0);
            mContainer = null;
        }
    }

    // interface.

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_main_first_card_header_timeIcon:
            case R.id.container_main_first_card_header_alert:
                IntentHelper.startAlertActivity(mActivity, mFormattedId);
                break;
        }
    }
}
