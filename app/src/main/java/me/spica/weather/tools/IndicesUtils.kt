package me.spica.weather.tools

import android.graphics.Color
import com.qweather.sdk.bean.base.IndicesType
import me.spica.weather.R


object IndicesUtils {


    private val levelColor3 = arrayListOf(
        R.color.l2,
        R.color.l5,
        R.color.l8,
    )

    private val levelColor4 = arrayListOf(
        R.color.l1,
        R.color.l2,
        R.color.l5,
        R.color.l8,
    )

    private val levelColor5 = arrayListOf(
        R.color.l1,
        R.color.l2,
        R.color.l5,
        R.color.l8,
    )

    private val levelColor6 = arrayListOf(
        R.color.l1,
        R.color.l2,
        R.color.l3,
        R.color.l5,
        R.color.l6,
        R.color.l8,
    )

    private val levelColor7 = arrayListOf(
        R.color.l1,
        R.color.l2,
        R.color.l3,
        R.color.l5,
        R.color.l6,
        R.color.l7,
    )

    private val levelColor8 = arrayListOf(
        R.color.l1,
        R.color.l2,
        R.color.l3,
        R.color.l5,
        R.color.l6,
        R.color.l7,
        R.color.l8,
    )

    /**
     * context：上下文
     * type：类型
     * level：级别
     */
    fun getColorRes(type: String, level: Int): Int {
        val lv = level - 1
        when (type) {
            IndicesType.SPI.code -> {
                return levelColor5[lv]
            }
            IndicesType.CW.code -> {
                return levelColor4[lv]
            }
            IndicesType.COMF.code -> {
                return levelColor7[lv]
            }
            IndicesType.DRSG.code -> {
                return levelColor7[lv]
            }
            IndicesType.FLU.code -> {
                return levelColor4[lv]
            }
            IndicesType.SPT.code -> {
                return levelColor3[lv]
            }
            IndicesType.TRAV.code -> {
                return levelColor5[lv]
            }
            IndicesType.UV.code -> {
                return levelColor5[lv]
            }
            IndicesType.AP.code -> {
                return levelColor5[lv]
            }
            IndicesType.AC.code -> {
                return levelColor4[lv]
            }
            IndicesType.AG.code -> {
                return levelColor5[lv]
            }
            IndicesType.GL.code -> {
                return levelColor5[lv]
            }
            IndicesType.SK.code -> {
                return levelColor8[lv]
            }
            IndicesType.MU.code -> {
                return levelColor7[lv]
            }
            IndicesType.DC.code -> {
                return levelColor6[lv]
            }
            IndicesType.PTFC.code -> {
                return levelColor5[lv]
            }
            IndicesType.FIS.code -> {
                return levelColor7[lv]
            }
            else -> {
                return Color.BLACK
            }
        }


    }


}
