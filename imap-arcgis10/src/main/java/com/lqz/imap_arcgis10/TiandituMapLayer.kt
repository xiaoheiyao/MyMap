package com.lqz.imap_arcgis10

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.esri.android.map.TiledServiceLayer
import com.esri.android.map.event.OnStatusChangedListener
import com.esri.core.geometry.Envelope
import com.esri.core.geometry.Point
import com.esri.core.geometry.SpatialReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.RejectedExecutionException

class TiandituMapLayer(
    val context: Context,
    val language: String,
    val countryCode: String,
    val source: String,
    val layerType: Int,
) : TiledServiceLayer(true) {

    init {
        try {
            serviceExecutor.submit { initLayer() }
        } catch (rejectedexecutionexception: RejectedExecutionException) {
            Log.e(
                "Tianditu Map Layer", "initialization of the layer failed.",
                rejectedexecutionexception
            )
        }
    }

    // 这里最大层级19，可以自定义（需要在下方的 scales 和 resolutions 中设置相对应的数据）
    private val minLevel = 0
    private val maxLevel = 18

    private val scales = doubleArrayOf(
        591657527.591555,
        295828763.79577702, 147914381.89788899, 73957190.948944002,
        36978595.474472001, 18489297.737236001, 9244648.8686180003,
        4622324.4343090001, 2311162.217155, 1155581.108577, 577790.554289,
        288895.277144, 144447.638572, 72223.819286, 36111.909643,
        18055.954822, 9027.9774109999998, 4513.9887049999998, 2256.994353,
        1128.4971760000001
    )
    private val resolutions = doubleArrayOf(
        156543.03392800014,
        78271.516963999937, 39135.758482000092, 19567.879240999919,
        9783.9396204999593, 4891.9698102499797, 2445.9849051249898,
        1222.9924525624949, 611.49622628138, 305.748113140558,
        152.874056570411, 76.4370282850732, 38.2185141425366,
        19.1092570712683, 9.55462853563415, 4.7773142679493699,
        2.3886571339746849, 1.1943285668550503, 0.59716428355981721,
        0.29858214164761665
    )

    private val origin = Point(-20037508.342787, 20037508.342787)

    private val dpi = 96

    private val tileWidth = 256
    private val tileHeight = 256

    override fun initLayer() {
        if (id == 0L) {
            nativeHandle = create()
            changeStatus(
                OnStatusChangedListener.STATUS
                    .fromInt(-1000)
            )
        } else {
            this.defaultSpatialReference =
                SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE_10)
            this.fullExtent = Envelope(
                -22041257.773878,
                -32673939.6727517, 22041257.773878, 20851350.0432886
            )
            this.tileInfo = TileInfo(
                origin, scales, resolutions,
                scales.size, dpi, tileWidth, tileHeight
            )
            super.initLayer()
        }
    }

    public override fun getTile(level: Int, col: Int, row: Int): ByteArray? {

        // TODO Auto-generated method stub
        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)

        val url = getTileUrl(level, col, row, 1)
        if (url == null || url.isEmpty()) {
            return null
        }
        val headers: Headers = LazyHeaders.Builder().build()
        val glideUrl = GlideUrl(
            url,
            headers
        )

        val submit: FutureTarget<File>

        submit =
            Glide.with(context).asFile().load(glideUrl).listener(object : RequestListener<File?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: File,
                    model: Any,
                    target: Target<File?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).submit(256, 256)


        try {
            Log.d("catfishdown", " try")
            return file2Bytes(submit.get())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("catfishdown", "eeeee")
        }
        Log.d("catfishdown", "return null")
        return null
    }


    protected fun getTileUrl(level: Int, col: Int, row: Int, type: Int): String {
        if (level > maxLevel || level < minLevel) {
            return ""
        }
        var url = ""
        when (layerType) {
            TiandituLayerTypes.IMAGE_MAP -> url = if (type == 1) {
                "https://t1.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default" +
                        "&TILEMATRIXSET=w" +
                        "&FORMAT=png" +
                        "&TILEMATRIX=" + level +
                        "&TILEROW=" + row +
                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a"
            } else {
                "a" + level + "_" + row + "_" + col
            }

            TiandituLayerTypes.VECTOR_MAP -> url = if (type == 1) {
                "https://t1.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default" +
                        "&TILEMATRIXSET=w" +
                        "&FORMAT=png" +
                        "&TILEMATRIX=" + level +
                        "&TILEROW=" + row +
                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a"
            } else {
                "b" + level + "_" + row + "_" + col
            }

            TiandituLayerTypes.TERRAIN_MAP -> url = if (type == 1) {
                "https://t1.tianditu.gov.cn/ter_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=ter&STYLE=default" +
                        "&TILEMATRIXSET=w" +
                        "&FORMAT=png" +
                        "&TILEMATRIX=" + level +
                        "&TILEROW=" + row +
                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a"
            } else {
                "c" + level + "_" + row + "_" + col
            }

            TiandituLayerTypes.ANNOTATION_IMAGE_MAP -> url = if (type == 1) {
                "https://t1.tianditu.gov.cn/cia_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cia&STYLE=default" +
                        "&TILEMATRIXSET=w" +
                        "&FORMAT=png" +
                        "&TILEMATRIX=" + level +
                        "&TILEROW=" + row +
                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a"
            } else {
                "d" + level + "_" + row + "_" + col
            }

            TiandituLayerTypes.ANNOTATION_VECTOR_MAP -> url = if (type == 1) {
                "https://t1.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default" +
                        "&TILEMATRIXSET=w" +
                        "&FORMAT=png" +
                        "&TILEMATRIX=" + level +
                        "&TILEROW=" + row +
                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a"
            } else {
                "e" + level + "_" + row + "_" + col
            }
        }
        Log.e("tiandituLayer", "url = $url")
        return url
    }


    fun file2Bytes(file: File?): ByteArray? {
        if (file == null) {
            return null
        }
        var buffer: ByteArray? = null
        try {
            val fis = FileInputStream(file)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var n: Int
            while (fis.read(b).also { n = it } != -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            bos.close()
            buffer = bos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return buffer
    }
}