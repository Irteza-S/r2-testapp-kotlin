package org.readium.r2.testapp.Results

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.readium.r2.testapp.R


class MyListAdapter (private var activity: Activity, private var items: List<ResultItem>) :  BaseAdapter(){
    private class ViewHolder(row: View?) {
        var id: TextView? = null
        var reference : TextView? = null
        var snippet: TextView? = null
        init {
            this.id = row?.findViewById<TextView>(R.id.id)
            this.reference = row?.findViewById<TextView>(R.id.reference)
            this.snippet = row?.findViewById<TextView>(R.id.snippet)
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_layout, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var emp = items[position]
        viewHolder.id?.text = emp.id.toString()
        viewHolder.snippet?.text = emp.snippet
        viewHolder.reference?.text = emp.reference

        return view as View
    }
    override fun getItem(i: Int): ResultItem {
        return items[i]
    }
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
    override fun getCount(): Int {
        return items.size
    }
}