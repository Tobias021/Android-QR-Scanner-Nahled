package cz.aurinet.tisk_kodu_cnc.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.androidnetworking.AndroidNetworking
import cz.aurinet.hacz.R
import cz.aurinet.odvadeni_cnc.ProcessMaterialPropertySingleton
import cz.aurinet.tisk_kodu_cnc.ui.call_enums.CodePrintUICalls
import cz.aurinet.tisk_kodu_cnc.ui.view_models.CodePrintViewModel

/*
 * Závěrečná aktivita pro uzavření procedury.
 */
class CodePrintActivity : AppCompatActivity(), View.OnClickListener {
    //Views
    private lateinit var txtProcedureName: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var btnYes: Button
    private lateinit var btnNo: Button
    private lateinit var btnStatus: Button
    //Variables
    private var isClosed = false
    //Utils
    private lateinit var mPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var singleton: ProcessMaterialPropertySingleton
    private lateinit var viewModel: CodePrintViewModel
    private lateinit var nextIntent: Intent


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_tisk_kodu_cnc)
        AndroidNetworking.initialize(applicationContext)
        //inicializuj utility
        mPlayer = MediaPlayer.create(this@CodePrintActivity, R.raw.beep_err)
        handler = Handler.createAsync(Looper.myLooper()!!)
        nextIntent = Intent(this@CodePrintActivity, TiskPrikazActivity::class.java)
        viewModel = ViewModelProvider(this)[CodePrintViewModel::class.java]
        //sleduj state ve ViewModelu a zachyť UICally
        viewModel.state.observe(this) {
            when (it) {
                CodePrintUICalls.ERR_KOD -> {
                    error("Chybný kód")
                }
                CodePrintUICalls.ERR_PRIKAZ_NEEXISTUJE -> {
                    error("Neexistující výrobní příkaz")
                }
                CodePrintUICalls.ERR_SERVER -> {
                    error("Chyba komunikace se severem")
                }
                CodePrintUICalls.ERR_PRIKAZ_UZAVREN -> {
                    error("Výrobní příkaz je již ukončen")
                }
                CodePrintUICalls.CALLBACK_OK -> {
                    isClosed = true
                    closed()
                }
                CodePrintUICalls.TO_DEFAULT -> toDefault()
                CodePrintUICalls.ERR_OBECNY -> error("Nastala nespecifikovaná chyba")
                else -> error("Neznámá chyba")
            }
        }
        initViews()
        showSwitchButtons()
    }

    /*
     * Inicializace a počáteční nastavení UI komponent
     */
    private fun initViews() {
        //načti pohledy
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)
        btnStatus = findViewById(R.id.btnStatus)
        singleton = ProcessMaterialPropertySingleton
        txtProcedureName = findViewById(R.id.txtProcedureNameClose)
        txtQuestion = findViewById(R.id.txtQuestion)
        //inicializuj pohledy
        txtProcedureName.text = singleton.procedureName
        txtQuestion.text = "Chcete vytisknout etikety na VP?"
        btnYes.setOnClickListener(this)
        btnYes.isEnabled = true
        btnNo.setOnClickListener(this)
        btnNo.isEnabled = true
        btnStatus.isEnabled = false
    }

    /*
     * Metoda poslouchá stitsk tlačítke pro odeslání čárového kódu do API nebo zrušení akce a přechod do další aktivity.
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnNo -> {
                startActivity(nextIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION))
            }
            R.id.btnYes -> {
                hideButtons()
                viewModel.callApi()
            }
            R.id.btnStatus -> {
                if(isClosed){
                    startActivity(nextIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION))
                }else{
                    toDefault()
                }
            }
        }
    }

    private fun hideButtons() {
        btnYes.visibility = View.INVISIBLE
        btnNo.visibility = View.INVISIBLE
        btnStatus.visibility = View.INVISIBLE
    }

    private fun error(text: String){
        handler.removeCallbacksAndMessages(null)
        btnStatus.setBackgroundColor(Color.RED)
        mPlayer.start()
        showStatusButton(text)
    }

    private fun closed(){
        handler.removeCallbacksAndMessages(null)
        btnStatus.setBackgroundColor(Color.GREEN)
        showStatusButton("Příkaz byl odveden")
    }

    private fun toDefault(){
        hideButtons()
        showSwitchButtons()
    }

    private fun showSwitchButtons(){
        hideButtons()
        btnYes.isVisible = true
        btnNo.isVisible = true
    }

    private fun showStatusButton(text: String) {
        hideButtons()
        btnStatus.text = text
        btnStatus.visibility = View.VISIBLE
        if(isClosed){
            handler.postDelayed(
                {startActivity(nextIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION))},
                singleton.DELAY_MS.toLong())
        }else{
            handler.postDelayed(this::toDefault, singleton.DELAY_MS.toLong())
        }
    }
}
