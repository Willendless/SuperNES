package com.willendless.nes.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NESDatabaseHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createGame = "create table Game (" +
            " id integer primary key autoincrement," +
            " name text," +
            " info text," +
            " year integer," +
            " type text," +
            " file_path text," +
            " img_name text)"

    private val createCollection = "create table Collection (" +
            " id integer primary key autoincrement," +
            " game_name text)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createGame)
        db?.execSQL(createCollection)

        // AlterEgo
        val alterego = ContentValues().apply {
            put("name", "Alter Ego")
            put("info", "  你控制一个拥有幻影双胞胎的英雄，他的另一个自我，进行闯关。当英雄时移动，" +
                    "他的另一个自我也以镜像的方式移动。在某些关卡上运动是水平镜像的，" +
                    "在其他一些关卡下运动是垂直镜像的。" +
                    "同一个关卡你可以在英雄和他的另一个自我之间进行有限次切换。\n" +
                    "  要完成一个关卡，您需要收集所有的跳跃像素。" +
                    "通常您只能通过英雄收集它们，但在某些关卡上有另一种颜色的像素，" +
                    "这些只能由英雄的另一个自我收集。")
            put("year", 2011)
            put("type", "解谜")
            put("file_path", "testGames/AlterEgo.nes")
            put("img_name", "alterego")
        }
        db?.insert("game", null, alterego)

        // Chase
        val chase = ContentValues().apply {
            put("name", "追逐")
            put("info", "在被追上之前尽可能多地收集宝石吧！好运！")
            put("year", 2012)
            put("type", "街机")
            put("file_path", "testGames/Chase.nes")
            put("img_name", "chase")
        }
        db?.insert("game", null, chase)

        // Cyo
        val cyo = ContentValues().apply {
            put("name", "cyo")
            put("info", "一个经典的飞机射击游戏。")
            put("year", 2015)
            put("type", "射击")
            put("file_path", "testGames/cyo.nes")
            put("img_name", "cyo")
        }
        db?.insert("game", null, cyo)

        val pacman = ContentValues().apply {
            put("name", "pacman")
            put("info", "经典吃豆人小游戏。")
            put("year", 2000)
            put("type", "迷宫")
            put("file_path", "testGames/PacMan.nes")
            put("img_name", "pacman")
        }
        db?.insert("game", null, pacman)


        val mario = ContentValues().apply {
            put("name", "超级马里奥")
            put("info", "本游戏中玩家需要控制游戏系列的主角——马力奥。" +
                    "游戏的目标在于游历蘑菇王国，并从大反派酷霸王的魔掌里救回桃花公主。" +
                    "马力奥可以在游戏世界收集散落各处的金币，或者敲击特殊的砖块，" +
                    "获得其中的金币或特殊道具。其他“秘密砖块”（通常为隐藏）" +
                    "还可能藏有更多的金币或某些稀有道具。如果玩家获得了超级蘑菇，马力欧会变大一倍，" +
                    "并能多承受一次敌人或障碍的伤害，还能将头顶上的砖块击碎。")
            put("year", 1985)
            put("type", "街机")
            put("file_path", "testGames/Super_mario_brothers.nes")
            put("img_name", "super_mario")
        }
        db?.insert("game", null, mario)

        val lanMaster = ContentValues().apply {
            put("name", "兰大师")
            put("info", "这是一个不知道如何操作的游戏。。。")
            put("year", 2011)
            put("type", "解谜")
            put("file_path", "testGames/Lan_Master.nes")
            put("img_name", "lan_master")
        }
        db?.insert("game", null, lanMaster)

        val lawnMower = ContentValues().apply {
            put("name", "无敌割草机")
            put("info", "只有在有限时间之内完成草地清理工作才能得到报酬！")
            put("year", 2011)
            put("type", "街机")
            put("file_path", "testGames/Lawn_Mower.nes")
            put("img_name", "lawn_mower")
        }
        db?.insert("game", null, lawnMower)

        val spaceDude = ContentValues().apply {
            put("name", "太空人")
            put("info", "是的，yet another 一个太空飞机射击游戏。")
            put("year", 2014)
            put("type", "射击")
            put("file_path", "testGames/spacedude.nes")
            put("img_name", "spacedude")
        }
        db?.insert("game", null, spaceDude)

        val virus = ContentValues().apply {
            put("name", "病毒清除者")
            put("info", "请尽力清除所有的病毒吧！")
            put("year", 2011)
            put("type", "解谜")
            put("file_path", "testGames/nes_virus_cleaner.nes")
            put("img_name", "virus")
        }
        db?.insert("game", null, virus)

        val bombSweeper = ContentValues().apply {
            put("name", "炸弹清除者")
            put("info", "你是约翰·解决者，无畏的炸弹清除者。 " +
                    "他必须从精神错乱的罪犯杰克手中拯救这座城市，杰克正试图用他的炸弹炸毁这一切。 ")
            put("year", 2002)
            put("type", "街机")
            put("file_path", "testGames/BombSweeper.nes")
            put("img_name", "bomb")
        }
        db?.insert("game", null, bombSweeper)

        val superpainter = ContentValues().apply {
            put("name", "超级画家")
            put("info", "于是你成为了一名超级画家。")
            put("year", 2015)
            put("type", "街机")
            put("file_path", "testGames/superpainter.nes")
            put("img_name", "superpainter")
        }
        db?.insert("game", null, superpainter)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}