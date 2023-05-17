package com.github.ihsg.demo.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.ihsg.demo.R
import com.github.ihsg.demo.ui.def.DefaultStyleActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import androidx.appcompat.app.ActionBar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //var bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img_no_avatar)
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

        btnSimple.setOnClickListener { DefaultStyleActivity.startAction(this@MainActivity) }
    }
}