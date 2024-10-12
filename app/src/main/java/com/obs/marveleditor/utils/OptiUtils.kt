/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.obs.marveleditor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.jeevandeshmukh.glidetoastlib.GlideToast
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OptiUtils {

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory()
                .toString() + File.separator + OptiConstant.APP_NAME + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    @SuppressLint("SuspiciousIndentation")
    fun copyFileToInternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val path = File(context.getExternalFilesDir(null), "${OptiConstant.APP_NAME}/${OptiConstant.CLIP_ARTS}/")
        if (!path.exists()) {
            path.mkdirs()
        }

        val dataPath = File(path, "$resourceName.png")
        Log.v("OptiUtils", "path: ${dataPath.absolutePath}")
        try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                dataPath.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("copyFileToInternalStorage Error", e.localizedMessage)
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("copyFileToInternalStorage Error", e.localizedMessage)
            e.printStackTrace()
        }

        return dataPath
    }

    fun copyFontToInternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val path = File(context.getExternalFilesDir(null), "${OptiConstant.APP_NAME}/${OptiConstant.FONT}/")
        if (!path.exists()) {
            path.mkdirs()
        }

        val dataPath = File(path, "$resourceName.ttf")
        Log.v("OptiUtils", "path: ${dataPath.absolutePath}")
        try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                dataPath.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return dataPath
    }

    private fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshGalleryAlone(context: Context) {
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isVideoHaveAudioTrack(path: String): Boolean {
        var audioTrack = false

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
        audioTrack = hasAudioStr == "yes"

        return audioTrack
    }

    fun showGlideToast(activity: Activity, content: String) {
        GlideToast.makeToast(
            activity,
            content,
            GlideToast.LENGTHTOOLONG,
            GlideToast.FAILTOAST,
            GlideToast.TOP
        ).show()
    }

    fun createVideoFile(context: Context): File {
        // Tạo một timestamp để đảm bảo tên file là duy nhất
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(Date())
        val videoFileName: String = OptiConstant.APP_NAME + timeStamp + "_"

        // Sử dụng getExternalFilesDir để lưu trữ file trong thư mục ứng dụng
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        // Đảm bảo thư mục tồn tại
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }

        // Tạo file tạm thời với tên và định dạng đã chỉ định
        return File.createTempFile(videoFileName, OptiConstant.VIDEO_FORMAT, storageDir)
    }

    fun createAudioFile(context: Context): File {
        val timeStamp: String =
            SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!
        if (!storageDir.exists()) storageDir.mkdirs()
        return File.createTempFile(imageFileName, OptiConstant.AUDIO_FORMAT, storageDir)
    }

    fun getVideoDuration(context: Context, file: File): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time?.toLong()
        retriever.release()
        return timeInMillis ?: 0
    }
}


