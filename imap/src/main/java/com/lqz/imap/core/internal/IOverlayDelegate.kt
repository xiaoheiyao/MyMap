package com.lqz.imap.core.internal

interface IOverlayDelegate {
    fun getObject(): Any?

    fun setObject(o: Any)

    fun remove()

    fun getId(): String?

    fun setZIndex(var1: Float)

    fun getZIndex(): Float

    fun setVisible(var1: Boolean)

    fun isVisible(): Boolean
}