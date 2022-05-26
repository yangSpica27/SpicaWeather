package me.spica.weather.ui.about

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import com.fondesa.recyclerviewdivider.dividerBuilder
import me.spica.weather.BuildConfig
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityAboutMeBinding
import me.spica.weather.tools.dp

/**
 * 关于我
 */
class AboutActivity : BindingActivity<ActivityAboutMeBinding>() {

    private val adapter = LicenceAdapter()

    override fun initializer() {
        this
            .dividerBuilder()
            .size(1.dp.toInt())
            .colorRes(R.color.line_divider)
            .showFirstDivider()
            .showLastDivider()
            .build()
            .addTo(viewBinding.rvLicence)

        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        viewBinding.rvLicence.layoutAnimation = animation
        viewBinding.rvLicence.adapter = adapter
        initToolBar()
        initData()
        viewBinding.tvVersionCode.text = BuildConfig.VERSION_NAME
    }

    private fun initToolBar() {
        setSupportActionBar(viewBinding.toolbar)
        viewBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        adapter.items.clear()
        adapter.items.add(
            Licence(
                name = "Timber",
                address = "https://github.com/JakeWharton/timber"
            )
        )
        adapter.items.add(
            Licence(
                name = "OkHttp",
                address = "https://github.com/square/okhttp"
            )
        )
        adapter.items.add(
            Licence(
                name = "Moshi",
                address = "https://github.com/square/moshi"
            )
        )
        adapter.items.add(
            Licence(
                name = "JPinYin",
                address = "https://github.com/shenkevin/jpinyin"
            )
        )
        adapter.items.add(
            Licence(
                name = "RecyclerViewDivider",
                address = "https://github.com/fondesa/recycler-view-divider"
            )
        )
        adapter.items.add(
            Licence(
                name = "Coil",
                address = "https://coil-kt.github.io/coil"
            )
        )
        adapter.notifyDataSetChanged()
        viewBinding.rvLicence.scheduleLayoutAnimation()
    }

    override fun setupViewBinding(inflater: LayoutInflater):
            ActivityAboutMeBinding = ActivityAboutMeBinding.inflate(inflater)
}
