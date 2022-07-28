package me.spica.weather.ui.warn

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityWarningBinding
import me.spica.weather.model.weather.AlertBean

/**
 * 警告详情的页面
 */
private const val INTENT_ALERT = "INTENT_ALERT"

class WarningDetailActivity : BindingActivity<ActivityWarningBinding>() {

  /**
   * [{"title":"南京市气象台发布高温橙色预警[Ⅱ级/严重]"
   * ,"description":"南京市气象台2022年07月28日09时18分升级发布高温橙色预警信号：
   * 预计今天全市各区的大部分街道（镇）最高气温将升至37℃以上，请注意防暑降温。","
   * status":"预警中","code":"0703","source":"国家预警信息发布中心"}]
   */

  companion object {
    // 启动
    fun startActivity(context: Context, alertBean: AlertBean) {
      val intent = Intent(context, WarningDetailActivity::class.java)
      intent.putExtra(INTENT_ALERT, alertBean)
      context.startActivity(intent)
    }
  }


  override fun initializer() {
    val alert = intent.getParcelableExtra<AlertBean>(INTENT_ALERT)
    viewBinding.toolbar.setNavigationOnClickListener {
      onBackPressed()
    }

    viewBinding.tvTitle.text = alert?.title
    viewBinding.tvUpdateTime.text = alert?.status
    viewBinding.tvBody.text = alert?.description

    val bgDrawable = getDrawable(R.drawable.bg_round)
    bgDrawable?.colorFilter = alert?.getAlertColor()?.let { PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
    viewBinding.ivWarnType.background = bgDrawable

  }


  override fun setupViewBinding(inflater: LayoutInflater): ActivityWarningBinding = ActivityWarningBinding.inflate(inflater)


}