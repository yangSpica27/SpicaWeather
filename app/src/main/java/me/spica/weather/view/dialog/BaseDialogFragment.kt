package me.spica.weather.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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