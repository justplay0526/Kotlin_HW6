package com.example.ch16_master

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbrw = MyDBHelper(this).writableDatabase

        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        setListener()
    }
    override fun onDestroy() {
        dbrw.close()
        super.onDestroy()
    }

    private fun setListener() {
        val ed_book = findViewById<EditText>(R.id.ed_book)
        val ed_price = findViewById<EditText>(R.id.ed_price)
        findViewById<Button>(R.id.btn_insert).setOnClickListener {

            if (ed_book.length() < 1 || ed_price.length() < 1)
                showToast("欄位請勿留空")
            else
                try {

                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?,?)",
                        arrayOf(ed_book.text.toString(),
                            ed_price.text.toString())
                    )
                    showToast("新增:${ed_book.text},價格:${ed_price.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("新增失敗:$e")
                }
        }
        findViewById<Button>(R.id.btn_update).setOnClickListener {

            if (ed_book.length() < 1 || ed_price.length() < 1)
                showToast("欄位請勿留空")
            else
                try {

                    dbrw.execSQL("UPDATE myTable SET price = ${ed_price.text} WHERE book LIKE '${ed_book.text}'")
                    showToast("更新:${ed_book.text},價格:${ed_price.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("更新失敗:$e")
                }
        }
        findViewById<Button>(R.id.btn_delete).setOnClickListener {

            if (ed_book.length() < 1)
                showToast("書名請勿留空")
            else
                try {

                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '${ed_book.text}'")
                    showToast("刪除:${ed_book.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("刪除失敗:$e")
                }
        }
        findViewById<Button>(R.id.btn_query).setOnClickListener {

            val queryString = if (ed_book.length() < 1)
                "SELECT * FROM myTable"
            else
                "SELECT * FROM myTable WHERE book LIKE '${ed_book.text}'"
            val c = dbrw.rawQuery(queryString, null)
            c.moveToFirst()
            items.clear()
            showToast("共有${c.count}筆資料")
            for (i in 0 until c.count) {

                items.add("書名:${c.getString(0)}\t\t\t\t 價格:${c.getInt(1)}")
                c.moveToNext()
            }
            adapter.notifyDataSetChanged()
            c.close()
        }
    }

    private fun showToast(text: String) =
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()

    private fun cleanEditText() {
        findViewById<EditText>(R.id.ed_book).setText("")
        findViewById<EditText>(R.id.ed_price).setText("")
    }
}