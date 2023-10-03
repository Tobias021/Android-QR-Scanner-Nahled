package cz.aurinet.hacz

import android.util.Log
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

class SqlGoblin {
    private val user = "Zebra1"
    private val pass = "Zebra2021"
    private val database = "HA_TERMINALS"
    private val server = "192.168.40.109"
    var conn: Connection? = null
    private var URL: String? = null

    private suspend fun getConnection(): Connection? { withContext(Dispatchers.IO){
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            URL = ("jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + database + ";user=" + user + ";password="
                    + pass + ";")
            conn = DriverManager.getConnection(URL)
        } catch (e: ClassNotFoundException) {
            Log.e("ERRO", e.message!!)
        } catch (throwables: SQLException) {
            Log.e("ERRO", throwables.message!!)
        }
        return@withContext conn
    }
        return conn
    }

    suspend fun getResult(query: String): ResultSet {
        return work(query)
    }

    @Throws(SQLException::class)
    suspend fun update(update: String) = coroutineScope {
        withContext(Dispatchers.IO) {
            getConnection()?.createStatement()?.executeUpdate(update)
        }

    }

    private suspend fun work(query: String): ResultSet = coroutineScope {
         withContext(Dispatchers.IO) {
            conn = getConnection()
            val statement = conn!!.createStatement()
            val rs = statement!!.executeQuery(query)
            //statement.close()
            //conn?.close()
            return@withContext rs
        }
    }

}

