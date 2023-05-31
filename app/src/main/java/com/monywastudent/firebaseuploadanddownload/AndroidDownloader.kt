package com.monywastudent.firebaseuploadanddownload

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(
    private val context:Context
):Downloader {

    val token="https://firebasestorage.googleapis.com/v0/b/movieapp-ade5a.appspot.com/o/upload?alt=media&token=471a5dd1-656d-49be-9b35-b40501525c21"

    private val downloaderManager:DownloadManager=context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(uri: Uri): Long {
        val request=DownloadManager.Request(uri)
            .setMimeType("video/mp4")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Sample Video.mp4")
            .addRequestHeader("Autorization","Bearer <token>")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Sample Video.mp4")

        return downloaderManager.enqueue(request)
    }

    override fun downloadFile(url: String): Long {
        val request=DownloadManager.Request(url.toUri())
            .setMimeType("video/mp4")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setTitle("Sample Video.mp4")
            .addRequestHeader("Autorization",token)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Sample Video.mp4")

        return downloaderManager.enqueue(request)
    }
}