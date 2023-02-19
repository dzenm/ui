package com.dzenm.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.badge_drawable_button).setOnClickListener {
            startActivity(Intent(this, BadgeDrawableActivity::class.java))
        }
        findViewById<Button>(R.id.dialog_button).setOnClickListener {
            startActivity(Intent(this, DialogActivity::class.java))
        }
        findViewById<Button>(R.id.fish_button).setOnClickListener {
            startActivity(Intent(this, FishActivity::class.java))
        }
        findViewById<Button>(R.id.edit_text_button).setOnClickListener {
            startActivity(Intent(this, EditControlTextActivity::class.java))
        }
        findViewById<Button>(R.id.progress_view_button).setOnClickListener {
            startActivity(Intent(this, ProgressViewActivity::class.java))
        }
        findViewById<Button>(R.id.switch_button).setOnClickListener {
            startActivity(Intent(this, SwitchButtonActivity::class.java))
        }

    }
}