package com.monywastudent.firebaseuploadanddownload

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        val downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

//            val query = DownloadManager.Query().setFilterById(id)
//                .setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
//            downloadManager.query(query)
            if (id != -1L) {
                println("Download with ID $id finished!")
            }
        }
    }
}