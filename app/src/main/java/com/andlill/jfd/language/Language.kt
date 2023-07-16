package com.andlill.jfd.language

abstract class Language {
    companion object {
        val partOfSpeech = hashMapOf(
            0 to "Unclassified",
            1 to "Adjective",
            2 to "Auxiliary Adjective",
            3 to "Na-Adjective",
            4 to "No-Adjective",
            5 to "Adverb",
            6 to "Conjunction",
            7 to "Copula",
            8 to "Counter",
            9 to "Expression",
            10 to "Interjection",
            11 to "Noun",
            12 to "Adverbial Noun",
            13 to "Prefix Noun",
            14 to "Proper Noun",
            15 to "Suffix Noun",
            16 to "Temporal Noun",
            17 to "Numeric",
            18 to "Pronoun",
            19 to "Prefix",
            20 to "Suffix",
            21 to "Auxiliary Verb",
            22 to "Ichidan Verb",
            23 to "Irregular Verb",
            24 to "Intransitive Verb",
            25 to "Kuru Verb",
            26 to "Godan Verb",
            27 to "Nidan Verb",
            28 to "Suru Verb",
            29 to "Transitive Verb",
            30 to "Unspecified Verb",
            31 to "Yodan Verb",
        )
    }
}