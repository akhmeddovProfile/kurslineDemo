package com.example.kurslinemobileapp.adapter

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

class ResizeTransformation(private val targetWidth: Int, private val targetHeight: Int) :
    Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val aspectRatio = source.width.toDouble() / source.height.toDouble()
        val targetAspectRatio = targetWidth.toDouble() / targetHeight.toDouble()

        val scaleFactor = if (aspectRatio > targetAspectRatio) {
            targetWidth.toDouble() / source.width.toDouble()
        } else {
            targetHeight.toDouble() / source.height.toDouble()
        }

        val scaledWidth = (source.width * scaleFactor).toInt()
        val scaledHeight = (source.height * scaleFactor).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, false)
        if (resizedBitmap != source) {
            source.recycle()
        }

        return resizedBitmap
    }

    override fun key(): String {
        return "resize($targetWidth,$targetHeight)"
    }
}