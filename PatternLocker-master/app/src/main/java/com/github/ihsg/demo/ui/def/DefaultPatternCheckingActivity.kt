package com.github.ihsg.demo.ui.def

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import com.github.ihsg.demo.R
import com.github.ihsg.demo.util.PatternHelper
import com.github.ihsg.demo.util.RecordHelper
import com.github.ihsg.patternlocker.OnPatternChangeListener
import com.github.ihsg.patternlocker.PatternLockerView
import kotlinx.android.synthetic.main.activity_default_pattern_checking.*
import java.lang.Exception


class DefaultPatternCheckingActivity : AppCompatActivity() {
    private var patternHelper: PatternHelper? = null
    private var recorderHelper: RecordHelper?= null
    private var isBegin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_pattern_checking)

        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
        }
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        //patternLockerView.linkedLineView = null
        //patternLockerView.hitCellView = null
        patternLockerView.setOnPatternChangedListener(object : OnPatternChangeListener {
            override fun onStart(view: PatternLockerView) {
                // 滑动开始时调用
                if (isBegin){
                    Toast.makeText(applicationContext, "begin to record", Toast.LENGTH_SHORT).show()
                    try {
                        recorderHelper!!.BeginRecord(applicationContext, 1)
                    }catch (e: Exception){
                        Log.println(Log.ERROR, "record", e.message)
                    }
                    showNotification(view);
                    isBegin = false
                }
            }

            override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {}

            override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
                val isError = !isPatternOk(hitIndexList)
                view.updateStatus(isError)
                //patternIndicatorView.updateState(hitIndexList, isError)
                updateMsg()
            }

            override fun onClear(view: PatternLockerView) {
                finishIfNeeded()
            }
        })

        //this.textMsg.text = "绘制解锁图案"
        this.patternHelper = PatternHelper()
        this.recorderHelper = RecordHelper()
    }

    fun showNotification(view: View)
    {
        // CHANNEL_ID：通道ID，可在类 MainActivity 外自定义。如：val CHANNEL_ID = 'msg_1'
        val builder = NotificationCompat.Builder(this, "msg")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("录音提醒")
                .setContentText("正在录音中...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
        // 显示通知
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("msg", "record", importance).apply {description = "this is a record tip"}
            // 注册通道(频道)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isPatternOk(hitIndexList: List<Int>): Boolean {
        this.patternHelper!!.validateForChecking(hitIndexList)
        return this.patternHelper!!.isOk
    }

    private fun updateMsg() {
        /*this.textMsg.text = this.patternHelper!!.message
        this.textMsg.setTextColor(if (this.patternHelper!!.isOk)
            ContextCompat.getColor(this, R.color.colorPrimary)
        else
            ContextCompat.getColor(this, R.color.colorAccent))*/
    }

    private fun finishIfNeeded() {
        if (this.patternHelper!!.isFinish) {
            recorderHelper!!.StopRecord()
            finish()
        }
    }

    companion object {

        fun startAction(context: Context) {
            val intent = Intent(context, DefaultPatternCheckingActivity::class.java)
            context.startActivity(intent)
        }
    }
}
