package com.leapwill.timersequence

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.leapwill.timersequence.database.Segment


class SegmentAdapter(context: Context, private val resource: Int, val segments: List<Segment>) : ArrayAdapter<Segment>(context, resource, segments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView != null && convertView is LinearLayout) {
            //recycle view for better performance
            view = convertView
        } else {
            view = LayoutInflater.from(context).inflate(resource, parent, false)
        }
        view.findViewById<TextView>(R.id.list_description).text = segments[position].description
        view.findViewById<TextView>(R.id.list_time).text = context.getString(R.string.time, segments[position].minutes, segments[position].seconds)
        view.setBackgroundColor(segments[position].background)
        return view
    }

}