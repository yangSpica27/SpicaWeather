package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.dp

interface SpicaWeatherCard {


    var animatorView: View

    var enterAnim: AnimatorSet

    var index: Int

    fun startEnterAnim() {
        enterAnim.doOnEnd {
            enterAnim = AnimatorSet()
        }
        enterAnim.startDelay = 200
        enterAnim.start()
    }

    fun resetAnim() {
        hasInScreen = false
        animatorView.alpha = 0f
        enterAnim.playTogether(
            ObjectAnimator.ofFloat(
                animatorView,
                "alpha", 0f, 1f
            ),
            ObjectAnimator.ofFloat(
                animatorView, "translationY", 120.dp, 0f
            ).apply {
                interpolator = OvershootInterpolator(.3f * (index + 1))
            },
            ObjectAnimator.ofFloat(
                animatorView, "scaleY", 1.025f, 1f
            ).apply {
                interpolator = DecelerateInterpolator(1f)
            },
            ObjectAnimator.ofFloat(
                animatorView, "scaleX", 1.025f, 1f
            ).apply {
                interpolator = DecelerateInterpolator(1f)
            }
        )
        enterAnim.duration = 500
    }

    var hasInScreen: Boolean


    fun checkEnterScreen(
        isIn: Boolean
    ) {
        if (hasInScreen) {
            return
        }

        if (isIn) {
            hasInScreen = true
            startEnterAnim()
        }
    }


    // 绑定数据
    fun bindData(weather: Weather)

}
