package cz.aurinet.hacz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cz.aurinet.odvadeni_cnc.ui.OdvadeniPrikazActivity
import cz.aurinet.processmaterial_dim.ui.ProcessMaterialActivity
import cz.aurinet.tisk_kodu_cnc.ui.TiskPrikazActivity

class LocalIntents(private val context: Context): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }
    //Pracuje s URL, kterou dodává databáze držící konfiguraci tlačítek pro určité zařízení
    @RequiresApi(Build.VERSION_CODES.R)
    fun resolve(URL: String): Intent {
        return when(URL){
            "local_productinfo" -> getProductInfoIntent()
            "local_processcnc" -> getOdvadeniCncIntent()
            "local_processcnc_print" -> getTiskKoduCncIntent()
            "local_processmaterial_dim" -> getProcessMaterialIntent()
            else -> {
                val webActivityIntent = getWebActivityIntent()
                webActivityIntent.putExtra("URL", URL)
                webActivityIntent
            }
        }
    }
    private fun getProductInfoIntent(): Intent{
       return Intent(context, ProductInfoMain::class.java)
    }
    private fun getWebActivityIntent(): Intent{
        return Intent(context, WebActivity::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getOdvadeniCncIntent(): Intent{
        return Intent(context, OdvadeniPrikazActivity::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getTiskKoduCncIntent(): Intent{
        return Intent(context, TiskPrikazActivity::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getProcessMaterialIntent(): Intent{
        return Intent(context, ProcessMaterialActivity::class.java)
    }
}