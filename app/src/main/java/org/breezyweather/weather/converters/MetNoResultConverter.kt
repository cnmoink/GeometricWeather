package org.breezyweather.weather.converters

import android.content.Context
import org.breezyweather.BreezyWeather.Companion.instance
import org.breezyweather.common.basic.models.Location
import org.breezyweather.common.basic.models.weather.*
import org.breezyweather.common.utils.DisplayUtils
import org.breezyweather.weather.json.metno.MetNoForecastResult
import org.breezyweather.weather.json.metno.MetNoEphemerisTime
import org.breezyweather.weather.json.metno.MetNoEphemerisResult
import org.breezyweather.weather.services.WeatherService.WeatherResultWrapper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

fun convert(
    context: Context,
    location: Location,
    forecastResult: MetNoForecastResult,
    ephemerisResult: MetNoEphemerisResult
): WeatherResultWrapper {
    // If the API doesn’t return hourly, consider data as garbage and keep cached data
    if (forecastResult.properties == null
        || forecastResult.properties.timeseries.isNullOrEmpty()) {
        return WeatherResultWrapper(null);
    }

    return try {
        val hourlyByHalfDay: MutableMap<String, Map<String, MutableList<Hourly>>> = HashMap()
        val hourlyList: MutableList<Hourly> = mutableListOf()
        var currentI: Int? = null;

        for (i in forecastResult.properties.timeseries.indices) {
            val hourlyForecast = forecastResult.properties.timeseries[i]
            val symbolCode = hourlyForecast.data?.next1Hours?.summary?.symbolCode
                ?: hourlyForecast.data?.next6Hours?.summary?.symbolCode
                ?: hourlyForecast.data?.next12Hours?.summary?.symbolCode
            val hourly = Hourly(
                date = hourlyForecast.time,
                isDaylight = true, // Will be completed later with daily sunrise/set
                weatherText = null, // TODO
                weatherCode = getWeatherCode(symbolCode),
                temperature = Temperature(
                    temperature = hourlyForecast.data?.instant?.details?.airTemperature?.roundToInt(),
                ),
                precipitation = Precipitation(
                    total = hourlyForecast.data?.next1Hours?.details?.precipitationAmount
                        ?: hourlyForecast.data?.next6Hours?.details?.precipitationAmount
                        ?: hourlyForecast.data?.next12Hours?.details?.precipitationAmount
                ),
                precipitationProbability = PrecipitationProbability(
                    total = hourlyForecast.data?.next1Hours?.details?.probabilityOfPrecipitation
                        ?: hourlyForecast.data?.next6Hours?.details?.probabilityOfPrecipitation
                        ?: hourlyForecast.data?.next12Hours?.details?.probabilityOfPrecipitation,
                    thunderstorm = hourlyForecast.data?.next1Hours?.details?.probabilityOfThunder
                        ?: hourlyForecast.data?.next6Hours?.details?.probabilityOfThunder
                        ?: hourlyForecast.data?.next12Hours?.details?.probabilityOfThunder
                ),
                wind = if (hourlyForecast.data?.instant?.details == null) null else Wind(
                    direction = getWindDirection(context, hourlyForecast.data.instant.details.windFromDirection),
                    degree = WindDegree(hourlyForecast.data.instant.details.windFromDirection, false),
                    speed = hourlyForecast.data.instant.details.windSpeed?.times(3.6f),
                    level = getWindLevel(context, hourlyForecast.data.instant.details.windSpeed?.times(3.6f))
                ),
                // airQuality = TODO
                uV = UV(
                    index = hourlyForecast.data?.instant?.details?.ultravioletIndexClearSky?.roundToInt(),
                    level = getUVLevel(context, hourlyForecast.data?.instant?.details?.ultravioletIndexClearSky?.roundToInt())
                )
            )

            // We shift by 6 hours the hourly date, otherwise nighttime (00:00 to 05:59) would be on the wrong day
            val theDayAtMidnight = DisplayUtils.toTimezoneNoHour(
                Date(hourlyForecast.time.time - (6 * 3600 * 1000)),
                location.timeZone
            )
            val theDayFormatted =
                DisplayUtils.getFormattedDate(theDayAtMidnight, location.timeZone, "yyyy-MM-dd")
            if (!hourlyByHalfDay.containsKey(theDayFormatted)) {
                hourlyByHalfDay[theDayFormatted] = hashMapOf(
                    "day" to ArrayList(),
                    "night" to ArrayList()
                )
            }
            if (hourlyForecast.time.time < theDayAtMidnight.time + 18 * 3600 * 1000) {
                // 06:00 to 17:59 is the day
                hourlyByHalfDay[theDayFormatted]!!["day"]!!.add(hourly)
            } else {
                // 18:00 to 05:59 is the night
                hourlyByHalfDay[theDayFormatted]!!["night"]!!.add(hourly)
            }

            // Add to the app only if starts in the current hour
            if (hourlyForecast.time.time >= System.currentTimeMillis() - 3600 * 1000) {
                if (currentI == null) {
                    currentI = i + 1
                }
                hourlyList.add(hourly)
            }
        }

        val dailyList = getDailyList(context, location.timeZone, ephemerisResult.location?.time, hourlyList, hourlyByHalfDay)
        val weather = Weather(
            base = Base(
                cityId = location.cityId,
                publishDate = forecastResult.properties.meta?.updatedAt ?: Date()
            ),
            current = Current(
                weatherText = hourlyList.getOrNull(1)?.weatherText,
                weatherCode = hourlyList.getOrNull(1)?.weatherCode,
                temperature = hourlyList.getOrNull(1)?.temperature,
                wind = hourlyList.getOrNull(1)?.wind,
                uV = getCurrentUV(
                    context,
                    dailyList.getOrNull(0)?.uV?.index,
                    Date(),
                    dailyList.getOrNull(0)?.sun?.riseDate,
                    dailyList.getOrNull(0)?.sun?.setDate,
                    location.timeZone
                ),
                // airQuality = TODO,
                relativeHumidity = if (currentI != null)
                    forecastResult.properties.timeseries.getOrNull(currentI)?.data?.instant?.details?.relativeHumidity
                else null,
                pressure = if (currentI != null)
                    forecastResult.properties.timeseries.getOrNull(currentI)?.data?.instant?.details?.airPressureAtSeaLevel
                else null,
                dewPoint = if (currentI != null)
                    forecastResult.properties.timeseries.getOrNull(currentI)?.data?.instant?.details?.dewPointTemperature?.roundToInt()
                else null
            ),
            dailyForecast = dailyList,
            hourlyForecast = completeHourlyListFromDailyList(context, hourlyList, dailyList, location.timeZone, completeDaylight = true)
        )
        WeatherResultWrapper(weather)
    } catch (e: Exception) {
        if (instance.debugMode) {
            e.printStackTrace()
        }
        WeatherResultWrapper(null)
    }
}

