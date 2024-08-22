package com.lqz.imap.core.listener

import com.lqz.imap.core.internal.IMarkerDelegate

interface IMarkerDelegateListListener {
    /**
     * 监听标记列表
     */
    fun iMarkerDelegateList(callback: List<IMarkerDelegate>)
}