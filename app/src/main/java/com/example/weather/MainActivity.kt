package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView // Ensure the correct SearchView is imported
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.airbnb.lottie.LottieAnimationView

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Mumbai")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) fetchWeatherData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can handle text changes here if needed
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "//use your api key//", "metric")
        response.enqueue(object : Callback<Weather> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.currenttemp.text = "$temperature °C"
                    binding.humid.text = "$humidity %"
                    binding.windspd.text = "$windSpeed m/s"
                    binding.con.text = condition
                    binding.maxtemp.text = "Max Temp: $maxTemp °C"
                    binding.mintemp.text = "Min Temp: $minTemp °C"
                    binding.sunrise.text = formatTime(sunRise)
                    binding.sunset.text = formatTime(sunSet)
                    binding.sealevel.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = cityName
                    binding.cond.text= condition

                    changeImageATC(condition)

                } else {
                    Log.d("WeatherData", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d("WeatherData", "Failure: ${t.message}")
            }
        })
    }

    private fun changeImageATC(condition: String) {
        val animationView = findViewById<LottieAnimationView>(R.id.lottie)
        when (condition) {
            "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.hazzyy)
                animationView.setAnimation(R.raw.snow)
            }
            "Clear" -> {
               binding.root.setBackgroundResource(R.drawable.sunny_background)
                animationView.setAnimation(R.raw.cloud)
            }
            "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                animationView.setAnimation(R.raw.rain)
            }
            "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                animationView.setAnimation(R.raw.snow)
            }
            "Thunderstorm" -> {
                binding.root.setBackgroundResource(R.drawable.thunder)
                animationView.setAnimation(R.raw.thunder)
            }
            "Fog" -> {
                binding.root.setBackgroundResource(R.drawable.fog)
                animationView.setAnimation(R.raw.fog)
            }
            "Drizzle" -> {
                binding.root.setBackgroundResource(R.drawable.drizzle)
               animationView.setAnimation(R.raw.drizzle)
            }
            "Mist" -> {
                binding.root.setBackgroundResource(R.drawable.mist)
                animationView.setAnimation(R.raw.fog)
            }

            "Clouds" -> {
                binding.root.setBackgroundResource(R.drawable.cloud)
               animationView.setAnimation(R.raw.cld)
            }
            "Smoke" -> {
                binding.root.setBackgroundResource(R.drawable.smmokee)
                animationView.setAnimation(R.raw.fog)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                animationView.setAnimation(R.raw.sunny)
            }
        }

        animationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun formatTime(timestamp: Int): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp.toLong() * 1000))
    }
}
