package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper

class GameCollectionItemAdapter(val context: Context, private val gameCollectionList: MutableList<TextImageItem>):
    RecyclerView.Adapter<GameCollectionItemAdapter.GameCollectionViewHolder>() {

    inner class GameCollectionViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val gameText: TextView = view.findViewById(R.id.game_collection_item_text)
        val gameImage: ImageView = view.findViewById(R.id.game_collection_item_image)
        val gameStart: ImageButton = view.findViewById(R.id.game_collection_item_start)
        val gameDelete: ImageButton = view.findViewById(R.id.game_collection_item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_collection_item, parent, false)
        return GameCollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameCollectionViewHolder, position: Int) {
        val gameInfo = gameCollectionList[position]
        val gameName = gameInfo.text
        holder.gameText.text = gameName
        Glide.with(context).load(gameInfo.imageId).into(holder.gameImage)

        // game intro
        holder.itemView.setOnClickListener {
            GameIntroActivity.actionStart(context, gameName, gameInfo.imageId)
        }

        // start game
        holder.gameStart.setOnClickListener {
            val dbHelper = NESDatabaseHelper(context, "supernes", 1)
                .readableDatabase
            val cursor = dbHelper.query("game", arrayOf("file_path"),
                "name=?", arrayOf(gameName),
                null, null, null)
            if (cursor.moveToFirst()) {
                GameActivity.actionStart(context, gameName, cursor.getString(
                    cursor.getColumnIndex("file_path")))
            }
            cursor.close()
        }

        // delete game from collection
        holder.gameDelete.setOnClickListener {
            val dbHelper = NESDatabaseHelper(context, "supernes", 1)
                .writableDatabase
            dbHelper.delete("collection", "game_name=?",
                arrayOf(gameName))
            gameCollectionList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, gameCollectionList.size)
        }
    }

    override fun getItemCount(): Int = gameCollectionList.size
}