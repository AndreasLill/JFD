package com.andlill.jld.language

import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.model.Verb

class VerbConjugation(entry: DictionaryEntry) {

    val affirmative = Verb()
    val negative = Verb()

    init {
        val word = entry.getPrimaryReading()
        when {
            entry.sense[0].partOfSpeech.any { pos -> pos.lowercase().startsWith("ichidan") } -> {
                val base = word.substring(0, word.length - 1)
                conjugateIchidan(base)
            }
            entry.sense[0].partOfSpeech.any { pos -> pos.lowercase().startsWith("godan") } -> {
                val character = word[word.length - 1]
                val base = word.substring(0, word.length - 1)
                when (character) {
                    'う' -> conjugateGodanU(base)
                    'く' -> conjugateGodanKU(base)
                    'ぐ' -> conjugateGodanGU(base)
                    'ぶ' -> conjugateGodanBU(base)
                    'む' -> conjugateGodanMU(base)
                    'ぬ' -> conjugateGodanNU(base)
                    'る' -> conjugateGodanRU(base)
                    'す' -> conjugateGodanSU(base)
                    'つ' -> conjugateGodanTSU(base)
                }
            }
            entry.sense[0].partOfSpeech.any { pos -> pos.lowercase().startsWith("kuru") } -> {
                val base = word.substring(0, word.length - 1)
                conjugateKURU(base)
            }
            entry.sense[0].partOfSpeech.any { pos -> pos.lowercase().startsWith("suru") } -> {
                val base = word.substring(0, word.length - 1)
                conjugateSURU(base)
            }
        }
    }

    private fun conjugateIchidan(base: String) {
        affirmative.present = "${base}る"
        affirmative.past = "${base}た"
        affirmative.teForm = "${base}て"
        affirmative.potential = "${base}られる"
        affirmative.passive = "${base}られる"
        affirmative.causative = "${base}させる"
        affirmative.imperative = "${base}ろ"

        negative.present = "${base}ない"
        negative.past = "${base}なかった"
        negative.teForm = "${base}なくて"
        negative.potential = "${base}られない"
        negative.passive = "${base}られない"
        negative.causative = "${base}させない"
        negative.imperative = "${base}るな"
    }

    private fun conjugateGodanU(base: String) {
        affirmative.present = "${base}う"
        affirmative.past = "${base}った"
        affirmative.teForm = "${base}って"
        affirmative.potential = "${base}える"
        affirmative.passive = "${base}われる"
        affirmative.causative = "${base}わせる"
        affirmative.imperative = "${base}え"

        negative.present = "${base}わない"
        negative.past = "${base}わなかった"
        negative.teForm = "${base}わなくて"
        negative.potential = "${base}えない"
        negative.passive = "${base}われない"
        negative.causative = "${base}わせない"
        negative.imperative = "${base}うな"
    }

    private fun conjugateGodanKU(base: String) {
        affirmative.present = "${base}く"
        affirmative.past = "${base}いた"
        affirmative.teForm = "${base}いて"
        affirmative.potential = "${base}ける"
        affirmative.passive = "${base}かれる"
        affirmative.causative = "${base}かせる"
        affirmative.imperative = "${base}け"

        negative.present = "${base}かない"
        negative.past = "${base}かなかった"
        negative.teForm = "${base}かなくて"
        negative.potential = "${base}けない"
        negative.passive = "${base}かれない"
        negative.causative = "${base}かせない"
        negative.imperative = "${base}くな"
    }

    private fun conjugateGodanGU(base: String) {
        affirmative.present = "${base}ぐ"
        affirmative.past = "${base}いだ"
        affirmative.teForm = "${base}いで"
        affirmative.potential = "${base}げる"
        affirmative.passive = "${base}がれる"
        affirmative.causative = "${base}がせる"
        affirmative.imperative = "${base}げ"

        negative.present = "${base}がない"
        negative.past = "${base}がなかった"
        negative.teForm = "${base}がなくて"
        negative.potential = "${base}げない"
        negative.passive = "${base}がれない"
        negative.causative = "${base}がせない"
        negative.imperative = "${base}ぐな"
    }

    private fun conjugateGodanBU(base: String) {
        affirmative.present = "${base}ぶ"
        affirmative.past = "${base}んだ"
        affirmative.teForm = "${base}んで"
        affirmative.potential = "${base}べる"
        affirmative.passive = "${base}ばれる"
        affirmative.causative = "${base}ばせる"
        affirmative.imperative = "${base}べ"

        negative.present = "${base}ばない"
        negative.past = "${base}ばなかった"
        negative.teForm = "${base}ばなくて"
        negative.potential = "${base}べない"
        negative.passive = "${base}ばれない"
        negative.causative = "${base}ばせない"
        negative.imperative = "${base}ぶな"
    }

