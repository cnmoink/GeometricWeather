package org.breezyweather.background.polling.work.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import org.breezyweather.common.basic.models.Location;
import org.breezyweather.remoteviews.presenters.notification.ForecastNotificationIMP;
import org.breezyweather.background.polling.PollingManager;
import org.breezyweather.location.LocationHelper;
import org.breezyweather.weather.WeatherHelper;

@HiltWorker
public class TodayForecastUpdateWorker extends AsyncUpdateWorker {

    @AssistedInject
    public TodayForecastUpdateWorker(@Assisted @NonNull Context context,
                                     @Assisted @NonNull WorkerParameters workerParams,
                                     LocationHelper locationHelper,
                                     WeatherHelper weatherHelper) {
        super(context, workerParams, locationHelper, weatherHelper);
    }

    @Override
    public void updateView(Context context, Location location) {
        if (ForecastNotificationIMP.isEnable(context, true)) {
            ForecastNotificationIMP.buildForecastAndSendIt(context, location, true);
        }
    }

    @Override
    public void updateView(Context context, List<Location> locationList) {
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void handleUpdateResult(SettableFuture<Result> future, boolean failed) {
        future.set(failed ? Result.failure() : Result.success());
        PollingManager.resetTodayForecastBackgroundTask(
                getApplicationContext(), false, true);
    }
}
