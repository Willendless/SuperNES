package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import android.widget.Toast
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    companion object {
        private val acceptableURLs = mapOf(
            Pair("https://www.nesworld.com/", "NES WORLD"),
            Pair("https://nesdev.com/", "NESDEV")
        )

        fun actionStart(context: Context, url: String) {
            if (url !in acceptableURLs) {
                Toast.makeText(context, "Unknown url", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // supportaction bar
        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }

        // web
        val url = intent.getStringExtra("url")!!
        web_view_title.text = WebViewActivity.acceptableURLs[url]
        web_view.settings.displayZoomControls = true
        web_view.webViewClient = WebViewClient()
        web_view.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}