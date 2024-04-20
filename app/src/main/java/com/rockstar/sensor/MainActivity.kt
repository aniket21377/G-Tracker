package com.rockstar.sensor

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rockstar.sensor.ui.theme.SensorTheme
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size

import androidx.compose.ui.platform.LocalContext
import com.github.mikephil.charting.utils.Utils
import kotlinx.coroutines.flow.map
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    lateinit var database: AppDatabase
    private var orientationAngles by mutableStateOf(Triple(0f, 0f, 0f))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       database = DatabaseManager.getDatabase(applicationContext)
        Utils.init(this)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            MaterialTheme {
                Surface() {
                    Column {
                        Text(text = "Welcome to G-Tracker", style = MaterialTheme.typography.displayLarge,color = androidx.compose.ui.graphics.Color.Magenta, modifier = Modifier.padding(16.dp))

                        GoToMainActivity2Button(applicationContext)
                        OrientationDisplay(orientationAngles, database)


                    }

                }
            }
        }

    }

    @Composable
    fun GoToMainActivity2Button(context: Context) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { startActivity(MainActivity2.createIntent(context)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View History")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Ignored for this example
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val (x, y, z) = event.values

            val pitch = String.format("%.3f", x.toDouble()).toDouble()
            val roll = String.format("%.3f", y.toDouble()).toDouble()
            val yaw = String.format("%.3f", z.toDouble()).toDouble()

            orientationAngles = Triple(pitch.toFloat(), roll.toFloat(), yaw.toFloat())
            // Store orientation data in the database
            val orientationData = OrientationData(pitch = pitch, roll = roll, yaw = yaw)

            CoroutineScope(Dispatchers.IO).launch {
                database.orientationDataDao().insert(orientationData)
            }
            // Log the values of x, y, and z
            Log.d("Accelerometer", "X: $x, Y: $y, Z: $z")
        }
    }
}

@Composable
fun OrientationDisplay(orientationAngles: Triple<Float, Float, Float>, database: AppDatabase) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pitch: ${orientationAngles.first}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Roll: ${orientationAngles.second}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Yaw: ${orientationAngles.third}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.tertiary

        )
    }
}
@Composable
fun LineChartWrapper(
    context: Context,
    modifier: Modifier = Modifier,
    lineData: LineData,
    xAxisPosition: XAxis.XAxisPosition
) {
    // Remember the chart to hold its reference across recompositions
    val chart = remember {
        LineChart(context).apply {
            // Set LineData
            data = lineData

            // Configure XAxis
            xAxis.position = xAxisPosition
            description.isEnabled = false
        }
    }

    // Apply the modifier to the Android View
    AndroidView(
        factory = { chart },
        modifier = modifier
    ) { view ->

        Log.d("LineChartWrapper", "Chart view size: ${view.measuredWidth} x ${view.measuredHeight}")
    }
}

@Composable
fun HistoricalGraphs(database: AppDatabase, context: Context) {
    var orientationDataState by remember {
        mutableStateOf(emptyList<OrientationData>())
    }

    var graphDisplayed by remember {
        mutableStateOf(false)
    }

    // Collect orientation data from the database only once
    LaunchedEffect(Unit) {
        if (!graphDisplayed) {
            database.orientationDataDao().getAllOrientationData()
                .collect { orientationData ->
                    orientationDataState = orientationData
                }
            graphDisplayed = true
        }
    }

    // Create LineData for each line (pitch, roll, yaw)
    val pitchData = LineDataSet(
        orientationDataState.mapIndexed { index, orientationData ->
            com.github.mikephil.charting.data.Entry(index.toFloat(), orientationData.pitch.toFloat())
        }, "Pitch"
    ).apply {
        color = Color.BLUE
        setDrawValues(false)
        setCircleColor(Color.BLUE)
    }

    val rollData = LineDataSet(
        orientationDataState.mapIndexed { index, orientationData ->
            com.github.mikephil.charting.data.Entry(index.toFloat(), orientationData.roll.toFloat())
        }, "Roll"
    ).apply {
        color = Color.RED
        setDrawValues(false)
        setCircleColor(Color.RED)
    }

    val yawData = LineDataSet(
        orientationDataState.mapIndexed { index, orientationData ->
            com.github.mikephil.charting.data.Entry(index.toFloat(), orientationData.yaw.toFloat())
        }, "Yaw"
    ).apply {
        color = Color.GREEN
        setDrawValues(false)
        setCircleColor(Color.GREEN)
    }

    // Plot the graphs if data is available
    if (orientationDataState.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                LineChartWrapper(
                    context = context,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    lineData = LineData(pitchData),
                    xAxisPosition = XAxis.XAxisPosition.BOTTOM
                )
            }
            item {
                LineChartWrapper(
                    context = context,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    lineData = LineData(rollData),
                    xAxisPosition = XAxis.XAxisPosition.BOTTOM
                )
            }
            item {
                LineChartWrapper(
                    context = context,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    lineData = LineData(yawData),
                    xAxisPosition = XAxis.XAxisPosition.BOTTOM
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewOrientationDisplay() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
//            OrientationDisplay(Triple(0f, 0f, 0f), AppDatabase()) // You may need to pass a mock database instance here
        }
    }
}