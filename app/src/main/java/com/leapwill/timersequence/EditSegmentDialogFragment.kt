package com.leapwill.timersequence

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.TextView
import com.leapwill.timersequence.database.Segment

class EditSegmentDialogFragment : DialogFragment() {

    var position = -1
    lateinit var segmentId: String
    private lateinit var mListener: EditSegmentListener
    lateinit var segmentReturned: Segment

    companion object {
        fun newInstance(segment: Segment): EditSegmentDialogFragment {
            val f = EditSegmentDialogFragment()
            val args = Bundle()
            args.putInt("position", segment.position)
            args.putInt("minutes", segment.minutes)
            args.putInt("seconds", segment.seconds)
            args.putString("description", segment.description)
            args.putString("segmentId", segment.segmentid)
            f.arguments = args
            return f
        }
    }

    interface EditSegmentListener {
        fun onEditSegmentDialogPositiveClick(dialog: EditSegmentDialogFragment)
    }

    override fun onAttach(context: Context?) {
        //TODO Samsung devices don't call this
        super.onAttach(context)
        try {
            mListener = context as EditSegmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement EditSegmentListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = activity.layoutInflater.inflate(R.layout.segment_edit_dialog, null)
        dialogView.findViewById<NumberPicker>(R.id.segment_edit_minutes).maxValue = 59
        dialogView.findViewById<NumberPicker>(R.id.segment_edit_seconds).maxValue = 59

        if (arguments != null) {
            //load in pre-existing information
            dialogView.findViewById<NumberPicker>(R.id.segment_edit_minutes).value =
                    arguments.getInt("minutes", 0)
            dialogView.findViewById<NumberPicker>(R.id.segment_edit_seconds).value =
                    arguments.getInt("seconds", 0)
            dialogView.findViewById<TextView>(R.id.segment_edit_description).text =
                    arguments.getString("description", "test")
            this.segmentId = arguments.getString("segmentId", "")
            this.position = arguments.getInt("position")
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.edit_title)
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                    this.segmentReturned = Segment(
                            position = this.position,
                            minutes = dialogView.findViewById<NumberPicker>(R.id.segment_edit_minutes).value,
                            seconds = dialogView.findViewById<NumberPicker>(R.id.segment_edit_seconds).value,
                            description = dialogView.findViewById<TextView>(R.id.segment_edit_description).text.toString()
                    )
                    this.mListener.onEditSegmentDialogPositiveClick(this) })

        return builder.create()
    }

}