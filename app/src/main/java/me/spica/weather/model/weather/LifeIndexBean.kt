package me.spica.weather.model.weather

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.squareup.moshi.JsonClass
import me.spica.weather.R


@JsonClass(generateAdapter = true)
data class LifeIndexBean(
    val type: Int, // 类型
    val name: String, // 指数名称
    val category: String, // 指数级别说明
    val text: String, // 说明
    @ColorRes val color: Int,
    @DrawableRes val iconRes: Int
) {

    companion object {
        const val SPT = 0x01 // 运动指数
        const val CLOTHES = 0x02 // 穿衣指数
        const val AIR = 0x04 // 空气指数
        const val CAR = 0x08 // 洗车指数
        const val UNKNOWN = -1
    }
}

/**
 * 目前只展示几个常用参数
 * 穿衣，运动，空气，洗车
 */

fun me.spica.weather.network.hefeng.index.Daily.toLifeIndexBean(): LifeIndexBean {
    when (type) {
        "1" -> {
            return LifeIndexBean(
                LifeIndexBean.SPT,
                "运动指数",
                category,
                text ?: "无更多描述",
                Color.BLACK,
                R.drawable.ic_spt
            )
        }
        "3" -> {
            return LifeIndexBean(
                LifeIndexBean.CLOTHES,
                "穿衣指数",
                category,
                text ?: "无更多描述",
                Color.BLACK,
                R.drawable.ic_clothes
            )
        }
        "10" -> {
            return LifeIndexBean(
                LifeIndexBean.AIR,
                "空气污染指数",
                category,
                text ?: "无更多描述",
                Color.BLACK,
                R.drawable.ic_air_index
            )
        }
        "2" -> {
            return LifeIndexBean(
                LifeIndexBean.CAR,
                "洗车指数",
                category,
                text ?: "无更多描述",
                Color.BLACK,
                R.drawable.ic_clean_car
            )
        }
    }
    return LifeIndexBean(
        LifeIndexBean.UNKNOWN,
        name,
        category,
        text ?: "无更多描述",
        R.color.textColorPrimaryHint,
        R.drawable.ic_clean_car
    )
}
