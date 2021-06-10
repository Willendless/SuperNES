package com.willendless.nes.emulator.ppu

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.util.unreachable

@RequiresApi(Build.VERSION_CODES.O)
object PaletteMap {
   private val map = arrayOf(
      Color.valueOf(0x80.toFloat(), 0x80.toFloat(), 0x80.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x3D.toFloat(), 0xA6.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x12.toFloat(), 0xB0.toFloat()),
      Color.valueOf(0x44.toFloat(), 0x00.toFloat(), 0x96.toFloat()),
      Color.valueOf(0xA1.toFloat(), 0x00.toFloat(), 0x5E.toFloat()),
      Color.valueOf(0xC7.toFloat(), 0x00.toFloat(), 0x28.toFloat()),
      Color.valueOf(0xBA.toFloat(), 0x06.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x8C.toFloat(), 0x17.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x5C.toFloat(), 0x2F.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x10.toFloat(), 0x45.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x05.toFloat(), 0x4A.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x47.toFloat(), 0x2E.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x41.toFloat(), 0x66.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x00.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x05.toFloat(), 0x05.toFloat(), 0x05.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x00.toFloat(), 0x00.toFloat()),
      Color.valueOf(0xC7.toFloat(), 0xC7.toFloat(), 0xC7.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x77.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0x21.toFloat(), 0x55.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0x82.toFloat(), 0x37.toFloat(), 0xFA.toFloat()),
      Color.valueOf(0xEB.toFloat(), 0x2F.toFloat(), 0xB5.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x29.toFloat(), 0x50.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x22.toFloat(), 0x00.toFloat()),
      Color.valueOf(0xD6.toFloat(), 0x32.toFloat(), 0x00.toFloat()),
      Color.valueOf(0xC4.toFloat(), 0x62.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x35.toFloat(), 0x80.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x05.toFloat(), 0x8F.toFloat(), 0x00.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x8A.toFloat(), 0x55.toFloat()),
      Color.valueOf(0x00.toFloat(), 0x99.toFloat(), 0xCC.toFloat()),
      Color.valueOf(0x21.toFloat(), 0x21.toFloat(), 0x21.toFloat()),
      Color.valueOf(0x09.toFloat(), 0x09.toFloat(), 0x09.toFloat()),
      Color.valueOf(0x09.toFloat(), 0x09.toFloat(), 0x09.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xFF.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0x0F.toFloat(), 0xD7.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0x69.toFloat(), 0xA2.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0xD4.toFloat(), 0x80.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x45.toFloat(), 0xF3.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x61.toFloat(), 0x8B.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x88.toFloat(), 0x33.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0x9C.toFloat(), 0x12.toFloat()),
      Color.valueOf(0xFA.toFloat(), 0xBC.toFloat(), 0x20.toFloat()),
      Color.valueOf(0x9F.toFloat(), 0xE3.toFloat(), 0x0E.toFloat()),
      Color.valueOf(0x2B.toFloat(), 0xF0.toFloat(), 0x35.toFloat()),
      Color.valueOf(0x0C.toFloat(), 0xF0.toFloat(), 0xA4.toFloat()),
      Color.valueOf(0x05.toFloat(), 0xFB.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0x5E.toFloat(), 0x5E.toFloat(), 0x5E.toFloat()),
      Color.valueOf(0x0D.toFloat(), 0x0D.toFloat(), 0x0D.toFloat()),
      Color.valueOf(0x0D.toFloat(), 0x0D.toFloat(), 0x0D.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xFF.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0xA6.toFloat(), 0xFC.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0xB3.toFloat(), 0xEC.toFloat(), 0xFF.toFloat()),
      Color.valueOf(0xDA.toFloat(), 0xAB.toFloat(), 0xEB.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xA8.toFloat(), 0xF9.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xA8.toFloat(), 0xB3.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xD2.toFloat(), 0xB0.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xEF.toFloat(), 0xA6.toFloat()),
      Color.valueOf(0xFF.toFloat(), 0xF7.toFloat(), 0x9C.toFloat()),
      Color.valueOf(0xD7.toFloat(), 0xE8.toFloat(), 0x95.toFloat()),
      Color.valueOf(0xA6.toFloat(), 0xED.toFloat(), 0xAF.toFloat()),
      Color.valueOf(0xA2.toFloat(), 0xF2.toFloat(), 0xDA.toFloat()),
      Color.valueOf(0x99.toFloat(), 0xFF.toFloat(), 0xFC.toFloat()),
      Color.valueOf(0xDD.toFloat(), 0xDD.toFloat(), 0xDD.toFloat()),
      Color.valueOf(0x11.toFloat(), 0x11.toFloat(), 0x11.toFloat()),
      Color.valueOf(0x11.toFloat(), 0x11.toFloat(), 0x11.toFloat())
   )

   // REQUIRES: index < 64
   fun getColor(index: Int): Color {
       if (index >= 64) unreachable("Unable to get color with index $index")
       return map[index]
   }
}