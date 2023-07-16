package com.andlill.jfd.io.database.dictionary

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.andlill.jfd.model.DictKanji
import com.andlill.jfd.model.DictionaryEntry

@Dao
interface DictionaryDAO {

    @Transaction
    @Query("SELECT DISTINCT Entry.* FROM Entry JOIN Reading ON Entry.ID = Reading.EntryID WHERE Reading.Kanji LIKE :kanji || '%'")
    suspend fun searchByKanji(kanji: String): List<DictionaryEntry>

    @Transaction
    @Query("SELECT DISTINCT Entry.* FROM Entry JOIN Reading ON Entry.ID = Reading.EntryID WHERE Reading.Kana LIKE :hiragana || '%' OR Reading.Kana LIKE :katakana || '%'")
    suspend fun searchByKana(hiragana: String, katakana: String): List<DictionaryEntry>

    @Transaction
    @Query("SELECT DISTINCT Entry.* FROM Entry JOIN Sense ON Entry.ID = Sense.EntryID WHERE Sense.Glossary = :query OR Sense.Glossary LIKE :query || ' %' OR Sense.Glossary LIKE '% ' || :query OR Sense.Glossary LIKE '% ' || :query || ' %'")
    suspend fun searchByGlossary(query: String): List<DictionaryEntry>

    @Transaction
    @Query("SELECT * FROM Entry WHERE ID = :id")
    suspend fun getEntry(id: Int): DictionaryEntry

    @Transaction
    @Query("SELECT * FROM Entry WHERE ID IN (:args)")
    suspend fun getEntry(args: List<Int>): List<DictionaryEntry>

    @Query("SELECT * FROM Kanji WHERE Character IN (:args)")
    suspend fun getKanji(args: List<String>): List<DictKanji>
}