package org.breezyweather.common.utils.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.URISyntaxException;

import org.breezyweather.common.basic.models.Location;
import org.breezyweather.common.ui.activities.AlertActivity;
import org.breezyweather.main.MainActivity;
import org.breezyweather.search.SearchActivity;
import org.breezyweather.BuildConfig;
import org.breezyweather.R;
import org.breezyweather.background.polling.services.basic.AwakeForegroundUpdateService;
import org.breezyweather.common.ui.activities.AllergenActivity;
import org.breezyweather.daily.DailyWeatherActivity;
import org.breezyweather.settings.activities.AboutActivity;
import org.breezyweather.settings.activities.CardDisplayManageActivity;
import org.breezyweather.settings.activities.DailyTrendDisplayManageActivity;
import org.breezyweather.settings.activities.HourlyTrendDisplayManageActivity;
import org.breezyweather.settings.activities.PreviewIconActivity;
import org.breezyweather.settings.activities.SelectProviderActivity;
import org.breezyweather.settings.activities.SettingsActivity;
import org.breezyweather.wallpaper.MaterialLiveWallpaperService;

/**
 * Intent helper.
 * */

public class IntentHelper {

    public static void startMainActivity(Context context) {
        context.startActivity(
                new Intent(MainActivity.ACTION_MAIN)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }

    public static void startMainActivityForManagement(Activity activity) {
        activity.startActivity(
                new Intent(MainActivity.ACTION_MANAGEMENT)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }

    public static Intent buildMainActivityIntent(@Nullable Location location) {
        String formattedId = null;
        if (location != null) {
            formattedId = location.getFormattedId();
        }

        return new Intent(MainActivity.ACTION_MAIN)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId);
    }

    public static Intent buildMainActivityShowAlertsIntent(@Nullable Location location) {
        String formattedId = null;
        if (location != null) {
            formattedId = location.getFormattedId();
        }

        return new Intent(MainActivity.ACTION_SHOW_ALERTS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId);
    }

    public static Intent buildMainActivityShowDailyForecastIntent(@Nullable Location location,
                                                                  int index) {
        String formattedId = null;
        if (location != null) {
            formattedId = location.getFormattedId();
        }

        return new Intent(MainActivity.ACTION_SHOW_DAILY_FORECAST)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID, formattedId)
                .putExtra(MainActivity.KEY_DAILY_INDEX, index);
    }

    public static Intent buildAwakeUpdateActivityIntent() {
        return new Intent("org.breezyweather.UPDATE")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void startDailyWeatherActivity(Activity activity,
                                                 @Nullable String formattedId, int index) {
        Intent intent = new Intent(activity, DailyWeatherActivity.class);
        intent.putExtra(DailyWeatherActivity.KEY_FORMATTED_LOCATION_ID, formattedId);
        intent.putExtra(DailyWeatherActivity.KEY_CURRENT_DAILY_INDEX, index);
        activity.startActivity(intent);
    }

    public static void startAlertActivity(Activity activity, @Nullable String formattedId) {
        Intent intent = new Intent(activity, AlertActivity.class);
        intent.putExtra(AlertActivity.KEY_FORMATTED_ID, formattedId);
        activity.startActivity(intent);
    }

    public static void startAllergenActivity(Activity activity, Location location) {
        Intent intent = new Intent(activity, AllergenActivity.class);
        intent.putExtra(
                AllergenActivity.KEY_ALLERGEN_ACTIVITY_LOCATION_FORMATTED_ID,
                location.getFormattedId()
        );
        activity.startActivity(intent);
    }

    public static void startSearchActivityForResult(Activity activity, int requestCode) {
        ActivityCompat.startActivityForResult(
                activity,
                new Intent(activity, SearchActivity.class),
                requestCode,
                null
        );
    }

    public static void startSettingsActivity(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    public static void startCardDisplayManageActivity(Activity activity) {
        activity.startActivity(new Intent(activity, CardDisplayManageActivity.class));
    }

    public static void startDailyTrendDisplayManageActivity(Activity activity) {
        activity.startActivity(new Intent(activity, DailyTrendDisplayManageActivity.class));
    }

    public static void startHourlyTrendDisplayManageActivityForResult(Activity activity) {
        activity.startActivity(new Intent(activity, HourlyTrendDisplayManageActivity.class));
    }

    public static void startSelectProviderActivity(Activity activity) {
        activity.startActivity(new Intent(activity, SelectProviderActivity.class));
    }

    public static void startPreviewIconActivity(Activity activity, String packageName) {
        activity.startActivity(
                new Intent(activity, PreviewIconActivity.class).putExtra(
                        PreviewIconActivity.KEY_ICON_PREVIEW_ACTIVITY_PACKAGE_NAME,
                        packageName
                )
        );
    }

    public static void startAboutActivity(Activity activity) {
        activity.startActivity(new Intent(activity, AboutActivity.class));
    }

    public static void startApplicationDetailsActivity(Context context) {
        startApplicationDetailsActivity(context, context.getPackageName());
    }

    public static void startApplicationDetailsActivity(Context context, String pkgName) {
        context.startActivity(
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.fromParts("package", pkgName, null))
        );
    }

    public static void startLocationSettingsActivity(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static void startLiveWallpaperActivity(Context context) {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, MaterialLiveWallpaperService.class)
        );
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar(context.getString(R.string.feedback_cannot_start_live_wallpaper_activity));
        }
    }

    public static void startAppStoreDetailsActivity(Context context) {
        startAppStoreDetailsActivity(context, context.getPackageName());
    }

    public static void startAppStoreDetailsActivity(Context context, String packageName) {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + packageName)
        );
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar("Unavailable AppStore.");
        }
    }

    public static void startAppStoreSearchActivity(Context context, String query) {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://search?q=" + query)
        );
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar("Unavailable AppStore.");
        }
    }

    public static void startWebViewActivity(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar("Unavailable internet browser.");
        }
    }

    public static void startEmailActivity(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar("Unavailable e-mail.");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("BatteryLife")
    public static void startBatteryOptimizationActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            SnackbarHelper.showSnackbar("Unavailable battery optimization activity.");
        }
    }

    public static void startAwakeForegroundUpdateService(Context context) {
        ContextCompat.startForegroundService(context, getAwakeForegroundUpdateServiceIntent(context));
    }

    public static Intent getAwakeForegroundUpdateServiceIntent(Context context) {
        return new Intent(context, AwakeForegroundUpdateService.class);
    }

    @SuppressLint("WrongConstant")
    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
                .size() > 0;
    }
}
