package com.lqz.imap.core

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.map_interface.R

class MapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var iMapView: IMapViewDelegate? = null

    init {
        init()
    }

    private fun init() {
        inflateView()
    }

    private fun inflateView() {
        LayoutInflater.from(context).inflate(R.layout.map_view, this)
    }

    protected open fun getMapViewImp(): IMapViewDelegate? {
        return null
    }

    fun initialize(iMapView: IMapViewDelegate) {
        this.iMapView = iMapView
        removeAllViews()
        addView(iMapView.getDelfView())
    }

    fun getIMapView(): IMapViewDelegate? {
        return iMapView
    }

    fun onCreate(bundle: Bundle) {
        iMapView?.onCreate(bundle)
    }

    fun onResume() {
        iMapView?.onResume()
    }

    fun onPause() {
        iMapView?.onPause()
    }

    fun onDestroy() {
        iMapView?.onDestroy()
    }

    fun onStart() {
        iMapView?.onStart()
    }

    fun onStop() {
        iMapView?.onStop()
    }
}