private fun getDailyList(
    context: Context,
    timeZone: TimeZone,
    ephemerisTimeResults: List<MetNoEphemerisTime>?,
    hourlyList: List<Hourly>,
    hourlyListByHalfDay: Map<String, Map<String, MutableList<Hourly>>>
): List<Daily> {
    val dailyList: MutableList<Daily> = ArrayList()
    val hourlyListByDay = hourlyList.groupBy { DisplayUtils.getFormattedDate(it.date, timeZone, "yyyy-MM-dd") }
    for (day in hourlyListByDay.entries) {
        val dayDate = DisplayUtils.toDateNoHour(timeZone, day.key)
        val ephemerisInfo = ephemerisTimeResults?.firstOrNull { it.date == day.key }

        dailyList.add(
            Daily(
                date = dayDate,
                day = completeHalfDayFromHourlyList(
                    dailyDate = dayDate,
                    initialHalfDay = null,
                    halfDayHourlyList = hourlyListByHalfDay.getOrDefault(day.key, null)?.get("day"),
                    isDay = true
                ),
                night = completeHalfDayFromHourlyList(
                    dailyDate = dayDate,
                    initialHalfDay = null,
                    halfDayHourlyList = hourlyListByHalfDay.getOrDefault(day.key, null)?.get("night"),
                    isDay = false
                ),
                sun = Astro(
                    riseDate = ephemerisInfo?.sunrise?.time,
                    setDate = ephemerisInfo?.sunset?.time,
                ),
                moon = Astro(
                    riseDate = ephemerisInfo?.moonrise?.time,
                    setDate = ephemerisInfo?.moonset?.time,
                ),
                moonPhase = MoonPhase(
                    angle = ephemerisInfo?.moonposition?.phase?.roundToInt(),
                    description = ephemerisInfo?.moonposition?.desc
                ),
                //airQuality = TODO,
                uV = getDailyUVFromHourlyList(context, day.value),
                hoursOfSun = getHoursOfDay(ephemerisInfo?.sunrise?.time, ephemerisInfo?.sunset?.time)
            )
        )
    }
    return dailyList
}

private fun getWeatherCode(icon: String?): WeatherCode? {
    return if (icon == null) {
        null
    } else when(icon.replace("_night", "").replace("_day", "")) {
        "clearsky", "fair" -> WeatherCode.CLEAR
        "partlycloudy" -> WeatherCode.PARTLY_CLOUDY
        "cloudy" -> WeatherCode.CLOUDY
        "fog" -> WeatherCode.FOG
        "heavyrain", "heavyrainshowers", "lightrain", "lightrainshowers", "rain", "rainshowers" -> WeatherCode.RAIN
        "heavyrainandthunder", "heavyrainshowersandthunder", "heavysleetandthunder", "heavysleetshowersandthunder",
        "heavysnowandthunder", "heavysnowshowersandthunder", "lightrainandthunder", "lightrainshowersandthunder",
        "lightsleetandthunder", "lightsleetshowersandthunder", "lightsnowandthunder", "lightsnowshowersandthunder",
        "rainandthunder", "rainshowersandthunder", "sleetandthunder", "sleetshowersandthunder", "snowandthunder",
        "snowshowersandthunder" -> WeatherCode.THUNDERSTORM
        "heavysnow", "heavysnowshowers", "lightsnow", "lightsnowshowers", "snow", "snowshowers" -> WeatherCode.SNOW
        "heavysleet", "heavysleetshowers", "lightsleet", "lightsleetshowers", "sleet",
        "sleetshowers" -> WeatherCode.SLEET
        else -> null
    }
}
