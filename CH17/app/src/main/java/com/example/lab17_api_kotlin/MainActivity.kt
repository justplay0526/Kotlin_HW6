package com.example.lab17_api_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button

    class MyObject {
        lateinit var records: Array<Record>
        class Record {
            var sitename = ""
            var status = ""
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        btn_query.setOnClickListener {

            btn_query.isEnabled = false

            sendRequest()
        }
    }

    private fun sendRequest() {

        val url = "https://data.epa.gov.tw/api/v2/aqx_p_488?format=json&offset=0&limit=1000&api_key=3907da74-56c0-4bb9-8637-b45009312b6e&filters=county,EQ,%E8%87%BA%E5%8C%97%E5%B8%82,%E6%96%B0%E5%8C%97%E5%B8%82%7Cdatacreationdate,GT,2022-12-26%2014:00%7Cdatacreationdate,LE,2022-12-26%2015:00&fields=sitename,status&sort=datacreationdate%20desc"

        val req = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                //使用 response.body?.string()取得 JSON 字串
                val json = response.body?.string()
                //建立 Gson 並使用其 fromJson()方法，將 JSON 字串以 MyObject 格式輸出
                val myObject = Gson().fromJson(json, MyObject::class.java)
                //顯示結果
                showDialog(myObject)
            }
            //發送失敗執行此方法
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    Toast.makeText(this@MainActivity,
                        "查詢失敗$e", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    //顯示結果
    private fun showDialog(myObject: MyObject) {
        //建立一個字串陣列，用於存放 SiteName 與 Status 資訊
        val items = arrayOfNulls<String>(myObject.records.size)
        //將 API 資料取出並建立字串，並存放到字串陣列
        myObject.records.forEachIndexed { index, data ->
            items[index] = "地區：${data.sitename}, 狀態：${data.status}"
        }
        //切換到主執行緒將畫面更新
        runOnUiThread {
            //開啟按鈕可再次查詢
            btn_query.isEnabled = true
            //建立 AlertDialog 物件並顯示字串陣列
            AlertDialog.Builder(this)
                .setTitle("雙北空氣品質")
                .setItems(items, null)
                .show()
        }
    }
}