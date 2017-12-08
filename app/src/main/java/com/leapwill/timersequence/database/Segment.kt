package com.leapwill.timersequence.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.util.Log
import java.util.*

@Entity(tableName = "Segments")
class Segment(
        @ColumnInfo(name = "position")
        var position: Int = -1,
        @ColumnInfo(name = "minutes")
        var minutes: Int = 0,
        @ColumnInfo(name = "seconds")
        var seconds: Int = 0,
        @ColumnInfo(name = "description")
        var description: String = "",
        @PrimaryKey
        @ColumnInfo(name = "segmentid")
        var segmentid: String = UUID.randomUUID().toString()
) {
    @Ignore
    var background: Int = 0x0
}
