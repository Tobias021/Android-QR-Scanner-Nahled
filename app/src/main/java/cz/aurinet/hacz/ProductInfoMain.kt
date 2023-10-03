package cz.aurinet.hacz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.lang.IndexOutOfBoundsException
import java.sql.*

class ProductInfoMain : AppCompatActivity() {
    private var statBar: TextView? = null
    private var textBar: EditText? = null
    private var tableCon: ProductInfoTableControl? = null
    private var progressBar: ProgressBar? = null
    private var linearLayout: LinearLayout? = null
    private val scope = MainScope()
    private var dbWorker: ProductInfoDBWorker? = null

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_info_main)
        statBar =  findViewById(R.id.statusBar)
        textBar = findViewById(R.id.txt_pne)
        progressBar = findViewById(R.id.progressBar)
        linearLayout = findViewById(R.id.kontextLinearLayout)
        tableCon = ProductInfoTableControl(this, linearLayout!!, progressBar!!, statBar!!)
        dbWorker = ProductInfoDBWorker(this, tableCon!!)

        // nastaví event listener na textové pole vstupu (on_enter_down)
        textBar!!.setOnKeyListener { _, keyCode, event ->
            when{
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                    statBar!!.text = null // vymaže errory ve status baru
                    picture()
                    textBar!!.requestFocus()
                    return@setOnKeyListener true
                }
                else -> false
            }
        }
        textBar!!.requestFocus()

    }

    private suspend fun init() = withContext(Dispatchers.IO){
        launch{
            try {
                dbWorker!!.work(textBar.toString())
            }catch (e:SQLException){
                runOnUiThread(){ statBar!!.text = getString(R.string.error002)}
                Log.e("work", e.message!!)
            }catch (e: IndexOutOfBoundsException){
                runOnUiThread(){ statBar!!.text = getString(R.string.error003,e.message) }
            }catch (e: java.lang.Exception) {
                runOnUiThread() {statBar!!.text = getString(R.string.error004,e.message) }
                Log.e("OTHER", e.stackTraceToString())
            }finally {  //na konci vymazat text z pole
                runOnUiThread(){

                    textBar!!.text = null
                }
            }
        }
    }


    fun picture() = scope.launch {
        tableCon!!.removeAllViews()
        init()
        textBar!!.requestFocus()
    }
}
