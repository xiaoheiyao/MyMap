package com.lqz.imap.core.internal

import android.content.Context
import android.os.Bundle
import android.view.View
import com.lqz.imap.core.listener.OnMapReadyCallback

/**
 * 地图View委托
 */
interface IMapViewDelegate {
    /**
     * 异步获取地图
     */
    fun getMapAsync(callback: (IMapDelegate) -> Unit)

    fun getContext(): Context

    fun onCreate(bundle: Bundle?)

    fun onResume()

    fun onPause()

    fun onStart()

    fun onStop()

    fun onDestroy()

    fun getDelfView(): View
}