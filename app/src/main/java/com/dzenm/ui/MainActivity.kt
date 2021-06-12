package com.dzenm.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        badge_drawable_button.setOnClickListener {
            startActivity(Intent(this, BadgeDrawableActivity::class.java))
        }
        dialog_button.setOnClickListener {
            startActivity(Intent(this, DialogActivity::class.java))
        }
        fish_button.setOnClickListener {
            startActivity(Intent(this, FishActivity::class.java))
        }
        edit_text_button.setOnClickListener {
            startActivity(Intent(this, EditControlTextActivity::class.java))
        }
        progress_view_button.setOnClickListener {
            startActivity(Intent(this, ProgressViewActivity::class.java))
        }
        switch_button.setOnClickListener {
            startActivity(Intent(this, SwitchButtonActivity::class.java))
        }

    }
}