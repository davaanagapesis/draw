package com.example.draw

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.draw.utils.TensorFlowHelper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.tensorflow.lite.Interpreter

class MainActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private lateinit var pieChart: PieChart
    private lateinit var detectedNumberText: TextView
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        detectedNumberText = findViewById(R.id.detectedNumberText)
        pieChart = findViewById(R.id.pieChart)
        drawingView = findViewById(R.id.drawingView)

        // Load TensorFlow Lite model
        interpreter = TensorFlowHelper.loadModelFile(this, "model.tflite")

        drawingView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                detectNumberAndDisplayPercentage()
                drawingView.clear()
            }
            true
        }
    }

    private fun detectNumberAndDisplayPercentage() {
        val bitmap = drawingView.getBitmap()
        val inputBuffer = TensorFlowHelper.convertBitmapToByteBuffer(bitmap)

        val result = Array(1) { FloatArray(10) }
        interpreter.run(inputBuffer, result)

        val detectedNumber = result[0].indexOf(result[0].maxOrNull() ?: -1f)
        detectedNumberText.text = "Hasil: $detectedNumber"

        updatePieChart(result[0])
    }

    private fun updatePieChart(predictions: FloatArray) {
        val entries = ArrayList<PieEntry>()
        for (i in predictions.indices) {
            entries.add(PieEntry(predictions[i] * 100, i.toString()))
        }
        val dataSet = PieDataSet(entries, "Prediksi")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }
}
