package com.example.weather

import com.example.weather.models.WeatherData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


//https://api.openweathermap.org/data/2.5/weather?q=Jeddah&appid=16af880d4178964f12c73d68e482a647

interface ApiInterface {

    @GET("data/2.5/weather?&units=metric&APPID=16af880d4178964f12c73d68e482a647")

    fun getData(

        @Query("q") cityName: String
    ): Single<WeatherData>

}