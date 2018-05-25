package android.prosotec.proyectocierzo

///////////////////////////////////
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_local_player.*
///////////////////////////


import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat
import android.widget.*

import com.chibde.visualizer.LineVisualizer
import com.example.android.mediasession.R
import android.media.MediaMetadataRetriever





/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LocalPlayerActivity : AppCompatActivity() {

    private var mlineVisualizer: LineVisualizer? = null
    private var mMediaPlayer = MediaPlayer()

    private var uri: Uri? = null

    private val mSeekbarUpdateHandler = Handler()
    private val mUpdateSeekbar = object : Runnable {
        override fun run() {
            seek_bar_local.setProgress(mMediaPlayer.currentPosition)
            current_time.text = setTextTime(mMediaPlayer.currentPosition)
            mSeekbarUpdateHandler.postDelayed(this, 50)
        }
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_local_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clickListener = ClickListener()
        findViewById<View>(R.id.ib_prev).setOnClickListener(clickListener)
        findViewById<View>(R.id.play).setOnClickListener(clickListener)
        findViewById<View>(R.id.ib_next).setOnClickListener(clickListener)

        prepareSong()
        prepareUI()
        prepareVisualizer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.release()
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
    }

     private fun prepareSong(){
        // Código para capturar el intent de mp3 en local.
        if (Intent.ACTION_VIEW == intent.action) {
            uri = intent.data
            val metaRetriver: MediaMetadataRetriever
            metaRetriver = MediaMetadataRetriever()
            metaRetriver.setDataSource(this, uri)
            song_name.text = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            artist_name.text = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.setDataSource(getApplicationContext(), uri)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        }
    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.ib_prev -> mMediaPlayer.seekTo(mMediaPlayer.currentPosition - 10000)
                R.id.play -> if (mMediaPlayer.isPlaying) {
                    mMediaPlayer.pause()

                } else {
                    mMediaPlayer.start()
                }
                R.id.ib_next -> mMediaPlayer.seekTo(mMediaPlayer.currentPosition + 30000)
            }
        }
    }

    private fun prepareVisualizer(){
        mlineVisualizer = LineVisualizer(this)
        var mainLayout: LinearLayout = findViewById(R.id.fullscreen_content)
        mainLayout.addView(mlineVisualizer)
        mlineVisualizer?.setColor(ContextCompat.getColor(this, R.color.colorAccent))
        mlineVisualizer?.setStrokeWidth(5)
        mlineVisualizer?.setPlayer(mMediaPlayer)
    }

    private fun prepareUI(){
        seek_bar_local.max = mMediaPlayer.duration
        final_time.text = setTextTime(mMediaPlayer.duration)
        current_time.text = setTextTime(0)
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

        seek_bar_local.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                if(fromUser){
                    mMediaPlayer.seekTo(progress)
                    current_time.text = setTextTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                current_time.text = setTextTime(mMediaPlayer.currentPosition)
            }
        })
    }

    private fun setTextTime(duration: Int): String{
        var finalTimerString: String
        var secondsString: String

        val minutes  = (duration % (1000*60*60) / (1000*60))
        val seconds =  (duration % (1000*60*60) % (1000*60) / 1000)

        if(seconds < 10){
            secondsString = "0" + seconds
        }else{
            secondsString = "" + seconds
        }

        finalTimerString = "" + minutes + ":" + secondsString
        return finalTimerString
    }

}