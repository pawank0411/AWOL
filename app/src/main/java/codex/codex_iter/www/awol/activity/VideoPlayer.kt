package codex.codex_iter.www.awol.activity

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import codex.codex_iter.www.awol.R
import codex.codex_iter.www.awol.activity.VideoPlayer
import codex.codex_iter.www.awol.activity.VideoPlayer.NetworkChangeReceiver.NetworkChangeListener
import codex.codex_iter.www.awol.utilities.Constants
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class VideoPlayer : AppCompatActivity() {
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var progressBar: ProgressBar? = null
    private var fullScreenButton: ImageButton? = null
    private val runnableCalled = false
    private var videoUrl: String? = null
    private val videoId: String? = null
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var errorMessage: TextView? = null
    private var mediaSource: ProgressiveMediaSource? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playerview)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        window.decorView.setBackgroundColor(Color.BLACK)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        playerView = findViewById(R.id.exo_player_view)
        progressBar = findViewById(R.id.progress_bar)
        fullScreenButton = playerView!!.findViewById(R.id.exo_fullscreen_button)
        errorMessage = findViewById(R.id.error_message)
        networkChangeReceiver = NetworkChangeReceiver(object : NetworkChangeListener {
            override fun onNetworkChanged(isConnected: Boolean) {
                if (isConnected && errorMessage!!.visibility == View.VISIBLE) {
                    if (runnableCalled && player != null && mediaSource != null) {
                        player!!.retry()
                    } else {
                        releasePlayer()
                        initExoPlayer()
                    }
                } else {
                    setInit()
                }
            }
        })
        if (!isConnected(this)) {
            AlertDialog.Builder(this)
                    .setTitle("No internet connection")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                        dialog.dismiss()
                        finish()
                    }.create().show()
        }
        if (savedInstanceState == null) {
            setInit()
        }
        initExoPlayer()
    }

    private fun setInit() {
        val bundle = intent.extras
        if (bundle != null) videoUrl = bundle.getString(Constants.VIDEOURL)
        if (videoUrl == null || videoUrl!!.isEmpty()) {
            Toast.makeText(this, "Video Url is empty", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun initExoPlayer() {
        if (!isConnected(this)) {
            return
        } else {
            errorMessage!!.visibility = View.GONE
            errorMessage!!.text = ""
        }
        player = ExoPlayerFactory.newSimpleInstance(this)
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA) //CONTENT_TYPE_MOVIE
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build()
        player!!.setAudioAttributes(audioAttributes, true)
        if (playerView == null) {
            playerView = findViewById(R.id.exo_player_view)
        }
        playerView!!.player = player
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_enter)
        } else {
            fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_exit)
        }
        fullScreenButton!!.setOnClickListener {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_enter)
            } else {
                // In portrait
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_exit)
            }
        }
        val dataSourceFactory1: DataSource.Factory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)))
        val uri = Uri.parse(videoUrl)
        //        mediaSource = new HlsMediaSource.Factory(dataSourceFactory1).createMediaSource(uri);
        mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory1).createMediaSource(uri)
        player!!.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    ExoPlayer.STATE_READY -> progressBar!!.visibility = View.GONE
                    ExoPlayer.STATE_BUFFERING -> {
                        progressBar!!.visibility = View.VISIBLE
                        errorMessage!!.visibility = View.GONE
                        errorMessage!!.text = ""
                    }
                    ExoPlayer.STATE_ENDED -> {
                    }
                    Player.STATE_IDLE -> {
                        TODO()
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {
                Log.e("ExoPlaybackException", error.message, error)
                progressBar!!.visibility = View.GONE
                if (isConnected(this@VideoPlayer)) {
                    errorMessage!!.visibility = View.VISIBLE
                } else {
                    errorMessage!!.visibility = View.VISIBLE
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare(mediaSource, true, false)
        player!!.playWhenReady = true
        playerView!!.visibility = View.VISIBLE
        playerView!!.keepScreenOn = true
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.playWhenReady = true
            player!!.stop()
            player!!.release()
            player = null
            playerView!!.player = null
            playerView = null
        }
    }

    private fun pausePlayer() {
        if (player != null) {
            player!!.playWhenReady = false
            player!!.playbackState
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
        pausePlayer()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (playerView == null) {
            playerView = findViewById(R.id.exo_player_view)
        }
        if (fullScreenButton == null) {
            fullScreenButton = playerView!!.findViewById(R.id.exo_fullscreen_button)
        }
        try {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_enter)
            } else {
                fullScreenButton!!.setImageResource(R.drawable.exo_controls_fullscreen_exit)
            }
        } catch (e: NullPointerException) {
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        } else {
            showSystemUI()
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onBackPressed()
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    class NetworkChangeReceiver(private val networkChangeListener: NetworkChangeListener?) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null && intent.action == ConnectivityManager.CONNECTIVITY_ACTION && networkChangeListener != null) networkChangeListener.onNetworkChanged(isConnected(context))
        }

        private fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    ?: return false
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

        interface NetworkChangeListener {
            fun onNetworkChanged(isConnected: Boolean)
        }

    }

}