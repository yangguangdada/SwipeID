package com.github.ihsg.demo.ui.def

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.github.ihsg.demo.R
import kotlinx.android.synthetic.main.activity_default_style.*


class DefaultStyleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_style)
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
        btnSetting.setOnClickListener { DefaultPatternSettingActivity.startAction(this@DefaultStyleActivity) }
        btnChecking.setOnClickListener { DefaultPatternCheckingActivity.startAction(this@DefaultStyleActivity) }
    }

    companion object {

        fun startAction(context: Context) {
            val intent = Intent(context, DefaultStyleActivity::class.java)
            context.startActivity(intent)
        }
    }
}
