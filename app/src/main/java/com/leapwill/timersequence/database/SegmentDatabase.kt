package com.leapwill.timersequence.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(Segment::class)], version = 1)
abstract class SegmentDatabase : RoomDatabase() {
    abstract fun segmentDao(): SegmentDao

    companion object {
        @Volatile private var INSTANCE: SegmentDatabase? = null

        fun getInstance(context: Context): SegmentDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, SegmentDatabase::class.java,
                        "segments.db").build()
    }
}
