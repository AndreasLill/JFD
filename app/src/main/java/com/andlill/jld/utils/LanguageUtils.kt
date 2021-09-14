package com.andlill.jld.utils

object LanguageUtils {

    private val kana = listOf(
            "kya|きゃ|キャ",
            "kyu|きゅ|キュ",
            "kyo|きょ|キョ",

            "sha|しゃ|シャ",
            "shu|しゅ|シュ",
            "sho|しょ|ショ",

            "cha|ちゃ|チャ",
            "chu|ちゅ|チュ",
            "cho|ちょ|チョ",

            "nya|にゃ|ニャ",
            "nyu|にゅ|ニュ",
            "nyo|にょ|ニョ",

            "hya|ひゃ|ヒャ",
            "hyu|ひゅ|ヒュ",
            "hyo|ひょ|ヒョ",

            "mya|みゃ|ミャ",
            "myu|みゅ|ミュ",
            "myo|みょ|ミョ",

            "rya|りゃ|リャ",
            "ryu|りゅ|リュ",
            "ryo|りょ|リョ",

            "gya|ぎゃ|ギャ",
            "gyu|ぎゅ|ギュ",
            "gyo|ぎょ|ギョ",

            "bya|びゃ|ビャ",
            "byu|びゅ|ビュ",
            "byo|びょ|ビョ",

            "pya|ぴゃ|ピャ",
            "pyu|ぴゅ|ピュ",
            "pyo|ぴょ|ピョ",

            "ja|じゃ|ジャ",
            "ju|じゅ|ジュ",
            "jo|じょ|ジョ",

            "ka|か|カ",
            "ki|き|キ",
            "ku|く|ク",
            "ke|け|ケ",
            "ko|こ|コ",

            "sa|さ|サ",
            "shi|し|シ",
            "su|す|ス",
            "se|せ|セ",
            "so|そ|ソ",

            "ta|た|タ",
            "chi|ち|チ",
            "tsu|つ|ツ",
            "te|て|テ",
            "to|と|ト",

            "na|な|ナ",
            "ni|に|ニ",
            "nu|ぬ|ヌ",
            "ne|ね|ネ",
            "no|の|ノ",

            "ha|は|ハ",
            "hi|ひ|ヒ",
            "fu|ふ|フ",
            "he|へ|ヘ",
            "ho|ほ|ホ",

            "ma|ま|マ",
            "mi|み|ミ",
            "mu|む|ム",
            "me|め|メ",
            "mo|も|モ",

            "ya|や|ヤ",
            "yu|ゆ|ユ",
            "yo|よ|ヨ",

            "ra|ら|ラ",
            "ri|り|リ",
            "ru|る|ル",
            "re|れ|レ",
            "ro|ろ|ロ",

            "wa|わ|ワ",
            "wo|を|ヲ",

            "ga|が|ガ",
            "gi|ぎ|ギ",
            "gu|ぐ|グ",
            "ge|げ|ゲ",
            "go|ご|ゴ",

            "za|ざ|ザ",
            "ji|じ|ジ",
            "zu|ず|ズ",
            "ze|ぜ|ゼ",
            "zo|ぞ|ゾ",

            "da|だ|ダ",
            "de|で|デ",
            "do|ど|ド",

            "ba|ば|バ",
            "bi|び|ビ",
            "bu|ぶ|ブ",
            "be|べ|ベ",
            "bo|ぼ|ボ",

            "pa|ぱ|パ",
            "pi|ぴ|ピ",
            "pu|ぷ|プ",
            "pe|ぺ|ペ",
            "po|ぽ|ポ",

            "a|あ|ア",
            "i|い|イ",
            "u|う|ウ",
            "e|え|エ",
            "o|お|オ",
            "n|ん|ン",
    )

    fun Char.isKanji(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    }

    fun Char.isKana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.HIRAGANA || Character.UnicodeBlock.of(this) == Character.UnicodeBlock.KATAKANA
    }

    fun Char.isHiragana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.HIRAGANA
    }

    fun Char.isKatakana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.KATAKANA
    }

    fun String.isRomaji(): Boolean {
        this.forEach {
            if (it.isKanji() || it.isKana())
                return false
        }
        return true
    }

    fun String.isKana(): Boolean {
        this.forEach {
            if (!it.isKana())
                return false
        }
        return true
    }

    fun String.isHiragana(): Boolean {
        this.forEach {
            if (!it.isHiragana())
                return false
        }
        return true
    }

    fun String.isKatakana(): Boolean {
        this.forEach {
            if (!it.isKatakana())
                return false
        }
        return true
    }

    fun String.containsKanji(): Boolean {
        this.forEach {
            if (it.isKanji())
                return true
        }
        return false
    }

    private fun romajiToHiragana(str: String): String {
        var value: String = str
        kana.forEach {
            val data = it.split("|")
            val src = data[0]
            val dest = data[1]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun romajiToKatakana(str: String): String {
        var value: String = str
        kana.forEach {
            val data = it.split("|")
            val src = data[0]
            val dest = data[2]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun hiraganaToKatakana(str: String): String {
        var value: String = str
        kana.forEach {
            val data = it.split("|")
            val src = data[1]
            val dest = data[2]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun katakanaToHiragana(str: String): String {
        var value: String = str
        kana.forEach {
            val data = it.split("|")
            val src = data[2]
            val dest = data[1]
            value = value.replace(src, dest)
        }
        return value
    }

    fun String.toHiragana(): String {
        return when {
            this.isRomaji() -> romajiToHiragana(this)
            this.isKatakana() -> katakanaToHiragana(this)
            else -> ""
        }
    }

    fun String.toKatakana(): String {
        return when {
            this.isRomaji() -> romajiToKatakana(this)
            this.isHiragana() -> hiraganaToKatakana(this)
            else -> ""
        }
    }
}