package cz.aurinet.hacz

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.URL

class ProductInfoTableControl(private var context: Context, private var tabulka: LinearLayout, private var progressbar: ProgressBar, private var statBar: TextView): AppCompatActivity()  {

    private var prog: Int = 0
    //private val tableIntent = Intent(context, ImageDetail::class.java)

    fun insertName(str: String?){
        val name = TextView(context)
        name.gravity = 1
        name.textSize = 28.0F
        name.text = str
        runOnUiThread(){
            tabulka.addView(name)
        }
    }

    fun createRow(data: Array<String?>) { // data -> [0] = normativ, [1] = počet fotek, [2] = název obrázku, [3] = text1, [4] = text2

        val normativ = data[0]
        val name = TextView(context)
        val text1 = TextView(context)
        val text2 = TextView(context)

        name.gravity = 0
        name.textSize = 25.0F
        text1.textSize = 22.0F
        text1.setTypeface(Typeface.DEFAULT_BOLD)
        text2.textSize = 18.0F



        text1.text = "\n${data[2]?.trim() ?: "-"}"
        text2.text = data[3]?.trim() ?: "-"
        runOnUiThread() {
            tabulka.addView(text1)
            tabulka.addView(text2)

            Log.d("WW", "Created a row!!")
        }
    }
        fun createImg(data: String?) {

            val normativ = data
            val draw = ImageView(context)

            draw.adjustViewBounds = true
            draw.isClickable = true

            val inS: InputStream =
                URL("http://feed.hilding.cz/pictures/$normativ-1.jpg").content as InputStream
            val d: Drawable = Drawable.createFromStream(inS, "web")
            draw.setImageDrawable(d)



            runOnUiThread() {
                tabulka.addView(draw)
                Log.d("WW", "Created a row!!")
            }

            /** runOnUiThread( Runnable(){
            tabulka.setHorizontalGravity(1)
            })**/

            progressbar.progress = 0
        }

        fun removeAllViews() {
            runOnUiThread() {
                tabulka.removeAllViews()
            }
        }

        /**public fun addLayoutView(message: String){

        }
         **/

}