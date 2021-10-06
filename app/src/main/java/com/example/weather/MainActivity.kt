package com.example.weather

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.weather.databinding.ActivityMainBinding
import java.lang.Exception
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var  GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    private lateinit var binding: ActivityMainBinding

    private var temp = 0.0

    private var isCelsius = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        getLiveData()

        var cName = GET.getString("cityName", "Los Angles")?.toLowerCase()
        binding.tvCity.text = cName
       viewModel.refreshData(cName!!)

           binding.swipeRefreshLayout.setOnRefreshListener {
               var cityName = GET.getString("cityName", cName)?.toLowerCase()
                   binding.tvCity.text = cityName
                    viewModel.refreshData(cityName!!)
               binding.swipeRefreshLayout.isRefreshing =false
           }


        //animation

        val animate = binding.mainLayout.background as AnimationDrawable
        animate.setEnterFadeDuration(10)
        animate.setExitFadeDuration(2000)
        animate.start()





        binding.layoutClickTest.setOnClickListener {
               // var dialogFragment = CustomDialogFragment()
          //  dialogFragment.show(supportFragmentManager, "Custom Dialog")
            cityChangeDialogAlert()

        }

        binding.tvTemp.setOnClickListener {


            if (isCelsius){
                temp = Math.ceil(temp* 1.8 + 32)
                binding.tvTemp.text = "$temp °F"
                    isCelsius = false
            }else{
                temp = Math.ceil((temp -32) * 0.555)
                binding.tvTemp.text = "$temp °C"
                isCelsius = true
            }

        }

        binding.tvRefresh.setOnClickListener {
            getLiveData()
        }




    }

    private fun getLiveData(){

        try {

        viewModel.weatherList.observe(this, Observer { data ->
            data?.let{
               // binding.layoutMain.visibility = View.VISIBLE

                val today = Calendar.getInstance().time
                val timelong = today.time

                binding.tvCity.text = data.name.toString()
                binding.tvPercent.text = data.main.humidity.toString()
                binding.tvPresAmount.text = data.main.pressure.toString()
                binding.tvWind.text = data.wind.speed.toString()
                binding.tvHiTemp.text =data.main.temp_max.toString() + "°C"
                binding.tvLowTemp.text= data.main.temp_min.toString()+ "°C"

                binding.tvSky.text = data.weather.get(0).description

                binding.tvAM.text = timeFormat(data.sys.sunrise.toLong()).toString() +" AM"
                binding.tvPM.text = "${timeFormat(data.sys.sunset.toLong()).toString()} PM"
                binding.tvDate.text ="Last updated at: ${dateFormat(timelong).toString()}"

                temp = data.main.temp
                binding.tvTemp.text = " ${Math.ceil(data.main.temp)} °C"


            }
        })
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Failure: $e", Toast.LENGTH_LONG).show()
        }

    }
    private fun timeFormat(time: Long): String? {
        val date = Date(time * 1000L)
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm")
        //sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun dateFormat(date: Long): String? {
        val backtoDate = Date(date)
        val format = SimpleDateFormat("EEE, MMM d, ''yy")
        return format.format(backtoDate)


    }

    private fun cityChangeDialogAlert(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.change_city, null)

        val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
            //builder.setTitle("Change the City")

        //show dialog
        val alert = builder.show()

        val btnSend = dialogView.findViewById<Button>(R.id.btnSend)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)


        btnSend.setOnClickListener {
            val input = dialogView.findViewById<EditText>(R.id.etInputCity)
            val cityName = input.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
            alert.dismiss()
        }

        btnCancel.setOnClickListener {
            alert.dismiss()
        }


    }



}