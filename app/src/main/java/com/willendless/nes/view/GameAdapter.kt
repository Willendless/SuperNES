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

class GameAdapter(val context: Context, private val gameList: List<Game>): RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
    inner class GameViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.game_image)
        val gameName: TextView = view.findViewById(R.id.game_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.game_item, parent, false)
        val holder = GameViewHolder(view)
        view.setOnClickListener {
            val position = holder.adapterPosition
            val game = gameList[position]
            GameIntroActivity.actionStart(context,game.name, game.imageId)
        }
        return holder
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]
        holder.gameName.text = game.name
        Glide.with(context).load(game.imageId).into(holder.gameImage)
    }

    override fun getItemCount(): Int = gameList.size
}
