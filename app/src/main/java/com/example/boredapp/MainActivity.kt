package com.example.boredapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.boredapp.databinding.ActivityMainBinding
import okhttp3.Headers
import org.json.JSONException
import java.io.IOError

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater) // activity_main.xml -> ActivityMainBinding
        val view = binding.root // layout of activity is stored in a special property called root
        setContentView(view)
        getBoredContent() // initial screen
        getNextBoredContent(binding.boredButton) // button listener
        getBoredContentType(binding.boredTypeButton) // button listener
    }

    private fun getBoredContentType(button: Button) {
        button.setOnClickListener {
            getTypeBoredContent(binding.boredType.text.toString())
        }
    }

    private fun getNextBoredContent(button: Button) {
        button.setOnClickListener {
            getBoredContent()
        }
    }

    private fun getBoredContent() {
        val client = AsyncHttpClient()
        client["https://www.boredapi.com/api/activity/", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("bored", "response successful$json")
                binding.boredTitle.text = "I'm bored, well:\n" + json.jsonObject.getString("activity")
                binding.boredInfo1.text = "Type: " + json.jsonObject.getString("type")
                binding.boredInfo2.text = "Accessibility Rate: " + json.jsonObject.getString("accessibility")
                Log.d("bored", "bored content set")
            }
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("bored error", errorResponse)
            }
        }]
    }

    private fun getTypeBoredContent(type: String) {
        val client = AsyncHttpClient()
        client["https://www.boredapi.com/api/activity?type=$type", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("bored", "response successful$json")
                if (type.isEmpty() || json.jsonObject.has("error")) {
                    Handler(Looper.getMainLooper()).post {
                        val toast = Toast.makeText(applicationContext, "Invalid type", Toast.LENGTH_SHORT) // in Activity
                        toast.show()
                    }
                }
                else {
                    binding.boredTitle.text = "I'm bored, well:\n" + json.jsonObject.getString("activity")
                    binding.boredInfo1.text = "Type: " + json.jsonObject.getString("type")
                    binding.boredInfo2.text = "Accessibility Rate: " + json.jsonObject.getString("accessibility")
                    Log.d("bored", "bored content set")
                }
            }
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("bored error", errorResponse)
                Handler(Looper.getMainLooper()).post {
                    val toast = Toast.makeText(applicationContext, "Invalid type", Toast.LENGTH_SHORT) // in Activity
                    toast.show()
                }

            }
        }]
    }
}