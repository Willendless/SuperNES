package com.willendless.nes.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.willendless.nes.R

class GameInfoItemAdapter(private val context: Context, private val gameInfoList: List<GameInfoItem>):
    RecyclerView.Adapter<GameInfoItemAdapter.GameInfoItemViewHolder>() {

    inner class GameInfoItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val gameInfoText: TextView = view.findViewById(R.id.game_info_Item_text)
        val gameImage: ImageView = view.findViewById(R.id.game_info_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameInfoItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_info_item, parent, false)
        return  GameInfoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameInfoItemViewHolder, position: Int) {
        val gameInfo = gameInfoList[position]
        holder.gameInfoText.text = gameInfo.text
        Glide.with(context).load(gameInfo.imageId).into(holder.gameImage)
    }

    override fun getItemCount(): Int = gameInfoList.size

}