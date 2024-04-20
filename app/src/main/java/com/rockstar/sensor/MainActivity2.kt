package com.rockstar.sensor

import android.content.Context
import android.content.Intent
//import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.rockstar.sensor.databinding.ActivityMainBinding


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

class MainActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recreate the database instance
        database = DatabaseManager.getDatabase(applicationContext)

        // Check if the database was successfully created
        if (database.isOpen) {
            Log.d("MainActivity2", "Database created successfully")
        } else {
            Log.e("MainActivity2", "Failed to create database")
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainActivity2Content()

                }
            }
        }
    }

    @Composable
    fun MainActivity2Content() {
        val text = AnnotatedString.Builder().apply {
            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                append("Welcome ")
            }
            withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                append("to ")
            }
            withStyle(style = SpanStyle(color = Color.Magenta, fontWeight = FontWeight.Bold)) {
                append("G-Tracker ")
            }
        }.toAnnotatedString()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge ,
                fontSize = 30.sp,
                modifier = Modifier.padding(vertical = 1.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "History",
                style = MaterialTheme.typography.bodyLarge ,fontSize = 30.sp,
                modifier = Modifier.padding(vertical = 1.dp),
                textAlign = TextAlign.Center,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )
            BackToMainActivityButton()
            HistoricalGraphs(database, context = LocalContext.current)
        }
    }

    @Composable
    fun BackToMainActivityButton() {
        val context = LocalContext.current

        Box(
           modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = { (context as? ComponentActivity)?.finish() },
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Return")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        fun createIntent(context: Context): Intent? {
            return Intent(context, MainActivity2::class.java).apply {
                putExtra("orientation", "Hello")

            }

        }
    }
}


