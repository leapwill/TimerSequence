package com.leapwill.timersequence

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import com.leapwill.timersequence.database.Segment
import com.leapwill.timersequence.database.SegmentDao
import com.leapwill.timersequence.database.SegmentDatabase

class MainActivity : Activity(), EditSegmentDialogFragment.EditSegmentListener {
    private var segmentIndex: Int = 0
    private var paused: Boolean = true
    private lateinit var timer: CountDownTimer
    private lateinit var db: SegmentDatabase
    private val timerListItemClick = AdapterView.OnItemClickListener { parent, _, position, _ ->
        val segment = parent.adapter.getItem(position) as Segment
        EditSegmentDialogFragment.newInstance(segment)
                .show(fragmentManager, "editSegmentDialog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(findViewById(R.id.actionbar))
        this.db = SegmentDatabase.getInstance(this)
        this.findViewById<ListView>(R.id.timer_list).onItemClickListener = timerListItemClick
        this.loadSegments()
    }

    private fun loadSegments() {
        GetSegments(db.segmentDao(), this).execute()
    }

    private fun startNextTimer() {
        val listView = this.findViewById<ListView>(R.id.timer_list)
        val adapter = listView.adapter as SegmentAdapter
        val currentSegment = adapter.getItem(segmentIndex) as Segment
        val secsTotal = currentSegment.minutes * 60 + currentSegment.seconds
        currentSegment.background = 0x22e9ed2c

        timer = object : CountDownTimer(secsTotal * 1000L, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secsUntilFinished = millisUntilFinished / 1000
                currentSegment.minutes = (secsUntilFinished / 60).toInt()
                currentSegment.seconds = (secsUntilFinished % 60).toInt()
                adapter.notifyDataSetChanged()
            }

            override fun onFinish() {
                currentSegment.background = 0x22b3bb00
                if (++segmentIndex < adapter.segments.size) {
                    startNextTimer()
                    listView.smoothScrollToPosition(segmentIndex)
                } else {
                    reset(null)
                }
            }

        }.start()
    }

    //region Bottom bar button click listeners
    fun reset(v: View?) {
        this.paused = false
        this.playpause(findViewById(R.id.button_playpause))
        this.segmentIndex = 0
        this.loadSegments()
    }

    fun playpause(v: View) {
        val ib = v as ImageButton
        if (this.paused) { //play
            //begin at segIdx
            this.startNextTimer()
            //switch click from edit to skipping to index
            this.findViewById<ListView>(R.id.timer_list).onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                this.timer.cancel()
                this.segmentIndex = position
                this.startNextTimer()
            }
            //set up to pause
            ib.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentDark))
            ib.setImageResource(R.drawable.ic_pause_black_24dp)
            this.paused = false
        } else { //pause
            this.timer.cancel()
            //allow editing again
            this.findViewById<ListView>(R.id.timer_list).onItemClickListener = timerListItemClick
            //set up to play
            ib.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            ib.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            this.paused = true
        }
    }

    fun add(v: View) {
        EditSegmentDialogFragment().show(fragmentManager, "editSegmentDialog")
    }
    //endregion

    override fun onEditSegmentDialogPositiveClick(dialog: EditSegmentDialogFragment) {
        if (dialog.position == -1) {
            //insert new segment as last
            dialog.segmentReturned.position = findViewById<ListView>(R.id.timer_list).count
            InsertSegment(this.db.segmentDao(), dialog.segmentReturned, this).execute()
        } else {
            //replace old segment with new data
            dialog.segmentReturned.segmentid = dialog.segmentId
            UpdateSegment(this.db.segmentDao(), dialog.segmentReturned, this).execute()
        }
        ((findViewById<ListView>(R.id.timer_list)).adapter as BaseAdapter).notifyDataSetChanged()
    }

    override fun onEditSegmentDialogNegativeClick(dialog: EditSegmentDialogFragment) {
        //replace old segment with new data
        dialog.segmentReturned.segmentid = dialog.segmentId
        DeleteSegment(this.db.segmentDao(), dialog.segmentReturned, this).execute()
        ((findViewById<ListView>(R.id.timer_list)).adapter as BaseAdapter).notifyDataSetChanged()
    }

    //region Database AsyncTasks
    private class GetSegments(val segmentDao: SegmentDao, val context: Context) : AsyncTask<Void, Void, List<Segment>>() {

        override fun doInBackground(vararg params: Void?): List<Segment> {
            return segmentDao.getSegments()
        }

        override fun onPostExecute(result: List<Segment>?) {
            super.onPostExecute(result)
            if (context is Activity && result != null) {
                val segmentListView = context.findViewById<ListView>(R.id.timer_list)
                val index = segmentListView.firstVisiblePosition
                val view: View? = segmentListView.getChildAt(0)
                segmentListView.adapter = SegmentAdapter(context, R.layout.list_segment, result)
                (segmentListView.adapter as BaseAdapter).notifyDataSetChanged()
                segmentListView.setSelectionFromTop(index, view?.top ?: 0)
            }
        }

    }

    private class InsertSegment(val segmentDao: SegmentDao, val segment: Segment, val context: Context) : AsyncTask<Void, Void, Long>() {

        override fun doInBackground(vararg params: Void?): Long {
            return segmentDao.insertSegment(segment)
        }

        override fun onPostExecute(result: Long?) {
            super.onPostExecute(result)
            GetSegments(segmentDao, context).execute()
        }
    }

    private class UpdateSegment(val segmentDao: SegmentDao, val segment: Segment, val context: Context) : AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void?) {
            segmentDao.updateSegment(segment)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            GetSegments(segmentDao, context).execute()
        }
    }

    private class DeleteSegment(val segmentDao: SegmentDao, val segment: Segment, val context: Context) : AsyncTask<Void, Void, Unit>() {
        override fun doInBackground(vararg params: Void?) {
            segmentDao.deleteSegment(segment)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            GetSegments(segmentDao, context).execute()
        }
    }
    //endregion

}


