package com.lqz.tianditu

object TianDiTuConstants {

    //天地图的KEY
    const val KEY = "f60c12fad90f2fbb624a1b8fb11d8f7a"

    private const val T0 = "t0"
    private const val T1 = "t1"
    private const val T2 = "t2"
    private const val T3 = "t3"
    private const val T4 = "t4"
    private const val T5 = "t5"
    private const val T6 = "t6"
    private const val T7 = "t7"
    private const val T = T0

    private const val ORG = "$T.tianditu.gov.cn"

    private const val URL = "https://$ORG"

    // 矢量底图，经纬度投影
    const val URL_VECTOR_2000 = "$URL/vec_c/wmts?"

    // 矢量底图，球面墨卡托投影
    const val URL_VECTOR_MERCATOR =
        "$URL/vec_w/wmts?"

    //矢量注记，经纬度投影
    const val URL_VECTOR_ANNOTATION_CHINESE_2000 =
        "$URL/cva_c/wmts?"

    //矢量注记，球面墨卡托投影
    const val URL_VECTOR_ANNOTATION_CHINESE_MERCATOR =
        "$URL/cva_w/wmts?"

    //影像底图 经纬度投影
    const val URL_IMAGE_2000 =
        "$URL/img_c/wmts?"

    //影像底图 ，球面墨卡托投影
    const val URL_IMAGE_MERCATOR =
        "$URL/img_w/wmts?"

    //影像注记，经纬度投影
    const val URL_IMAGE_ANNOTATION_CHINESE_2000 =
        "$URL/cia_c/wmts?"

    //影像注记，球面墨卡托投影
    const val URL_IMAGE_ANNOTATION_CHINESE_MERCATOR =
        "$URL/cia_w/wmts?"

    //地形晕渲 经纬度投影
    const val URL_TERRAIN_2000 = "$URL/ter_c/wmts?"

    //地形晕渲 球面墨卡托投影
    const val URL_TERRAIN_MERCATOR =
        "$URL/ter_w/wmts?"

    //地形注记，经纬度投影
    const val URL_TERRAIN_ANNOTATION_CHINESE_2000 =
        "$URL/cta_c/wmts?"

    //地形注记，球面墨卡托投影
    const val URL_TERRAIN_ANNOTATION_CHINESE_MERCATOR =
        "$URL/cta_w/wmts?"

    //全球境界，经纬度投影
    const val URL_GLOBAL_2000 = "$URL/ibo_c/wmts?tk=$KEY"

    //全球境界，球面墨卡托投影
    const val URL_GLOBAL_MERCATOR = "$URL/ibo_w/wmts?tk=$KEY"

    //三维地名
    const val URL_3D_ANNOTATION_CHINESE_2000 = "$URL/mapservice/GetTiles?tk=$KEY"

    //三维地形
    const val URL_3D_2000 = "$URL/mapservice/swdx?tk=$KEY"

    //矢量底图
    const val LAYER_NAME_VEC_C = "vec_c" //经纬度投影
    const val LAYER_NAME_VEC_W = "vec_w"//球面墨卡托投影

    //矢量注记
    const val LAYER_NAME_CVA_C = "cva_c" //经纬度投影
    const val LAYER_NAME_CVA_W = "cva_w"//球面墨卡托投影

    //影像底图
    const val LAYER_NAME_IMG_C = "img_c" //经纬度投影
    const val LAYER_NAME_IMG_W = "img_w"//球面墨卡托投影

    //影像注记
    const val LAYER_NAME_CIA_C = "cia_c" //经纬度投影
    const val LAYER_NAME_CIA_W = "cia_w"//球面墨卡托投影

    //地形晕渲
    const val LAYER_NAME_TER_C = "ter_c" //经纬度投影
    const val LAYER_NAME_TER_W = "ter_w"//球面墨卡托投影

    //地形注记
    const val LAYER_NAME_CTA_C = "cta_c" //经纬度投影
    const val LAYER_NAME_CTA_W = "cta_w"//球面墨卡托投影
}