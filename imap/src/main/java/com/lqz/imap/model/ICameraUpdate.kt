package com.lqz.imap.model

import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.model.ICameraPosition

/**
 * 图层位置更改的界面定义
 */
interface ICameraUpdate {

    fun getCameraPosition(iMap: IMapDelegate): ICameraPosition

}