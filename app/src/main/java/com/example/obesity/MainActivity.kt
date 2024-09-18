package com.example.obesity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")
    lateinit var editTextAge: EditText
    lateinit var editTextGender: EditText
    lateinit var editTextHeight: EditText
    lateinit var editTextWeight: EditText
    lateinit var editTextBMI: EditText
    lateinit var editTextPhysicalActivity: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //To run network operations on a main thread or as an synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        editTextAge = findViewById(R.id.editTextAge)
        editTextGender = findViewById(R.id.editTextGender)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextBMI = findViewById(R.id.editTextBMI)
        editTextPhysicalActivity = findViewById(R.id.editTextPhysicalActivity)
        val btnPredict = findViewById<Button>(R.id.btnPredict)

        btnPredict.setOnClickListener {
            val url: String = getString(R.string.root_url) // ใส่ URL ของเซิร์ฟเวอร์ที่ต้องการ

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("age", editTextAge.text.toString())
                .add("gender", editTextGender.text.toString())
                .add("height", editTextHeight.text.toString())
                .add("weight", editTextWeight.text.toString())
                .add("bmi", editTextBMI.text.toString())
                .add("physical_activity", editTextPhysicalActivity.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val riskLevel = data.getInt("obesity_risk")
                    var value = ""
                    if (riskLevel == 0){
                        value = "Normal weight"
                    }else if (riskLevel == 1){
                        value = "Obese"
                    }else if (riskLevel == 2){
                        value = "Overweight"
                    }else if (riskLevel == 3){
                        value = "Underweight"
                    }
                    val message = "ระดับความเสี่ยงโรคอ้วนของคุณคือ: $value"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ผลการทำนายความเสี่ยงโรคอ้วน")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()
                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            editTextAge.text.clear()
            editTextGender.text.clear()
            editTextHeight.text.clear()
            editTextWeight.text.clear()
            editTextBMI.text.clear()
            editTextPhysicalActivity.text.clear()
            editTextAge.requestFocus()
        }
    }
}
