package com.example.zentask.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

fun copyUriToCache(context: Context, uri: Uri): File? {
    return try {
        val file = File(context.cacheDir, "pick_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { input.copyTo(it) }
        }
        file
    } catch (e: Exception) {
        null
    }
}

fun imageToPdf(context: Context, imageFile: File): File? {
    return try {
        val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
        val raw = BitmapFactory.decodeFile(imageFile.absolutePath, opts) ?: return null

        val degrees = when (
            ExifInterface(imageFile.absolutePath)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val bitmap = if (degrees != 0f) {
            Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, Matrix().apply { postRotate(degrees) }, true)
        } else raw

        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create())
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdf.finishPage(page)

        val dir = File(context.filesDir, "scans").apply { mkdirs() }
        val out = File(dir, "scan_${System.currentTimeMillis()}.pdf")
        FileOutputStream(out).use { pdf.writeTo(it) }
        pdf.close()
        out
    } catch (e: Exception) {
        null
    }
}