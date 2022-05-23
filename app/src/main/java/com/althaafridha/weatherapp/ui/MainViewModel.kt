package com.althaafridha.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.althaafridha.weatherapp.data.ForecastResponse
import com.althaafridha.weatherapp.data.WeatherResponse
import com.althaafridha.weatherapp.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

	val weatherByCity = MutableLiveData<WeatherResponse>()
	val forecastByCity = MutableLiveData<ForecastResponse>()

	val weatherByCurrentLocation = MutableLiveData<WeatherResponse>()
	val forecastByCurrentLocation = MutableLiveData<ForecastResponse>()

	fun weatherByCity(city: String){
		ApiConfig.getApiService().weatherByCity(city).enqueue(object : Callback<WeatherResponse>{
			override fun onResponse(
				call: Call<WeatherResponse>,
				response: Response<WeatherResponse>
			) {
				if(response.isSuccessful) weatherByCity.postValue(response.body())
			}

			override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
				Log.e("Failure", t.message.toString())
			}

		})
	}

	fun getWeatherByCity(): LiveData<WeatherResponse> = weatherByCity
	
	fun forecastByCity(city: String) {
		ApiConfig.getApiService().forecastByCity(city).enqueue(object : Callback<ForecastResponse> {
			override fun onResponse(
				call: Call<ForecastResponse>,
				response: Response<ForecastResponse>
			) {
				if (response.isSuccessful) forecastByCity.postValue(response.body())
			}

			override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
				TODO("Not yet implemented")
			}

		})
	}

	fun getForecastByCity(): LiveData<ForecastResponse> = forecastByCity

	fun weatherByCurrentLocation(lat: Double, lon: Double) {
		ApiConfig.getApiService().weatherByCurrentLocation(lat, lon).enqueue(object : Callback<WeatherResponse>{
			override fun onResponse(
				call: Call<WeatherResponse>,
				response: Response<WeatherResponse>
			) {
				weatherByCurrentLocation.postValue(response.body())
			}

			override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
				TODO("Not yet implemented")
			}
		})
	}

	fun getWeatherByCurrentLocation(): LiveData<WeatherResponse> = weatherByCurrentLocation

	fun forecastByCurrentLocation(lat: Double, lon: Double){
		ApiConfig.getApiService().forecastByCurrentLocation(lat, lon).enqueue(object : Callback<ForecastResponse>{
			override fun onResponse(
				call: Call<ForecastResponse>,
				response: Response<ForecastResponse>
			) {
				forecastByCurrentLocation.postValue(response.body())
			}

			override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
				TODO("Not yet implemented")
			}
		})
	}

	fun getForecastByCurrentLocation(): LiveData<ForecastResponse> = forecastByCurrentLocation
}