package com.example.whetherappcompose

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.whetherappcompose.data.WeatherModel
import com.example.whetherappcompose.screens.MainCard
import com.example.whetherappcompose.screens.TabLayout
import com.example.whetherappcompose.ui.theme.WhetherAppComposeTheme
import org.json.JSONObject

const val API_KEY = "Your API KEY"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhetherAppComposeTheme {
                val daysList = remember{
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember{
                    mutableStateOf(false)
                }

                val currentDay = remember{
                    mutableStateOf(WeatherModel(
                        "","","0.0","","","0.0","0.0",""
                    ))
                }
                if(dialogState.value) {DialogSearch(dialogState, OnSabmit = {
                    getData(it, this,daysList, currentDay)
                })}
                getData("Alanya", this,daysList, currentDay)
                Image(
                    painter = painterResource(id = R.drawable.android), contentDescription = "image",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("Alanya", this@MainActivity,daysList, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true

                    })
                    TabLayout(daysList, currentDay)
                }


            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
val line = Volley.newRequestQueue(context)
    val stRequest = StringRequest(
        Request.Method.GET,
        url,{
            responce ->
            val list = getWeatherByDays(responce)
            currentDay.value = list[0]
            daysList.value = list
        },{
            error->
        }

    )
    line.add(stRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel>{
    if(response.isEmpty()) return  listOf()
    val mainObject = JSONObject(response)
    val list = ArrayList<WeatherModel>()
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for(i in 0..days.length()-1){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()


            )
        )

    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

