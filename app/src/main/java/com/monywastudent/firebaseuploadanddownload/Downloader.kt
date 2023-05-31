package com.monywastudent.firebaseuploadanddownload

import android.net.Uri

interface Downloader {
    fun downloadFile(uri: Uri):Long
    fun downloadFile(url:String):Long
}