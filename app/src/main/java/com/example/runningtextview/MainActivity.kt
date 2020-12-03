package com.example.runningtextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var runningTextView: RunningTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runningTextView = findViewById(R.id.rtv)
        runningTextView.apply {
            setRunningText("This is a demo running text :)")
            resume()
        }
    }
}