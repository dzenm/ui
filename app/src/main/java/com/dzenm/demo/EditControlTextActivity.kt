package com.dzenm.demo

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dzenm.input_text.EditTextLayout

class EditControlTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_control_text)

        val loginBtn = findViewById<Button>(R.id.btn_login)
        val editLayout = findViewById<EditTextLayout>(R.id.edit_layout)
        loginBtn.setOnClickListener {
            if (editLayout.verify()) {
                Toast.makeText(this, "校验成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "校验失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}