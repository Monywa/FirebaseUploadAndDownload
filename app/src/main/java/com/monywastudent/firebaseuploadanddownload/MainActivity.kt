package com.monywastudent.firebaseuploadanddownload

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {


    private val requestPermission= registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if(isGranted){
            //permission granted, perform the operation

        }else{
            //permission denied, handle accordingly (e.g., show a message, disable functionality)

        }
    }


    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadVideoToFirebaseStorage(uri)
        }
    }

    lateinit var  player:ExoPlayer
    var requestUrl="https://firebasestorage.googleapis.com/v0/b/movieapp-ade5a.appspot.com/o/upload?alt=media&token=471a5dd1-656d-49be-9b35-b40501525c21"

    var requestUri:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        val uploadBtn = findViewById<Button>(R.id.upload_btn)
        val downloadBtn = findViewById<Button>(R.id.download_btn)

        //upload Button is clicked
        uploadBtn.setOnClickListener {
            getContent.launch("video/*")
        }

        //download button is clicked
        downloadBtn.setOnClickListener {
            requestUri?.let {
                AndroidDownloader(this).downloadFile(it)
            }

            //request permission
            requestWriteExternalStoragePermission()
        }

//        findViewById<PlayerView>(R.id.player_view).setOnClickListener {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        }
    }

    private fun uploadVideoToFirebaseStorage(uri: Uri) {
        val storageRef = Firebase.storage.reference

        val fileRef = storageRef.child("upload")
        fileRef.putFile(uri).addOnSuccessListener {tasksnapshot->
            showToast("Success Upload")
            Log.d("Main Activity", "Success")
            tasksnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri->
                requestUri=uri
                setMediaPlayer(uri)
                Log.d("Main Activity",uri.toString())
            }


        }.addOnProgressListener { snapshot ->
            val progress = (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
            uploadingNotification(progress)
            Log.d("Main Activity", progress.toString())
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun uploadingNotification(duration: Double) {
        val channelId = "main channel Id"
        val channelName = "Upload Channel"

        //create the first notification manager
        val notificationManager = NotificationManagerCompat.from(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create the notification channel
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            showToast("Your Api is low")
        }


        //create th notification builder
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Uplaoding Video")
            .setContentText("Uploading..${duration.toInt()}%")
            .setProgress(100, 0, false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)




        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        builder.setProgress(100, duration.toInt(), false)
        notificationManager.notify(1, builder.build())

        if (duration.toInt() == 100) {
            builder.setProgress(0, 0, false)
            notificationManager.notify(1, builder.build())
        }
    }

    private fun setMediaPlayer(uri: Uri) {
         player = ExoPlayer.Builder(this).build()

        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.player = player

        // Build the media item.
        val mediaItem = MediaItem.fromUri(uri)
        // Set the media item to be played.
        player.setMediaItem(mediaItem)
        // Prepare the player.
        player.prepare()
        // Start the playback.
        player.play()

    }

    private fun requestWriteExternalStoragePermission(){
        val permission=Manifest.permission.WRITE_EXTERNAL_STORAGE
        if(ContextCompat.checkSelfPermission(
                this,
                permission
        )!=PackageManager.PERMISSION_GRANTED){
            requestPermission.launch(permission)
        }else{
            AndroidDownloader(this).downloadFile(requestUrl.toUri())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Enter full-screen mode
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
            supportActionBar?.hide()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Exit full-screen mode
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
        }
    }
}