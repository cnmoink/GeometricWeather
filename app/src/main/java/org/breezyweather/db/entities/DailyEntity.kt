package org.breezyweather.db.entities

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import org.breezyweather.common.basic.models.weather.WeatherCode
import org.breezyweather.common.basic.models.weather.WindDegree
import org.breezyweather.db.converters.WeatherCodeConverter
import org.breezyweather.db.converters.WindDegreeConverter
import java.util.Date

/**
 * Daily entity.
 *
 * [Daily].
 */
@Entity
data class DailyEntity(
    @field:Id var id: Long = 0,

    var cityId: String,
    var weatherSource: String,
    var date: Date,

    // daytime.
    var daytimeWeatherText: String? = null,
    var daytimeWeatherPhase: String? = null,
    @field:Convert(
        converter = WeatherCodeConverter::class,
        dbType = String::class
    ) var daytimeWeatherCode: WeatherCode? = null,

    var daytimeTemperature: Int? = null,
    var daytimeRealFeelTemperature: Int? = null,
    var daytimeRealFeelShaderTemperature: Int? = null,
    var daytimeApparentTemperature: Int? = null,
    var daytimeWindChillTemperature: Int? = null,
    var daytimeWetBulbTemperature: Int? = null,
    var daytimeDegreeDayTemperature: Int? = null,

    var daytimeTotalPrecipitation: Float? = null,
    var daytimeThunderstormPrecipitation: Float? = null,
    var daytimeRainPrecipitation: Float? = null,
    var daytimeSnowPrecipitation: Float? = null,
    var daytimeIcePrecipitation: Float? = null,

    var daytimeTotalPrecipitationProbability: Float? = null,
    var daytimeThunderstormPrecipitationProbability: Float? = null,
    var daytimeRainPrecipitationProbability: Float? = null,
    var daytimeSnowPrecipitationProbability: Float? = null,
    var daytimeIcePrecipitationProbability: Float? = null,

    var daytimeTotalPrecipitationDuration: Float? = null,
    var daytimeThunderstormPrecipitationDuration: Float? = null,
    var daytimeRainPrecipitationDuration: Float? = null,
    var daytimeSnowPrecipitationDuration: Float? = null,
    var daytimeIcePrecipitationDuration: Float? = null,

    var daytimeWindDirection: String? = null,
    @field:Convert(
        converter = WindDegreeConverter::class,
        dbType = Float::class
    ) var daytimeWindDegree: WindDegree? = null,
    var daytimeWindSpeed: Float? = null,
    var daytimeWindLevel: String? = null,

    var daytimeCloudCover: Int? = null,

    // nighttime.
    var nighttimeWeatherText: String? = null,
    var nighttimeWeatherPhase: String? = null,
    @field:Convert(
        converter = WeatherCodeConverter::class,
        dbType = String::class
    ) var nighttimeWeatherCode: WeatherCode? = null,

    var nighttimeTemperature: Int? = null,
    var nighttimeRealFeelTemperature: Int? = null,
    var nighttimeRealFeelShaderTemperature: Int? = null,
    var nighttimeApparentTemperature: Int? = null,
    var nighttimeWindChillTemperature: Int? = null,
    var nighttimeWetBulbTemperature: Int? = null,
    var nighttimeDegreeDayTemperature: Int? = null,

    var nighttimeTotalPrecipitation: Float? = null,
    var nighttimeThunderstormPrecipitation: Float? = null,
    var nighttimeRainPrecipitation: Float? = null,
    var nighttimeSnowPrecipitation: Float? = null,
    var nighttimeIcePrecipitation: Float? = null,

    var nighttimeTotalPrecipitationProbability: Float? = null,
    var nighttimeThunderstormPrecipitationProbability: Float? = null,
    var nighttimeRainPrecipitationProbability: Float? = null,
    var nighttimeSnowPrecipitationProbability: Float? = null,
    var nighttimeIcePrecipitationProbability: Float? = null,
    var nighttimeTotalPrecipitationDuration: Float? = null,

    var nighttimeThunderstormPrecipitationDuration: Float? = null,
    var nighttimeRainPrecipitationDuration: Float? = null,
    var nighttimeSnowPrecipitationDuration: Float? = null,
    var nighttimeIcePrecipitationDuration: Float? = null,

    var nighttimeWindDirection: String? = null,
    @field:Convert(
        converter = WindDegreeConverter::class,
        dbType = Float::class
    ) var nighttimeWindDegree: WindDegree? = null,
    var nighttimeWindSpeed: Float? = null,
    var nighttimeWindLevel: String? = null,

    var nighttimeCloudCover: Int? = null,

    // sun.
    var sunRiseDate: Date? = null,
    var sunSetDate: Date? = null,

    // moon.
    var moonRiseDate: Date? = null,
    var moonSetDate: Date? = null,

    // moon phase.
    var moonPhaseAngle: Int? = null,
    var moonPhaseDescription: String? = null,

    // aqi.
    var pm25: Float? = null,
    var pm10: Float? = null,
    var so2: Float? = null,
    var no2: Float? = null,
    var o3: Float? = null,
    var co: Float? = null,

    // pollen.
    var grassIndex: Int? = null,
    var grassLevel: Int? = null,
    var grassDescription: String? = null,
    var moldIndex: Int? = null,
    var moldLevel: Int? = null,
    var moldDescription: String? = null,
    var ragweedIndex: Int? = null,
    var ragweedLevel: Int? = null,
    var ragweedDescription: String? = null,
    var treeIndex: Int? = null,
    var treeLevel: Int? = null,
    var treeDescription: String? = null,

    // uv.
    var uvIndex: Int? = null,
    var uvLevel: String? = null,
    var uvDescription: String? = null,

    var hoursOfSun: Float? = null
)
