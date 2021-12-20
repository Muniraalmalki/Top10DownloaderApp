package com.example.top10downloaderapp

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.URL

class XmlParser {

    private var appsList = arrayListOf<TopApp>()

    private var text = ""

    private var appTitle = ""
    private var appImage = ""

    fun parse(): ArrayList<TopApp> {
        appsList = arrayListOf()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            val url =
                URL("https://ax.itunes.apple.com/webobjects/mzstoreservices.woa/ws/rss/topfreeapplications/limit=10/xml")
            parser.setInput(url.openStream(), null)
            var evenType = parser.eventType
            while (evenType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (evenType) {
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when {
                        tagName.equals("title", true) -> {
                            appTitle = text
                        }
                        tagName.equals("im:image", true) -> {
                            appImage = text
                            if (appImage[appImage.length - 8] == '0') {
                                appsList.add(TopApp(appTitle, appImage))
                           }
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

        return appsList
    }
}