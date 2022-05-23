package com.althaafridha.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.althaafridha.weatherapp.BuildConfig
import com.althaafridha.weatherapp.R
import com.althaafridha.weatherapp.data.ForecastResponse
import com.althaafridha.weatherapp.data.WeatherResponse
import com.althaafridha.weatherapp.databinding.ActivityMainBinding
import com.althaafridha.weatherapp.util.HelperFunction.formatterDegree
import com.althaafridha.weatherapp.util.sizeIconWeather4x
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

	private var _binding: ActivityMainBinding? = null
	private val binding get() = _binding as ActivityMainBinding

	private var _viewModel: MainViewModel? = null
	private val viewModel get() = _viewModel as MainViewModel

	private val mAdapter by lazy { WeatherAdapter() }

	private var isLoading: Boolean? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		window.setFlags(
			WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
			WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
		)

		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		_viewModel = ViewModelProvider(this)[MainViewModel::class.java]

		searchCity()
		getWeatherByCity()
		getWeatherByCurrentLocation()
	}

	private fun getWeatherByCity(){
		viewModel.getWeatherByCity().observe(this) { setupView(it, null) }
		viewModel.getForecastByCity().observe(this) { setupView(null, it) }
	}

	private fun setupView(weather: WeatherResponse?, forecast: ForecastResponse?) {
		binding.apply {
			weather?.let {
				tvCity.text = it.name
				tvDegree.text = formatterDegree(it.main?.temp)

				val iconId = it.weather?.get(0)?.icon
				val iconUrl = BuildConfig.ICON_URL + iconId + sizeIconWeather4x
				Glide.with(this@MainActivity).load(iconUrl)
					.into(imgIcWeather)

				setupBackgroundImage(it.weather?.get(0)?.id, iconId)
			}
			mAdapter.setData(forecast?.list)
			binding.rvWeather.apply {
				layoutManager = LinearLayoutManager(
					this.context,
					LinearLayoutManager.HORIZONTAL,
					false
				)
				adapter = mAdapter
			}
		}
	}

	private fun setupBackgroundImage(idWeather: Int?, day: String?) {
		idWeather?.let {
			when (idWeather) {
				in resources.getIntArray(R.array.thunderstorm_id_list) ->
					setImageBackground(R.drawable.thunderstorm)
				in resources.getIntArray(R.array.drizzle_id_list) ->
					setImageBackground(R.drawable.drizzle)
				in resources.getIntArray(R.array.dust_id_list) ->
					setImageBackground(R.drawable.dust)
				in resources.getIntArray(R.array.heavy_clouds_id_list) ->
					setImageBackground(R.drawable.heavycloud)
				in resources.getIntArray(R.array.rain_id_list) ->
					setImageBackground(R.drawable.rain)
				in resources.getIntArray(R.array.freezing_rain_id_list) ->
					setImageBackground(R.drawable.freezing_rain)
				in resources.getIntArray(R.array.snow_id_list) ->
					setImageBackground(R.drawable.snow)
				in resources.getIntArray(R.array.sand_id_list) ->
					setImageBackground(R.drawable.sand)
				in resources.getIntArray(R.array.sleet_id_list) ->
					setImageBackground(R.drawable.sleet)

				in resources.getIntArray(R.array.clear_id_list) ->
					when (day) {
						"01d" -> setImageBackground(R.drawable.clear)
						"01n" -> setImageBackground(R.drawable.clear_night)
					}

				in resources.getIntArray(R.array.clouds_id_list) ->
					setImageBackground(R.drawable.lightcloud)
				in resources.getIntArray(R.array.heavy_clouds_id_list) ->
					setImageBackground(R.drawable.heavycloud)
				in resources.getIntArray(R.array.fog_id_list) ->
					setImageBackground(R.drawable.fog)
				in resources.getIntArray(R.array.squalls_id_list) ->
					setImageBackground(R.drawable.squalls)
				in resources.getIntArray(R.array.tornado_id_list) ->
					setImageBackground(R.drawable.tornado)
			}
		}
	}

	private fun setImageBackground(image: Int) {
		Glide.with(this).load(image).into(binding.imgBgWeather)
	}

	private fun getWeatherByCurrentLocation() {

		isLoading = true
		loadingStateView()

		val fusedLocationClient: FusedLocationProviderClient =
			LocationServices.getFusedLocationProviderClient(this)

		if (ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_COARSE_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
				ActivityCompat.requestPermissions(
					this,
					arrayOf(
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION
					),
					1000
				)
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return
		}
		fusedLocationClient.lastLocation
			.addOnSuccessListener {
				try {
					val lat = it.latitude
					val lon = it.longitude

//					viewModel.weatherByCurrentLocation(lat, lon)
//					viewModel.forecastByCurrentLocation(lat, lon)

				} catch (e: Throwable) {
					Log.e("MainActivity", "Couldn't get latitude & longitude.")
				}
			}
			.addOnFailureListener {
				Log.e("MainActivity", "Failed getting current location")
			}

		viewModel.weatherByCurrentLocation(0.0, 0.0)
		viewModel.forecastByCurrentLocation(0.0, 0.0)

		viewModel.getWeatherByCurrentLocation().observe(this) { setupView(it, null) }

		viewModel.getForecastByCurrentLocation().observe(this) {
			setupView(null, it)
			isLoading = false
			loadingStateView()
		}
	}

	private fun searchCity() {
		binding.edtSearch.setOnQueryTextListener(
			object : SearchView.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String?): Boolean {
					query?.let {
						isLoading = true
						loadingStateView()
						try {
							val inputMethodManager =
								getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
							inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
						} catch (e: Throwable) {
							Log.e("MainActivity", e.toString() )
						}
						viewModel.weatherByCity(it)
						viewModel.forecastByCity(it)
					}
					isLoading = false
					loadingStateView()
					return true
				}

				override fun onQueryTextChange(newText: String?): Boolean {
					return false
				}

			}
		)
	}

	private fun loadingStateView(){
		binding.apply {
			when (isLoading) {
				true -> {
					layoutWeather.visibility = View.INVISIBLE
					progressBar.visibility = View.VISIBLE
				}
				false -> {
					layoutWeather.visibility = View.VISIBLE
					progressBar.visibility = View.INVISIBLE
				}
				else -> {
					layoutWeather.visibility = View.INVISIBLE
					progressBar.visibility = View.VISIBLE
				}
			}
		}
	}
}