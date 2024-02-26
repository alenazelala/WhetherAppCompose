package com.example.whetherappcompose

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.whetherappcompose.data.WeatherModel
import com.example.whetherappcompose.ui.theme.PurpleGrey40
@Composable
fun mainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(
            list
        ) { _, item ->

            ListItem(item, currentDay)
        }
    }
}

@Composable
fun ListItem(item:WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
            .clickable {
                if (item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },

        colors = CardDefaults.cardColors(
            containerColor = PurpleGrey40,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 8.dp, top = 5.dp, bottom = 5.dp)) {
                Text(text = item.time)
                Text(text = item.condition)
            }
            Text(text = item.currentTemp.ifEmpty { "${item.maxTemp.toFloat().toInt()}°C/${item.minTemp.toFloat().toInt()}°C" }, style = TextStyle(fontSize = 25.sp))
            AsyncImage(model = "https:${item.icon}", contentDescription = "listImage",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp))

        }
    }

}


@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, OnSabmit:(String)-> Unit) {
    val dialog = remember{
        mutableStateOf("")
    }
    AlertDialog(onDismissRequest = {
dialogState.value = false
    },
        confirmButton = {
            TextButton(onClick = {
                OnSabmit(dialog.value)
                dialogState.value = false
            }) {
                Text(text = "OK")
            }
        }, dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "CANCEL")
            }
        },

        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Введите название города:")
                TextField(value = dialog.value, onValueChange = {
                    dialog.value = it
                })
            }

        })
}