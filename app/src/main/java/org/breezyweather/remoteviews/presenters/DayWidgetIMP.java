package org.breezyweather.remoteviews.presenters;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import java.util.Date;

import org.breezyweather.BreezyWeather;
import org.breezyweather.common.basic.models.Location;
import org.breezyweather.common.basic.models.options.unit.TemperatureUnit;
import org.breezyweather.common.basic.models.weather.Temperature;
import org.breezyweather.common.basic.models.weather.Weather;
import org.breezyweather.theme.resource.ResourceHelper;
import org.breezyweather.theme.resource.ResourcesProviderFactory;
import org.breezyweather.theme.resource.providers.ResourceProvider;
import org.breezyweather.R;
import org.breezyweather.background.receiver.widget.WidgetDayProvider;
import org.breezyweather.common.utils.DisplayUtils;
import org.breezyweather.common.utils.helpers.LunarHelper;
import org.breezyweather.remoteviews.WidgetHelper;
import org.breezyweather.settings.SettingsManager;

public class DayWidgetIMP extends AbstractRemoteViewsPresenter {

    public static void updateWidgetView(Context context, Location location) {
        WidgetConfig config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_day_setting)
        );

        RemoteViews views = getRemoteViews(
                context, location,
                config.viewStyle, config.cardStyle, config.cardAlpha,
                config.textColor, config.textSize, config.hideSubtitle, config.subtitleData
        );

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, WidgetDayProvider.class),
                views
        );
    }

    public static RemoteViews getRemoteViews(Context context,
                                             Location location,
                                             String viewStyle, String cardStyle, int cardAlpha,
                                             String textColor, int textSize,
                                             boolean hideSubtitle, String subtitleData) {

        boolean dayTime = location.isDaylight();

        SettingsManager settings = SettingsManager.getInstance(context);
        TemperatureUnit temperatureUnit = settings.getTemperatureUnit();
        boolean minimalIcon = settings.isWidgetMinimalIconEnabled();

        WidgetColor color;
        if (viewStyle.equals("pixel") || viewStyle.equals("nano")
                || viewStyle.equals("oreo") || viewStyle.equals("oreo_google_sans")
                || viewStyle.equals("temp")) {
            color = new WidgetColor(context, "none", textColor);
        } else {
            color = new WidgetColor(context, cardStyle, textColor);
        }

        RemoteViews views = buildWidgetView(
                context, location, temperatureUnit,
                color,
                dayTime, minimalIcon,
                viewStyle, textSize,
                hideSubtitle, subtitleData);
        Weather weather = location.getWeather();
        if (weather == null) {
            return views;
        }

        if (color.showCard) {
            views.setImageViewResource(
                    R.id.widget_day_card,
                    getCardBackgroundId(color.cardColor)
            );
            views.setInt(
                    R.id.widget_day_card,
                    "setImageAlpha",
                    (int) (cardAlpha / 100.0 * 255)
            );
        }

        setOnClickPendingIntent(context, views, location, viewStyle, subtitleData);

        return views;
    }

    private static RemoteViews buildWidgetView(Context context, Location location, 
                                               TemperatureUnit temperatureUnit,
                                               WidgetColor color,
                                               boolean dayTime, boolean minimalIcon,
                                               String viewStyle, int textSize,
                                               boolean hideSubtitle, String subtitleData) {
        RemoteViews views = new RemoteViews(
                context.getPackageName(),
                !color.showCard
                        ? R.layout.widget_day_symmetry
                        : R.layout.widget_day_symmetry_card
        );
        switch (viewStyle) {
            case "rectangle":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_rectangle
                                : R.layout.widget_day_rectangle_card
                );
                break;

            case "symmetry":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_symmetry
                                : R.layout.widget_day_symmetry_card
                );
                break;

            case "tile":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_tile
                                : R.layout.widget_day_tile_card
                );
                break;

            case "mini":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_mini
                                : R.layout.widget_day_mini_card
                );
                break;

            case "nano":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_nano
                                : R.layout.widget_day_nano_card
                );
                break;

            case "pixel":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_pixel
                                : R.layout.widget_day_pixel_card
                        );
                break;

            case "vertical":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_vertical
                                : R.layout.widget_day_vertical_card
                );
                break;

            case "oreo":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_oreo
                                : R.layout.widget_day_oreo_card
                );
                break;

            case "oreo_google_sans":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_oreo_google_sans
                                : R.layout.widget_day_oreo_google_sans_card
                );
                break;

            case "temp":
                views = new RemoteViews(
                        context.getPackageName(),
                        !color.showCard
                                ? R.layout.widget_day_temp
                                : R.layout.widget_day_temp_card
                );
                break;
        }
        Weather weather = location.getWeather();
        if (weather == null) {
            return views;
        }

        ResourceProvider provider = ResourcesProviderFactory.getNewInstance();

        if (weather.getCurrent() != null && weather.getCurrent().getWeatherCode() != null) {
            views.setImageViewUri(
                    R.id.widget_day_icon,
                    ResourceHelper.getWidgetNotificationIconUri(
                            provider,
                            weather.getCurrent().getWeatherCode(),
                            dayTime,
                            minimalIcon,
                            color.getMinimalIconColor()
                    )
            );
        }
        if (!viewStyle.equals("oreo") && !viewStyle.equals("oreo_google_sans")) {
            views.setTextViewText(
                    R.id.widget_day_title,
                    getTitleText(context, location, viewStyle, temperatureUnit)
            );
        }
        if (viewStyle.equals("vertical")) {
            if (weather.getCurrent() != null
                    && weather.getCurrent().getTemperature() != null
                    && weather.getCurrent().getTemperature().getTemperature() != null) {
                boolean negative = temperatureUnit.getValueWithoutUnit(
                        weather.getCurrent().getTemperature().getTemperature()
                ) < 0;
                views.setViewVisibility(
                        R.id.widget_day_sign,
                        negative ? View.VISIBLE : View.GONE
                );
            } else {
                views.setViewVisibility(
                        R.id.widget_day_symbol,
                        View.GONE
                );
                views.setViewVisibility(
                        R.id.widget_day_sign,
                        View.GONE
                );
            }
        }
        views.setTextViewText(
                R.id.widget_day_subtitle,
                getSubtitleText(context, weather, viewStyle, temperatureUnit)
        );
        if (!viewStyle.equals("pixel")) {
            views.setTextViewText(
                    R.id.widget_day_time,
                    getTimeText(context, location, weather, viewStyle, subtitleData, temperatureUnit)
            );
        }

        if (color.textColor != Color.TRANSPARENT) {
            views.setTextColor(R.id.widget_day_title, color.textColor);
            views.setTextColor(R.id.widget_day_sign, color.textColor);
            views.setTextColor(R.id.widget_day_symbol, color.textColor);
            views.setTextColor(R.id.widget_day_subtitle, color.textColor);
            views.setTextColor(R.id.widget_day_time, color.textColor);
        }

        if (textSize != 100) {
            float signSymbolSize = context.getResources().getDimensionPixelSize(R.dimen.widget_current_weather_icon_size)
                    * textSize / 100f;

            views.setTextViewTextSize(R.id.widget_day_title, TypedValue.COMPLEX_UNIT_PX,
                    getTitleSize(context, viewStyle) * textSize / 100f);

            views.setTextViewTextSize(R.id.widget_day_sign, TypedValue.COMPLEX_UNIT_PX, signSymbolSize);
            views.setTextViewTextSize(R.id.widget_day_symbol, TypedValue.COMPLEX_UNIT_PX, signSymbolSize);

            views.setTextViewTextSize(R.id.widget_day_subtitle, TypedValue.COMPLEX_UNIT_PX,
                    getSubtitleSize(context, viewStyle) * textSize / 100f);

            views.setTextViewTextSize(R.id.widget_day_time, TypedValue.COMPLEX_UNIT_PX,
                    getTimeSize(context, viewStyle) * textSize / 100f);
        }

        views.setViewVisibility(R.id.widget_day_time, hideSubtitle ? View.GONE : View.VISIBLE);

        return views;
    }

    public static boolean isEnable(Context context) {
        int[] widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WidgetDayProvider.class));
        return widgetIds != null && widgetIds.length > 0;
    }

    @Nullable
    private static String getTitleText(Context context, Location location, 
                                       String viewStyle, TemperatureUnit unit) {
        Weather weather = location.getWeather();
        if (weather == null) {
            return null;
        }
        switch (viewStyle) {
            case "rectangle":
                return WidgetHelper.buildWidgetDayStyleText(context, weather, unit)[0];

            case "symmetry":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return location.getCityName(context)
                            + "\n"
                            + weather.getCurrent().getTemperature().getTemperature(context, unit);
                } else {
                    return location.getCityName(context);
                }

            case "tile":
            case "mini":
                if (weather.getCurrent() != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (!TextUtils.isEmpty(weather.getCurrent().getWeatherText())) {
                        stringBuilder.append(weather.getCurrent().getWeatherText());
                    }
                    if (weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                        if (!TextUtils.isEmpty(weather.getCurrent().getWeatherText())) {
                            stringBuilder.append(" ");
                        }
                        stringBuilder.append(weather.getCurrent().getTemperature().getTemperature(context, unit));
                    }
                    return stringBuilder.toString();
                } else {
                    return null;
                }

            case "nano":
            case "pixel":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return weather.getCurrent().getTemperature().getTemperature(context, unit);
                } else {
                    return null;
                }

            case "temp":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return weather.getCurrent().getTemperature().getShortTemperature(context, unit);
                } else {
                    return null;
                }

            case "vertical":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return String.valueOf(
                            Math.abs(
                                    unit.getValueWithoutUnit(
                                            weather.getCurrent().getTemperature().getTemperature()
                                    )
                            )
                    );
                } else {
                    return null;
                }
        }
        return null;
    }

    private static String getSubtitleText(Context context, Weather weather, String viewStyle, TemperatureUnit unit) {
        switch (viewStyle) {
            case "rectangle":
                return WidgetHelper.buildWidgetDayStyleText(context, weather, unit)[1];

            case "tile":
                if (weather.getDailyForecast().size() > 0
                        && weather.getDailyForecast().get(0).getDay() != null
                        && weather.getDailyForecast().get(0).getDay().getTemperature() != null
                        && weather.getDailyForecast().get(0).getDay().getTemperature().getTemperature() != null
                        && weather.getDailyForecast().get(0).getNight() != null
                        && weather.getDailyForecast().get(0).getNight().getTemperature() != null
                        && weather.getDailyForecast().get(0).getNight().getTemperature().getTemperature() != null
                ) {
                    return Temperature.getTrendTemperature(
                            context,
                            weather.getDailyForecast().get(0).getNight().getTemperature().getTemperature(),
                            weather.getDailyForecast().get(0).getDay().getTemperature().getTemperature(),
                            unit
                    );
                }

            case "symmetry":
            case "vertical":
                if (weather.getCurrent() != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (!TextUtils.isEmpty(weather.getCurrent().getWeatherText())) {
                        stringBuilder.append(weather.getCurrent().getWeatherText());
                    }
                    if (weather.getDailyForecast().size() > 0
                            && weather.getDailyForecast().get(0).getDay() != null
                            && weather.getDailyForecast().get(0).getDay().getTemperature() != null
                            && weather.getDailyForecast().get(0).getDay().getTemperature().getTemperature() != null
                            && weather.getDailyForecast().get(0).getNight() != null
                            && weather.getDailyForecast().get(0).getNight().getTemperature() != null
                            && weather.getDailyForecast().get(0).getNight().getTemperature().getTemperature() != null
                    ) {
                        if (!TextUtils.isEmpty(weather.getCurrent().getWeatherText())) {
                            stringBuilder.append(" ");
                        }
                        stringBuilder.append(Temperature.getTrendTemperature(
                                        context,
                                        weather.getDailyForecast().get(0).getNight().getTemperature().getTemperature(),
                                        weather.getDailyForecast().get(0).getDay().getTemperature().getTemperature(),
                                        unit
                                )
                        );
                    }
                    return stringBuilder.toString();
                }

            case "oreo":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return weather.getCurrent().getTemperature().getTemperature(context, unit);
                }

            case "oreo_google_sans":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getTemperature() != null) {
                    return unit.getLongValueText(context, weather.getCurrent().getTemperature().getTemperature());
                }
        }
        return "";
    }

    @Nullable
    private static String getTimeText(Context context, Location location, Weather weather,
                                      String viewStyle, String subtitleData, TemperatureUnit unit) {
        switch (subtitleData) {
            case "time":
                switch (viewStyle) {
                    case "rectangle":
                        return location.getCityName(context) 
                                + " " 
                                + DisplayUtils.getTime(context, weather.getBase().getUpdateDate(), location.getTimeZone());

                    case "symmetry":
                        return WidgetHelper.getWeek(context, location.getTimeZone())
                                + " " 
                                + DisplayUtils.getTime(context, weather.getBase().getUpdateDate(), location.getTimeZone());

                    case "tile":
                    case "mini":
                    case "vertical":
                        return location.getCityName(context)
                                + " " + WidgetHelper.getWeek(context, location.getTimeZone())
                                + " " + DisplayUtils.getTime(context, weather.getBase().getUpdateDate(), location.getTimeZone());
                }
                return null;

            case "aqi":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getAirQuality() != null
                        && weather.getCurrent().getAirQuality().getIndex(null) != null
                        && weather.getCurrent().getAirQuality().getName(context, null) != null) {
                    return weather.getCurrent().getAirQuality().getName(context, null)
                            + " (" 
                            + weather.getCurrent().getAirQuality().getIndex(null)
                            + ")";
                }
                return null;

            case "wind":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getWind() != null
                        && weather.getCurrent().getWind().getDirection() != null
                        && weather.getCurrent().getWind().getLevel() != null) {
                    return weather.getCurrent().getWind().getDirection()
                            + " "
                            + weather.getCurrent().getWind().getLevel();
                }
                return null;

            case "lunar":
                switch (viewStyle) {
                    case "rectangle":
                        return location.getCityName(context)
                                + " "
                                + LunarHelper.getLunarDate(new Date());

                    case "symmetry":
                        return WidgetHelper.getWeek(context, location.getTimeZone())
                                + " "
                                + LunarHelper.getLunarDate(new Date());

                    case "tile":
                    case "mini":
                    case "vertical":
                        return location.getCityName(context)
                                + " " + WidgetHelper.getWeek(context, location.getTimeZone())
                                + " " + LunarHelper.getLunarDate(new Date());
                }
                return null;

            case "feels_like":
                if (weather.getCurrent() != null
                        && weather.getCurrent().getTemperature() != null
                        && weather.getCurrent().getTemperature().getFeelsLikeTemperature() != null) {
                    return context.getString(R.string.temperature_feels_like)
                            + " "
                            + weather.getCurrent().getTemperature().getFeelsLikeTemperature(context, unit);
                }
                return null;
        }
        return getCustomSubtitle(context, subtitleData, location, weather);
    }

    private static float getTitleSize(Context context, String viewStyle) {
        switch (viewStyle) {
            case "rectangle":
            case "symmetry":
            case "tile":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_content_text_size);

            case "mini":
            case "nano":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_subtitle_text_size);

            case "pixel":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_design_title_text_size);

            case "vertical":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_current_weather_icon_size);

            case "oreo":
            case "oreo_google_sans":
            case "temp":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_large_title_text_size);
        }
        return 0;
    }

    private static float getSubtitleSize(Context context, String viewStyle) {
        switch (viewStyle) {
            case "rectangle":
            case "symmetry":
            case "tile":
            case "vertical":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_content_text_size);

            case "oreo":
            case "oreo_google_sans":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_large_title_text_size);
        }
        return 0;
    }

    private static float getTimeSize(Context context, String viewStyle) {
        switch (viewStyle) {
            case "rectangle":
            case "symmetry":
            case "tile":
            case "vertical":
            case "mini":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_time_text_size);

            case "pixel":
                return context.getResources().getDimensionPixelSize(R.dimen.widget_subtitle_text_size);
        }
        return 0;
    }

    private static void setOnClickPendingIntent(Context context, RemoteViews views, Location location,
                                                String viewStyle, String subtitleData) {
        // weather.
        views.setOnClickPendingIntent(
                R.id.widget_day_weather,
                getWeatherPendingIntent(
                        context,
                        location,
                        BreezyWeather.WIDGET_DAY_PENDING_INTENT_CODE_WEATHER
                )
        );

        // title.
        if (viewStyle.equals("oreo") || viewStyle.equals("oreo_google_sans")) {
            views.setOnClickPendingIntent(
                    R.id.widget_day_title,
                    getCalendarPendingIntent(
                            context,
                            BreezyWeather.WIDGET_DAY_PENDING_INTENT_CODE_CALENDAR
                    )
            );
        }

        // time.
        if (viewStyle.equals("pixel") || subtitleData.equals("lunar")) {
            views.setOnClickPendingIntent(
                    R.id.widget_day_time,
                    getCalendarPendingIntent(
                            context,
                            BreezyWeather.WIDGET_DAY_PENDING_INTENT_CODE_CALENDAR
                    )
            );
        }
    }
}
