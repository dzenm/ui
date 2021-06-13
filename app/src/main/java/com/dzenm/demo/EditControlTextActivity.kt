package com.dzenm.demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_control_text.*

class EditControlTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_control_text)

        btn_login.setOnClickListener(View.OnClickListener {
            if (edit_layout.verify()) {
                Toast.makeText(this, "校验成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "校验失败", Toast.LENGTH_SHORT).show()
            }
        })
    }
}