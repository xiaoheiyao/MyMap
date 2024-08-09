package com.lqz.imap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.View

object ViewUtils {
//    /**
//     * 把View绘制到Bitmap上
//     * @param comBitmap 需要绘制的View
//     * @param width 该View的宽度
//     * @param height 该View的高度
//     * @return 返回Bitmap对象
//     * add by csj 13-11-6
//     */
//    fun getViewBitmap(comBitmap: View?, width: Int, height: Int): Bitmap? {
//        var bitmap: Bitmap? = null
//        if (comBitmap != null) {
//            comBitmap.clearFocus()
//            comBitmap.setPressed(false)
//            val willNotCache = comBitmap.willNotCacheDrawing()
//            comBitmap.setWillNotCacheDrawing(false)
//
//            // Reset the drawing cache background color to fully transparent
//            // for the duration of this operation
//            val color = comBitmap.drawingCacheBackgroundColor
//            comBitmap.drawingCacheBackgroundColor = 0
//            val alpha = comBitmap.alpha
//            comBitmap.setAlpha(1.0f)
//            if (color != 0) {
//                comBitmap.destroyDrawingCache()
//            }
//            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
//            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
//            comBitmap.measure(widthSpec, heightSpec)
//            comBitmap.layout(0, 0, width, height)
//            try {
//                comBitmap.buildDrawingCache()
//            } catch (e: NullPointerException) {
//                Log.e("view.ProcessImageToBlur", "failed getViewBitmap()")
//                return null
//            }
//            val cacheBitmap = comBitmap.drawingCache
//            if (cacheBitmap == null) {
//                Log.e(
//                    "view.ProcessImageToBlur", "failed getViewBitmap($comBitmap)",
//                    RuntimeException()
//                )
//                return null
//            }
//            bitmap = Bitmap.createBitmap(cacheBitmap)
//            // Restore the view
//            comBitmap.setAlpha(alpha)
//            comBitmap.destroyDrawingCache()
//            comBitmap.setWillNotCacheDrawing(willNotCache)
//            comBitmap.drawingCacheBackgroundColor = color
//        }
//        return bitmap
//    }

    /**
     * 转化view为bitmap
     *
     * @param view
     * @return
     */
    fun convertViewToBitmap(view: View): Bitmap {
        // 创建一个空的Bitmap，大小与View的宽高相同
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        // 创建Canvas并将Bitmap设置为Canvas的背景
        val canvas = Canvas(bitmap)

        // 绘制View的内容到Canvas上
        view.draw(canvas)

        return bitmap
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(
//                0,
//                View.MeasureSpec.UNSPECIFIED
//            ),
//            View.MeasureSpec.makeMeasureSpec(
//                0,
//                View.MeasureSpec.UNSPECIFIED
//            )
//        )
//        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//        view.buildDrawingCache()
//        return view.drawingCache
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale
    }

    fun convertARGBToRGB(ARGB: Int): IntArray {
        val rgb = IntArray(3)
        rgb[0] = ARGB and 0xff0000 shr 16
        rgb[1] = ARGB and 0xff00 shr 8
        rgb[2] = ARGB and 0xff
        return rgb
    }

    fun getARGB(ARGB: Int): IntArray {
        val rgb = IntArray(4)
        rgb[0] = Color.alpha(ARGB)
        rgb[1] = Color.red(ARGB)
        rgb[2] = Color.green(ARGB)
        rgb[3] = Color.blue(ARGB)
        return rgb
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param rotate  旋转角度，可正可负
     * @return 旋转后的图片
     */
    fun rotateBitmap(origin: Bitmap?, rotate: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.getWidth()
        val height = origin.getHeight()
        val matrix = Matrix()
        matrix.setRotate(rotate)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }
}