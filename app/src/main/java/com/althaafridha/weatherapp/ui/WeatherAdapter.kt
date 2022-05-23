package com.althaafridha.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.althaafridha.weatherapp.BuildConfig
import com.althaafridha.weatherapp.data.ListItem
import com.althaafridha.weatherapp.databinding.RowItemWeatherBinding
import com.althaafridha.weatherapp.util.HelperFunction.formatterDegree
import com.althaafridha.weatherapp.util.sizeIconWeather2x
import com.althaafridha.weatherapp.util.sizeIconWeather4x
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter: RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

	private val listWeather = ArrayList<ListItem>()
	class MyViewHolder(val binding: RowItemWeatherBinding):
			RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder (
		RowItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		)

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val data = listWeather[position]
		holder.binding.apply {
			val maxTemp = "Max: " + formatterDegree(data.main?.tempMax)
			tvMaxTemp.text = maxTemp
			val minTemp = "Min: " + formatterDegree(data.main?.tempMin)
			tvMinTemp.text = minTemp

			val date = data.dtTxt?.take(10)
			val time = data.dtTxt?.takeLast(8)

			val dateArray = date?.split("-")?.toTypedArray()
			val timeArray = time?.split(":")?.toTypedArray()

			val calendar = Calendar.getInstance()

			// date
			calendar.set(Calendar.YEAR, Integer.parseInt(dateArray?.get(0) as String))
			calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))

			// time
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray?.get(0) as String))
			calendar.set(Calendar.MINUTE, 0)

			val dateFormat = SimpleDateFormat("EEE, MMM D", Locale.getDefault())
				.format(calendar.time).toString()
			tvDate.text = date

			val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
				.format(calendar.time).toString()
			tvTime.text = time

			val iconId = data.weather?.get(0)?.icon
			val iconUrl = BuildConfig.ICON_URL + iconId + sizeIconWeather2x
			Glide.with(imgIcWeather.context).load(iconUrl)
				.into(imgIcWeather)
		}
	}

	override fun getItemCount() = listWeather.size

	fun setData(data: List<ListItem>?) {
		if(data == null) return
		listWeather.clear()
		listWeather.addAll(data)
	}
}