package me.spica.weather.view.dialog

import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import me.spica.weather.R

abstract class BaseDialogFragment<ViewBindingType : ViewBinding> : DialogFragment(), LifecycleEventObserver {


  // Variables
  private var _binding: ViewBindingType? = null

  protected val viewBinding
    get() = requireNotNull(_binding)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.BaseDialogStyle)
  }

  override fun onStart() {
    super.onStart()
    val dialogWindow = dialog?.window ?: return

    val lp = dialogWindow.attributes
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.MATCH_PARENT
    lp.dimAmount = 0f
    lp.format = PixelFormat.TRANSPARENT

    dialogWindow.attributes = lp
    dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    dialogWindow.decorView.setPadding(0, 0, 0, 0)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    dialogWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

    if (event == Lifecycle.Event.ON_DESTROY) {
      viewLifecycleOwner.lifecycle.removeObserver(this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewLifecycleOwner.lifecycle.addObserver(this)
    init()
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = setupViewBinding(inflater, container)
    return requireNotNull(_binding).root
  }

  abstract fun init()


  abstract fun setupViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
  ): ViewBindingType

  override fun onDestroy() {
    super.onDestroy()
    if (_binding != null) {
      _binding = null
    }
  }

}