package com.sbsj.dreamwing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.sbsj.dreamwing.volunteer.VolunteerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.openVolunteerActivityButton)
        button.setOnClickListener {
            val intent = Intent(this, VolunteerActivity::class.java)
            startActivity(intent)
        }
    }
}
