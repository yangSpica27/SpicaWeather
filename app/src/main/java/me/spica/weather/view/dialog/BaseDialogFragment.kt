package me.spica.weather.view.dialog

import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
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
    lp.dimAmount = 0.0f


    dialogWindow.attributes = lp
    dialogWindow.decorView.setPadding(0, 0, 0, 0)
    dialogWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    val options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    dialogWindow.decorView.systemUiVisibility = options

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