package cz.aurinet.hacz

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

private class WebViewClientLocal: WebViewClient() {
    override fun shouldOverrideUrlLoading(view:WebView, URL:String): Boolean {
        view.loadUrl(URL)
        return false
    }
}

class WebActivity: AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var url: String? = null
        val wvClient: WebViewClient = WebViewClientLocal()
        val intent = intent
        if (intent.hasExtra("URL")) {
            url = intent.getCharSequenceExtra("URL").toString()
        }
        setContentView(R.layout.activity_webactivity)
        val web = findViewById<View>(R.id.WebVydejKompletace) as WebView
        //webview client pro otevírání linů v tom samém WV
        web.webViewClient = object: WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return false
            }
        }
        //web.setWebContentsDebuggingEnabled(false);
        //val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.hideSoftInputFromWindow(web.windowToken, 0)
        web.webViewClient = wvClient
        web.settings.javaScriptEnabled = true
        web.loadUrl(url!!)
        web.setBackgroundColor(Color.argb(1, 0, 0, 0))
        web.requestFocus()
    }
}

