package com.andlill.jld.io.xml

import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.model.Kanji
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.lang.Exception

object XmlManager {

    fun parseJMdict(stream: InputStream) : HashMap<Int, DictionaryEntry> {

        val hashMap = HashMap<Int, DictionaryEntry>()
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)
        XmlReplacement.setEntityReplacementText(parser)

        var text = ""
        var event = parser.eventType
        var entry = DictionaryEntry()
        var kanji = XmlModel.Kanji()
        var kana = XmlModel.Kana()
        var sense = DictionaryEntry.Sense()
        val kanjiList = ArrayList<XmlModel.Kanji>()
        val kanaList = ArrayList<XmlModel.Kana>()

        while (event != XmlPullParser.END_DOCUMENT) {
            val tag = parser.name
            when (event) {
                XmlPullParser.START_TAG -> when (tag) {
                    "entry" -> entry = DictionaryEntry()
                    "k_ele" -> kanji = XmlModel.Kanji()
                    "r_ele" -> kana = XmlModel.Kana()
                    "sense" -> sense = DictionaryEntry.Sense()
                }
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> when (tag) {
                    "ent_seq" -> entry.id = Integer.parseInt(text)
                    // Kanji
                    "keb" -> kanji.value = text
                    "ke_inf" -> kanji.info.add(text)
                    "ke_pri" -> kanji.priority.add(text)
                    "k_ele" -> kanjiList.add(kanji)
                    // Kana
                    "reb" -> kana.value = text
                    "re_nokanji" -> kana.noKanji = true
                    "re_restr" -> kana.restriction.add(text)
                    "re_inf" -> kana.info.add(text)
                    "re_pri" -> kana.priority.add(text).also {
                        if (text.startsWith("nf"))
                            entry.updateCommonScore(Integer.valueOf(text.replace("nf", "")))
                        else if ((text == "spec1" || text == "ichi1"))
                            entry.updateCommonScore(24)
                    }
                    "r_ele" -> kanaList.add(kana)
                    // Sense
                    "pos" -> sense.partOfSpeech.add(XmlReplacement.getValue(text))
                    "field" -> sense.field.add(text)
                    "misc" -> sense.misc.add(text)
                    "s_inf" -> sense.usage.add(text)
                    "dial" -> sense.dialect.add(text)
                    "gloss" -> sense.glossary.add(text)
                    "sense" -> entry.sense.add(sense)
                    // Add entry to HashMap via id.
                    "entry" -> {
                        entry.updateCommonScore(50)
                        entry.reading.addAll(createReadings(kanjiList, kanaList))
                        hashMap[entry.id] = entry
                        kanjiList.clear()
                        kanaList.clear()
                    }
                }
            }
            event = parser.next()
        }

        return hashMap
    }

    fun parseKanji(stream: InputStream): HashMap<String, Kanji> {

        val hashMap = HashMap<String, Kanji>()
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)

        var text = ""
        var attribute = ""
        var event = parser.eventType
        var entry = Kanji()

        while (event != XmlPullParser.END_DOCUMENT) {
            val tag = parser.name
            val attr = when {
                parser.attributeCount > 0 -> parser.getAttributeValue(0)
                else -> ""
            }
            when (event) {
                XmlPullParser.START_TAG -> when (tag) {
                    "character" -> entry = Kanji()
                    "reading" -> if (attr.isNotEmpty()) { attribute = attr }
                    "meaning" -> if (attr.isNotEmpty()) { attribute = attr }
                }
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> when (tag) {
                    "literal" -> entry.character = text
                    "grade" -> entry.grade = Integer.parseInt(text)
                    "stroke_count" -> entry.stroke = Integer.parseInt(text)
                    "freq" -> entry.freq = Integer.parseInt(text)
                    "jlpt" -> entry.jlpt = Integer.parseInt(text)
                    "reading" -> if (attribute == "ja_on" || attribute == "ja_kun") {
                        val reading = Kanji.Reading()
                        reading.value = text
                        reading.type = attribute.replace("ja_", "")
                        entry.reading.add(reading).also { attribute = "" }
                    }
                    "meaning" -> if (attribute.isEmpty()) {
                        entry.meaning.add(text).also { attribute = "" }
                    }
                    "character" -> hashMap[entry.character] = entry
                }
            }
            event = parser.next()
        }

        return hashMap
    }

    private fun createReadings(kanjiList: List<XmlModel.Kanji>, kanaList: List<XmlModel.Kana>): ArrayList<DictionaryEntry.Reading> {
        val readings = ArrayList<DictionaryEntry.Reading>()

        for (kana in kanaList) {
            if (kana.noKanji || kanjiList.isEmpty()) {
                // Create an empty kanji reading if kana has no kanji, or kanji list is empty.
                val reading = DictionaryEntry.Reading()
                reading.kana = kana.value
                reading.kanji = ""
                reading.info = kana.info
                reading.priority = kana.priority
                readings.add(reading)
            }
            else {
                if (kana.restriction.isNotEmpty()) {
                    // Add reading to kana by kanji restrictions.
                    for (kanji in kanjiList) {
                        if (kana.restriction.contains(kanji.value)) {
                            val reading = DictionaryEntry.Reading()
                            reading.kana = kana.value
                            reading.kanji = kanji.value
                            reading.info = kana.info
                            reading.priority = kana.priority
                            readings.add(reading)
                        }
                    }
                }
                else {
                    // Add all kanji readings to kana.
                    for (kanji in kanjiList) {
                        val reading = DictionaryEntry.Reading()
                        reading.kana = kana.value
                        reading.kanji = kanji.value
                        reading.info = kana.info
                        reading.priority = kana.priority
                        readings.add(reading)
                    }
                }
            }
        }

        if (readings.isEmpty())
            throw Exception("Readings should never be zero.")

        return readings
    }
}