package com.android.landmarks.util

import com.android.landmarks.domain.model.Coordinates
import com.android.landmarks.domain.model.Note
import kotlin.random.Random

object TestUtil {

    fun makeNotesList(size: Int, userName: String): MutableList<Note> {
        val random = Random
        val list = ArrayList<Note>(size)
        for (i in 600 until size + 600) {
            val note = Note()
            note.id = (i + 1).toString()
            note.userName = userName
            note.remark = "Test remark" + (i + 1).toString()
            note.location =
                Coordinates(
                    random.nextDouble(34.500000, 35.000000),
                    random.nextDouble(137.200000, 138.000000)
                )
            list.add(note)
        }
        return list
    }
}
