package com.leapwill.timersequence.database

import android.arch.persistence.room.*

@Dao
interface SegmentDao {
    @Query("SELECT * FROM Segments WHERE segmentid = :segmentid")
    fun getSegmentById(segmentid: String): Segment

    @Query("SELECT * FROM Segments WHERE position = :position")
    fun getSegmentByPosition(position: Int): Segment

    @Query("SELECT * FROM Segments ORDER BY position")
    fun getSegments(): List<Segment>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSegment(segment: Segment): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSegment(segment: Segment)

    @Delete
    fun deleteSegment(segment: Segment)
}