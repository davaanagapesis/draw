package com.example.draw.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

object TensorFlowHelper {

    fun loadModelFile(context: Context, modelFileName: String): Interpreter {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelFileName)
        val inputStream = fileDescriptor.createInputStream()
        val byteArray = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(byteArray.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(byteArray)

        return Interpreter(buffer)
    }

    fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 28 * 28) // Sesuaikan ukuran input
        byteBuffer.order(ByteOrder.nativeOrder())

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 28, 28, true) // Ukuran 28x28 sesuai MNIST
        val pixels = IntArray(28 * 28)
        resizedBitmap.getPixels(pixels, 0, 28, 0, 0, 28, 28)

        for (pixel in pixels) {
            val normalizedPixelValue = (pixel and 0xFF) / 255.0f // Normalisasi warna menjadi nilai antara 0 dan 1
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }
}
