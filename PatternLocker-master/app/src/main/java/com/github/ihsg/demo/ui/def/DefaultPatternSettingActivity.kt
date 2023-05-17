package com.github.ihsg.demo.ui.def

//import kotlinx.android.synthetic.main.activity_default_pattern_checking.patternIndicatorView
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_default_pattern_checking.patternLockerView
import kotlinx.android.synthetic.main.activity_default_pattern_setting.*

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class DefaultPatternSettingActivity : AppCompatActivity() {
    private var patternHelper: PatternHelper? = null
    private var recorderHelper: RecordHelper?= null
    private val num = 5
    private var isBegin = true
    private val message = Message()

    fun showNotification(view: View)
    {
        // CHANNEL_ID：通道ID，可在类 MainActivity 外自定义。如：val CHANNEL_ID = 'msg_1'
        val builder = NotificationCompat.Builder(this, "msg")
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("录音提醒")
//                .setContentText("正在录音中...")
                //.setContentTitle("Notification")
                //.setContentText("Recording...")
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_pattern_setting)
        //patternIndicatorView.updateState(Arrays.asList(1, 2), false)
        createNotificationChannel()
        InitNumber()
        waterWaveView.startWave()
        this.patternHelper = PatternHelper()
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



        patternLockerView.setOnPatternChangedListener(object : OnPatternChangeListener {
            override fun onStart(view: PatternLockerView) {
                // 滑动开始时调用
                if (isBegin){
                    Toast.makeText(applicationContext, "开始绘制", Toast.LENGTH_SHORT).show()
                    try {
                        recorderHelper!!.BeginRecord(applicationContext, 0)
                    }catch (e:Exception){
                        Log.println(Log.ERROR, "record", e.message)
                    }
                    showNotification(view);
                    isBegin = false
                }
            }

            override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {
                // Toast.makeText(applicationContext, "change", Toast.LENGTH_SHORT).show()
            }

            override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
                // Toast.makeText(applicationContext, "finish", Toast.LENGTH_SHORT).show()
                val isOk = isPatternOk(hitIndexList)
                DecrementCount()
                view.updateStatus(!isOk)
                //patternIndicatorView!!.updateState(hitIndexList, !isOk)
                updateMsg()
                //这里更新还需绘制的次数和提示内容
            }

            override fun onClear(view: PatternLockerView) {
                finishIfNeeded()
            }
        })

        // this.textMsg.text = "设置解锁图案"
        this.textMsg.text = "设置解锁图案"
        this.recorderHelper = RecordHelper()
    }

    private fun isPatternOk(hitIndexList: List<Int>): Boolean {
        this.patternHelper!!.validateForSetting(hitIndexList)
        return this.patternHelper!!.isOk
    }

    fun InitNumber(){
        val countText: TextView = findViewById(R.id.number);
        countText.text = (num).toString()
    }

    fun DecrementCount() {
        waterWaveView.startRise()
        handleCounter(-1)
    }

    private fun GetIsBegin() :Boolean{
        return this.isBegin
    }

    private fun SetIsBegin(flag : Boolean){
        this.isBegin = flag
    }

    private fun updateMsg() {
        this.textMsg.text = this.patternHelper!!.message
        val countText: TextView = findViewById(R.id.number);

        if (this.patternHelper!!.isOk){
            this.textMsg.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            Toast.makeText(applicationContext,"剩余次数: ${countText.text}", Toast.LENGTH_SHORT).show()
        }else{
            InitNumber()
            Toast.makeText(applicationContext,"剩余次数: ${countText.text}", Toast.LENGTH_SHORT).show()
            waterWaveView.startFall()
            SetIsBegin(true)
            recorderHelper!!.StopRecord()
            this.textMsg.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        }

    }

    private fun finishIfNeeded() {
        if (this.patternHelper!!.isFinish) {
            recorderHelper!!.StopRecord()
            finish()
        }
    }


    private fun handleCounter(sum: Int) {
        val countText: TextView = findViewById(R.id.number);

        countText.text = (sum + countText.text.toString().toInt()).toString()
        Log.d("count:", (countText.text as? String).toString())

    }


    companion object {

        fun startAction(context: Context) {
            val intent = Intent(context, DefaultPatternSettingActivity::class.java)
            context.startActivity(intent)
        }
    }
}