    private fun conjugateGodanMU(base: String) {
        affirmative.present = "${base}む"
        affirmative.past = "${base}んだ"
        affirmative.teForm = "${base}んで"
        affirmative.potential = "${base}める"
        affirmative.passive = "${base}まれる"
        affirmative.causative = "${base}ませる"
        affirmative.imperative = "${base}め"

        negative.present = "${base}まない"
        negative.past = "${base}まなかった"
        negative.teForm = "${base}まなくて"
        negative.potential = "${base}めない"
        negative.passive = "${base}まれない"
        negative.causative = "${base}ませない"
        negative.imperative = "${base}むな"
    }

    private fun conjugateGodanNU(base: String) {
        affirmative.present = "${base}ぬ"
        affirmative.past = "${base}んだ"
        affirmative.teForm = "${base}んで"
        affirmative.potential = "${base}ねる"
        affirmative.passive = "${base}なれる"
        affirmative.causative = "${base}なせる"
        affirmative.imperative = "${base}ね"

        negative.present = "${base}なない"
        negative.past = "${base}ななかった"
        negative.teForm = "${base}ななくて"
        negative.potential = "${base}ねない"
        negative.passive = "${base}なれない"
        negative.causative = "${base}なせない"
        negative.imperative = "${base}ぬな"
    }

    private fun conjugateGodanRU(base: String) {
        affirmative.present = "${base}る"
        affirmative.past = "${base}った"
        affirmative.teForm = "${base}って"
        affirmative.potential = "${base}れる"
        affirmative.passive = "${base}られる"
        affirmative.causative = "${base}らせる"
        affirmative.imperative = "${base}れ"

        negative.present = "${base}らない"
        negative.past = "${base}らなかった"
        negative.teForm = "${base}らなくて"
        negative.potential = "${base}れない"
        negative.passive = "${base}られない"
        negative.causative = "${base}らせない"
        negative.imperative = "${base}るな"
    }

    private fun conjugateGodanSU(base: String) {
        affirmative.present = "${base}す"
        affirmative.past = "${base}した"
        affirmative.teForm = "${base}して"
        affirmative.potential = "${base}せる"
        affirmative.passive = "${base}される"
        affirmative.causative = "${base}させる"
        affirmative.imperative = "${base}せ"

        negative.present = "${base}さない"
        negative.past = "${base}さなかった"
        negative.teForm = "${base}さなくて"
        negative.potential = "${base}せない"
        negative.passive = "${base}されない"
        negative.causative = "${base}させない"
        negative.imperative = "${base}すな"
    }

    private fun conjugateGodanTSU(base: String) {
        affirmative.present = "${base}つ"
        affirmative.past = "${base}った"
        affirmative.teForm = "${base}って"
        affirmative.potential = "${base}てる"
        affirmative.passive = "${base}たれる"
        affirmative.causative = "${base}たせる"
        affirmative.imperative = "${base}て"

        negative.present = "${base}たない"
        negative.past = "${base}たなかった"
        negative.teForm = "${base}たなくて"
        negative.potential = "${base}てない"
        negative.passive = "${base}たれない"
        negative.causative = "${base}たせない"
        negative.imperative = "${base}つな"
    }

    private fun conjugateKURU(base: String) {
        affirmative.present = "${base}る"
        affirmative.past = "${base}た"
        affirmative.teForm = "${base}て"
        affirmative.potential = "${base}られる"
        affirmative.passive = "${base}られる"
        affirmative.causative = "${base}させる"
        affirmative.imperative = "${base}い"

        negative.present = "${base}ない"
        negative.past = "${base}なかった"
        negative.teForm = "${base}なくて"
        negative.potential = "${base}られない"
        negative.passive = "${base}られない"
        negative.causative = "${base}させない"
        negative.imperative = "${base}るな"
    }

    private fun conjugateSURU(base: String) {
        affirmative.present = "${base}る"
        affirmative.past = "${base}た"
        affirmative.teForm = "${base}て"
        affirmative.potential = "できる"
        affirmative.passive = "${base}れる"
        affirmative.causative = "${base}せる"
        affirmative.imperative = "${base}ろ"

        negative.present = "${base}ない"
        negative.past = "${base}なかった"
        negative.teForm = "${base}なくて"
        negative.potential = "できない"
        negative.passive = "${base}れない"
        negative.causative = "${base}せない"
        negative.imperative = "${base}るな"
    }
}