package cz.aurinet.hacz

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class MenuActivity : AppCompatActivity() {
    var sqlGoblin: SqlGoblin? = null
    var localIntents: LocalIntents? = null
    var statement: Statement? = null
    var button1 //BUTTON1 - dole vpravo
            : Button? = null
    var button2 //BUTTON2 - dole vlevo
            : Button? = null
    var button3 //BUTTON3 - stred vpravo
            : Button? = null
    var button4 //BUTTON4 - stred vlevo
            : Button? = null
    var button5 //BUTTON5 - nahore vpravo
            : Button? = null
    var button6 //BUTTON6 - nahore vlevo
            : Button? = null
    var button: Array<Button?> = arrayOfNulls(6)
    var stavovyRadek: TextView? = null
    var kolecko: ProgressBar? = null
    var showBtn = BooleanArray(6)
    private var androidID: String? = null
    private lateinit var appVersion: String
    private val scope = MainScope()


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        sqlGoblin = SqlGoblin()
        // získání android id ze zařízení
        androidID = Secure.getString(this.contentResolver, Secure.ANDROID_ID)
        kolecko = findViewById(R.id.progressBar)
        kolecko!!.visibility = View.VISIBLE
        stavovyRadek = findViewById(R.id.stavovyRadek)
        button1 = findViewById(R.id.buttonPrijemVaku)
        button2 = findViewById(R.id.buttonPrijemPolotovaru)
        button3 = findViewById(R.id.buttonDilna)
        button4 = findViewById(R.id.buttonVydejKompletace)
        button5 = findViewById(R.id.buttonExpedovatPaletu)
        button6 = findViewById(R.id.buttonVyloha)
        button = arrayOf(button1, button2, button3, button4, button5, button6)
        localIntents = LocalIntents(this)
/* #TODO - za názvem nechává unspecified

        //získej verzi aplikace
        val packageManager = this.packageManager
        appVersion = BuildConfig.VERSION_NAME

        //zadej verzi aplikace do text. pole v menu
        val txtVersion = findViewById<TextView>(R.id.txt_verze)
        txtVersion.text = appVersion
*/
        scope.launch { updateButtonLayout() }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    suspend fun updateButtonLayout() = coroutineScope {
        withContext(Dispatchers.IO) {
            try {
                // hodnota pro kontrolu záznamu; při inicializaci 99 - pokud se nezmění, vyhodí chybovou hlášku
                var jeZaznam = 99
                val rs =
                    sqlGoblin!!.getResult("select count(*) as pocet from dbo.Terminal_icons as ic where terminal_no = '$androidID'")
                runOnUiThread { kolecko!!.visibility = View.VISIBLE }

                while (rs.next()) {
                    jeZaznam = rs.getString("pocet").toInt()
                }
                // Pokud je záznam v databázi, proveď následující
                if (jeZaznam == 1) {
                    val list = ArrayList<Int>()
                    val rsIco =
                        sqlGoblin!!.getResult("select icon1, icon2, icon3, icon4, icon5, icon6 from dbo.Terminal_icons as ic where terminal_no = '$androidID'")

                    while (rsIco.next()) {
                        for (i in 1..6) {
                            list.add(rsIco.getString("icon$i").toInt())
                        }
                    }
                    if (list[0] + list[1] + list[2] + list[3] + list[4] + list[5] > 0) {
                        val buttonSet: ResultSet = sqlGoblin!!.getResult(
                            "select " +
                                    "CASE WHEN ico.icon1 > 0 THEN 'true' ELSE 'false' END as state1, " +
                                    "CASE WHEN ico.icon2 > 0 THEN 'true' ELSE 'false' END as state2, " +
                                    "CASE WHEN ico.icon3 > 0 THEN 'true' ELSE 'false' END as state3, " +
                                    "CASE WHEN ico.icon4 > 0 THEN 'true' ELSE 'false' END as state4, " +
                                    "CASE WHEN ico.icon5 > 0 THEN 'true' ELSE 'false' END as state5, " +
                                    "CASE WHEN ico.icon6 > 0 THEN 'true' ELSE 'false' END as state6, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon1) as name1, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon2) as name2, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon3) as name3, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon4) as name4, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon5) as name5, " +
                                    "(select name from dbo.Terminal_icset as ics where ics.icon_no = ico.icon6) as name6, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon1) as url1, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon2) as url2, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon3) as url3, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon4) as url4, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon5) as url5, " +
                                    "(select address from dbo.Terminal_icset as ics where ics.icon_no = ico.icon6) as url6 " +
                                    "from dbo.Terminal_icons as ico where terminal_no = '" + androidID + "'"
                        )
                        buttonSet.next()
                        runOnUiThread {
                            Log.i(
                                "DATABASE",
                                buttonSet.getString("name1") + buttonSet.getString("name2") + buttonSet.getString(
                                    "name3"
                                ) + buttonSet.getString("name4") + buttonSet.getString("name5") + buttonSet.getString(
                                    "name6"
                                ) + "1" + buttonSet.getString("state1") + buttonSet.getString("state2") + buttonSet.getString(
                                    "state3"
                                ) + buttonSet.getString("state4") + buttonSet.getString("state5") + buttonSet.getString(
                                    "state6"
                                )
                            )
                        }

                        for ((index, value) in button.withIndex()) {
                            runOnUiThread {
                                value!!.text = buttonSet.getString("name${index + 1}")
                                showBtn[index] = buttonSet.getBoolean("state${index + 1}")
                                if (buttonSet.getBoolean("state${index + 1}")) {
                                    button[index]?.setOnClickListener {
                                        startActivity(localIntents!!.resolve(buttonSet.getString("url${index + 1}")))
                                    }
                                }
                            }
                        }
                    } else {
                        runOnUiThread { stavovyRadek!!.text = getString(R.string.Neregistrovano_error) + androidID }
                    }
                } else if (jeZaznam == 0) {
                    runOnUiThread { stavovyRadek!!.text = getString(R.string.Neregistrovano_error) + androidID }
                    sqlGoblin!!.update("insert into dbo.Terminal_icons (terminal_no, icon1, icon2, icon3, icon4, icon5, icon6) values ('$androidID', 0,0,0,0,0,0)")
                } else {
                   runOnUiThread {  stavovyRadek!!.setText(R.string.Necekana_chyba) }
                }
            } catch (throwables: SQLException) {
                throwables.printStackTrace()
                Log.e("ERRO", throwables.message!!)
                runOnUiThread { stavovyRadek!!.setText(R.string.Necekana_chyba) }
            } catch (e: Exception) {
                e.message?.let { Log.e("ERRO", it) }
                runOnUiThread { stavovyRadek!!.text = getString(R.string.Offline_error) + androidID }
            } finally {
                runOnUiThread { kolecko!!.visibility = View.GONE }
            }
            runOnUiThread {updateVisibility()}
        }
    }

    // metody pro "onClick" v XML
    fun updateVisibility() {
        for (b in button.withIndex()){
            if (showBtn[b.index]) {
                button[b.index]?.visibility = View.VISIBLE
            }else{
                button[b.index]?.visibility = View.GONE
            }
            Log.i("VISIBILITY", "At index ${b.index} visibility: ${showBtn[b.index]}")
        }
    }
}