package com.willendless.nes.frontend

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper
import kotlinx.android.synthetic.main.fragment_home_games.*

class HomeGameFragment(private val title: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_games, container, false)

        if (context != null) {
            // game card of recycle view
            val dbHelper = NESDatabaseHelper(context!!, "supernes", 1)
                .readableDatabase
            val type = when (title) {
                "全部" -> "%"
                else -> title
            }
            val cursor = dbHelper.query("game", arrayOf("name", "img_name"),
                "type like ?", arrayOf(type), null, null, "id")
            val gameList = mutableListOf<TextImageItem>()
            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val imgName = cursor.getString(cursor.getColumnIndex("img_name"))
                    gameList.add(TextImageItem(name, this.resources.
                    getIdentifier(imgName, "drawable", context!!.packageName)))
                } while (cursor.moveToNext());
            }
            cursor.close()
            dbHelper.close()

            view.findViewById<RecyclerView>(R.id.game_recycle_view).apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = GameAdapter(context, gameList)
            }
        }
        return view
    }

}