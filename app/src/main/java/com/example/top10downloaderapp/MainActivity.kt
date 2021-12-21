package com.example.top10downloaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var appRecyclerView: RecyclerView
    private  var appsList = ArrayList<TopApp>()
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appRecyclerView = findViewById(R.id.recyclerView)
        recyclerViewAdapter = RecyclerViewAdapter(appsList)
        appRecyclerView.adapter = recyclerViewAdapter
        appRecyclerView.layoutManager = LinearLayoutManager(this)

        parseRRS()
    }

    private fun parseRRS() {
        CoroutineScope(IO).launch {
            async {
                getDataXML()
            }.await()

            withContext(Main) {

                recyclerViewAdapter = RecyclerViewAdapter(appsList)
                appRecyclerView.adapter!!.notifyDataSetChanged()
                Log.d("data", "get data Successfully${appsList}")

            }
        }
    }

    fun getDataXML() {
        var text = ""
        var appTitle = ""
        var appImage = ""
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            val url =
                URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
            parser.setInput(url.openStream(), null)
            var evenType = parser.eventType
            while (evenType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (evenType) {
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when {
                        tagName.equals("im:name", true) -> {
                            appTitle = text

                            Log.d("GetData", "getData: ${appTitle}")
                        }
                        tagName.equals("im:image", true) -> {

                                appImage = text
                              //  appsList.add(TopApp(appTitle, appImage))
                            if (appImage[appImage.length - 8] == '0') {
                                appsList.add(TopApp(appTitle, appImage))
                           }
                                Log.d("GetData", "getData: ${appImage}")
                        }
                        else -> {}
                    }
                    else -> {}
                }
                evenType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


