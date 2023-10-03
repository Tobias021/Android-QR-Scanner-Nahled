package cz.aurinet.hacz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import net.sourceforge.jtds.jdbc.Driver
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class ProductInfoDBWorker(private val context:Context, private val tableControl: ProductInfoTableControl): AppCompatActivity() {

    private var url: String? = null
    private var normativ: String? = null
    private var props = Properties()

    private fun varInit(){
        url =  "jdbc:jtds:sqlserver://" + context.getString(R.string.db_adress)
        props["user"] = context.getString(R.string.db_user)
        props["password"] = context.getString(R.string.db_password)
        props["TDS"] = "8.0"
        props["loginTimeout"] = 5
        props["databaseName"] = "HA_TERMINALS"
    }
    fun work(input: String){
        val data = arrayOfNulls<String>(5)
        varInit()
        getString(input)
        val driver = Driver()
        val connection: Connection = driver.connect(url, props)
        val statement: Statement = connection.createStatement()
        var nazev:String?
        val rs: ResultSet =  statement.executeQuery("SELECT [id_material],[material_id],[verze_id],[nazev],[photo_id] FROM [HA_TERMINALS].[dbo].[Omater] WHERE id_material = '$normativ'")
        while (rs.next()) {
            data[0] = rs.getString(1).replace("\\s".toRegex(), "") //odstraní mezery...
            data[1] = rs.getString(2).replace("\\s".toRegex(), "")
            data[2] = rs.getString(3).replace("\\s".toRegex(), "")
            nazev = rs.getString(4)
            tableControl.insertName(nazev)
            data[4] = rs.getString(5).replace("\\s".toRegex(), "")
            val statement2: Statement = connection.createStatement()
            val rsa: ResultSet = statement2.executeQuery("SELECT [material_id],[verze_id],[nazev_id],[text_pozn] FROM [HA_TERMINALS].[dbo].[Vkuspozn] WHERE material_id = '${data[1]}' AND verze_id = ${data[2]} ORDER BY nazev_id")
            var i = 0
            var normativ: String? = null
            while(rsa.next()){
                normativ = rsa.getString(1).replace("\\s".toRegex(), "")
                i++
                val rowData = arrayOf(nazev, rsa.getString(2), rsa.getString(3), rsa.getString(4))
                tableControl.createRow(rowData)// data -> [0] = normativ, [1] = počet fotek, [2] = název obrázku, [3] = text1, [4] = text2
            }
            tableControl.createImg(normativ)
        }
        rs.close()
        statement.close()
        connection.close()

        //closeStatement()
        // disconnect()
    }

    private fun getString(input: String){
        normativ = input.subSequence(11,17).toString()
    }
    /**
    private fun executeQuery(stmnt: String): ResultSet?{
    return if(connect() && createStatement()){
    var query: ResultSet? = null
    try {
    query = statement?.executeQuery(stmnt)
    }catch (e:SQLException){
    runOnUiThread(){statBar!!.text = e.message}
    }
    query
    }else{
    null
    }


    }

    private fun connect(): Boolean{
    return try {
    connection = driver?.connect(URL, info)
    true
    }catch(e: SQLException){
    runOnUiThread(){statBar!!.text = e.message}
    false
    }
    }
    private fun createStatement(): Boolean{
    return try {
    if (connection?.isClosed == false) {
    statement = connection?.createStatement()
    true
    } else {
    false
    }
    }catch (e:SQLException){
    runOnUiThread(){statBar!!.text = e.message}
    false
    }
    }
    private fun closeStatement(): Boolean{
    return try {
    statement?.close()
    true
    }catch (e:SQLException){
    runOnUiThread(){statBar!!.text = e.message}
    false
    }
    }

    private fun disconnect(): Boolean{
    return try {
    if(connection?.isClosed == false){
    connection?.close()
    true
    }else{
    true
    }
    }catch(e:SQLException){
    runOnUiThread(){statBar!!.text = e.message}
    false
    }
    }
     **/

}