package com.willendless.nes.view

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.willendless.nes.R

class TestAdapter(val testList: List<String>, val activity: Activity) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val test: TextView = view.findViewById(R.id.testName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.test_item, parent, false)
        val viewholder = ViewHolder(view)
        viewholder.test.setOnClickListener {
            val position = viewholder.adapterPosition
            val testname = testList[position]
            Toast.makeText(parent.context, "you clicked view $testname", Toast.LENGTH_SHORT).show()
            try {
                val className = "com.willendless.nes.view.$testname"
                val intent = Intent(activity, Class.forName(className))
                activity.startActivity(intent)
            } catch (e: ClassNotFoundException) {
                Log.d("Class not found", testname)
            } catch (e: ActivityNotFoundException) {
                Log.d("Activity not found", "")
            }
        }
        return viewholder
    }

    override fun getItemCount() = testList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val testName = testList[position]
        holder.test.text = testName
    }

}