package com.example.whetherappcompose.screens

import android.util.Half.toFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.whetherappcompose.ListItem
import com.example.whetherappcompose.R
import com.example.whetherappcompose.data.WeatherModel
import com.example.whetherappcompose.mainList
import com.example.whetherappcompose.ui.theme.PurpleGrey40
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(currentDay: MutableState<WeatherModel>, onClickSync:() -> Unit,onClickSearch:() -> Unit) {


    Column(
        modifier = Modifier

            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PurpleGrey40,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        text = currentDay.value.time,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    )
                    AsyncImage(
                        model = "https:"+ currentDay.value.icon,
                        contentDescription = "im2",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 8.dp)
                    )

                }
                Text(text = currentDay.value.city,
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Color.Black
                    ))
                Text(text = if (currentDay.value.currentTemp.isNotEmpty()) currentDay.value.currentTemp.toFloat().toInt().toString() +"°С"
                else "${currentDay.value.maxTemp.toFloat().toInt()}°С/${currentDay.value.minTemp.toFloat().toInt()}°С",
                    style = TextStyle(
                        fontSize = 65.sp,
                        color = Color.Black
                    ))
                Text(text = currentDay.value.condition,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    ))
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    IconButton(onClick = {
                        onClickSearch.invoke()

                    }) {
                        Icon(painter = painterResource(id = R.drawable.search), contentDescription = "search")
                        
                    }
                    Text(text = "${currentDay.value.maxTemp.toFloat().toInt()}°С/${currentDay.value.minTemp.toFloat().toInt()}°С",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ))
                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.sync), contentDescription = "sync")

                    }

                }
            }

            }

        }
    }

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("Hours", "Days")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { pos ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(pos[tabIndex]),
                    height = 2.dp,
                    color = Color.Black
                )

            },
            containerColor = PurpleGrey40
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(selected = false, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                    text = {
                        Text(text = text, color = Color.Black, fontSize = 20.sp)
                    })
            }

        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when(index){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            mainList(list = list, currentDay =currentDay )


        }

    }
}


private fun getWeatherByHours(hours:String): List<WeatherModel>{
    if(hours.isEmpty()) return  listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for(i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString()+"°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "","",""
            )
        )
    }
    return list
}