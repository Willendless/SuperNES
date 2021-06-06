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

class GameCollectionItemAdapter(val context: Context, private val gameCollectionList: List<TextImageItem>):
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
        holder.gameText.text = gameInfo.text
        Glide.with(context).load(gameInfo.imageId).into(holder.gameImage)
        holder.gameStart.setOnClickListener {
            // TODO: start game
        }
        holder.gameDelete.setOnClickListener {
            // TODO: delete collection Item
        }
    }

    override fun getItemCount(): Int = gameCollectionList.size
}