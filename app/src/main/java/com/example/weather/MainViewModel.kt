package com.example.weather

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.models.WeatherData

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers



class MainViewModel: ViewModel() {
    private val weatherService = WeatherService()
    private val disposal = CompositeDisposable()


    val weatherList = MutableLiveData<WeatherData>()



    fun refreshData(cityName: String){
        getDataFromApi(cityName)
       // getDataFromLocal()
    }

    private fun getDataFromApi(cityName: String) {



        disposal.add(
            weatherService.getDataService(cityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(object : DisposableSingleObserver<WeatherData>() {

                    override fun onSuccess(t: WeatherData){
                        weatherList.value =t
                        Log.d("ViewModel","onSuccess: Success")
                    }


                    override fun onError(e: Throwable) {
                        Log.d("ViewModel", "onError: $e")
                    }
                })


        )
    }
}