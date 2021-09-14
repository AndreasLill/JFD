package com.andlill.jld.io.xml

object XmlModel {

    // Only used for XML processing
    class Kanji {
        var value : String = ""
        val info : ArrayList<String> = ArrayList()
        val priority : ArrayList<String> = ArrayList()
    }

    // Only used for XML processing
    class Kana {
        var value : String = ""
        var noKanji : Boolean = false
        val info : ArrayList<String> = ArrayList()
        val priority : ArrayList<String> = ArrayList()
        val restriction : ArrayList<String> = ArrayList()
    }
}