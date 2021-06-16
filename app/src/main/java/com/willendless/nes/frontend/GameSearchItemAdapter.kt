package com.willendless.nes.frontend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper

class GameSearchItemAdapter(private val context: Context, private  val gameSearchResultList: List<TextImageItem>):
    RecyclerView.Adapter<GameSearchItemAdapter.GameSearchItemViewHolder>() {

    inner class GameSearchItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.game_search_item_image)
        val textView: TextView = view.findViewById(R.id.game_search_item_text)
        val gameStart: ImageButton = view.findViewById(R.id.game_search_item_start)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameSearchItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_search_item, parent, false)
        return GameSearchItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameSearchItemViewHolder, position: Int) {
        val searchResult = gameSearchResultList[position]

        Glide.with(context).load(searchResult.imageId).into(holder.imageView)
        holder.textView.text = searchResult.text

        // game intro
        holder.itemView.setOnClickListener {
            GameIntroActivity.actionStart(context, searchResult.text, searchResult.imageId)
        }

        // start button
        holder.gameStart.setOnClickListener {
            val dbHelper = NESDatabaseHelper(context, "game", 1).readableDatabase
            dbHelper.query("game", arrayOf("file_path"), "name=?",
                arrayOf(searchResult.text), null, null, null).use {
                    if (it.moveToFirst()) {
                        GameActivity.actionStart(context, searchResult.text, it.getString(
                            it.getColumnIndex("file_path")
                        ))
                    }
            }
        }
    }

    override fun getItemCount(): Int = gameSearchResultList.size

}