package com.lqz.app_arcgis100.feature.util

import android.util.Log
import com.lqz.imap.core.internal.IMarkerDelegate
import com.lqz.imap.model.ILatLng
import com.lqz.imap.utils.IMapUtils
import com.lqz.app_arcgis100.feature.MercatorProjection
import org.locationtech.jts.algorithm.Angle
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.math.Vector2D
import org.locationtech.jts.operation.distance.DistanceOp
import kotlin.math.abs
import kotlin.math.cos


/**
 * 多边形工具类，
 */
object FeatureUtil {

    /**
     * 判断多边形是否相交，
     * @return true 不相交 ；false 相交
     */
    fun isSimple(markerList: List<IMarkerDelegate>): Boolean {
        if (markerList.size < 3) { //当点数小于3，永远不相交，防止算法崩溃
            return true
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(markerList.size + 1)
        markerList.forEachIndexed { index, marker ->
            val latLng: ILatLng = marker.getPosition()
            coordinates[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        coordinates[markerList.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //边界点
        return polygon.isSimple //判断是否相交！！！
    }

    fun isSimple2(iLatLngs: List<ILatLng>): Boolean {
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(iLatLngs.size + 1)
        iLatLngs.forEachIndexed { index, latLng ->
            coordinates[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        coordinates[iLatLngs.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //边界点
        return polygon.isSimple //判断是否相交！！！
    }

    fun <T> getPoints(markerList: List<IMarkerDelegate>): List<T> {
        val points: MutableList<T> = ArrayList()
        for (markerDelegate in markerList) {
            points.add(markerDelegate.getObject() as T)
        }
        return points
    }

    fun getLatLngs(markerList: List<IMarkerDelegate>): List<ILatLng> {
        val iLatLngs: MutableList<ILatLng> = ArrayList()
        for (markerDelegate in markerList) {
            iLatLngs.add(markerDelegate.getPosition())
        }
        return iLatLngs
    }

    /**
     * Really small number.
     */
    val EPSILON = 10E-3

    fun isZeroLatLng(iLatLng: ILatLng): Boolean {
        return (abs(iLatLng.latitude) < EPSILON) && (abs(iLatLng.longitude) < EPSILON)
    }

    /**
     * 是否合法
     */
    fun isLegal(iLatLng: ILatLng): Boolean {
        return IMapUtils.checkILatLng(iLatLng)
    }

    /**
     * 点距离多边形的距离是多少？
     * 点到多边形的距离
     *  @return -1 点在多边形内部。点到多边形的距离
     */
    fun getDistancePointToPolygon(latLngList: List<ILatLng>, point: ILatLng): Double {
        if (latLngList.size < 3) {
            return -1.0 //不是多边形
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(latLngList.size + 1)
        latLngList.forEachIndexed { index, latLng ->
            //todo 将点转换成墨卡托点之后，再赋值！！！
            val mercatorPoint = MercatorProjection.toMercatorPoint(latLng)
            coordinates[index] =
                Coordinate(mercatorPoint.x, mercatorPoint.y)
        }
        coordinates[latLngList.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //多边形
        val mercatorPoint = MercatorProjection.toMercatorPoint(point)
        val geo = geometryFactory.createPoint(
            Coordinate(
                mercatorPoint.x,
                mercatorPoint.y
            )
        )
        if (polygon.contains(geo)) {
            return -1.0 //点在多边形内部
        }
        val distance = geo.distance(polygon)
        //todo 结果是对的，但还是有不理解的地方：比如：墨卡托投影的最终结果为啥还要经过矫正？？有时间再研究！！！
        Log.e("LQZ", "点到多边形墨卡托距离 = $distance")
        Log.e("LQZ", "点到多边形实际距离 = ${distance * cos(Math.toRadians(point.latitude))}")
        return distance * cos(Math.toRadians(point.latitude))
    }

    /**
     * 因为在纬线上，经度每差1度,实际距离为111×cosθ千米。（其中θ表示该纬线的纬度，在不同纬线上,经度每差1度的实际距离是不相等的）。
     * 所以在计算之前对所有的经度做处理 经度 * cos(纬度)
     */
    fun getDistancePointToPoint(latLngA: ILatLng, latLngB: ILatLng): Double {
        val geometryFactory = GeometryFactory()
        val point1: Point =
            geometryFactory.createPoint(
                Coordinate(
                    latLngA.latitude,
                    latLngA.longitude * cos(Math.toRadians(latLngA.latitude))
                )
            )
        val point2: Point =
            geometryFactory.createPoint(
                Coordinate(
                    latLngB.latitude,
                    latLngB.longitude * cos(Math.toRadians(latLngA.latitude))
                )
            )
        return convertDegreeToMeter(DistanceOp.distance(point1, point2), latLngA.latitude)
    }

    // 将经纬度距离（单位：度）转换为米
    private fun convertDegreeToMeter(
        distanceInDegrees: Double,
        latitude: Double
    ): Double {
        // 地球半径（单位：米）
        val earthRadius = 6371000.0
        // 弧长计算
        val arcLength = distanceInDegrees * (Math.PI / 180.0) * earthRadius
        // 在纬度上调整弧长，考虑纬度对实际距离的影响
//        Log.e("LQZ", "distanceInDegrees = $distanceInDegrees")
//        Log.e("LQZ", "arcLength = $arcLength")
//        Log.e("LQZ", "cos = ${cos(Math.toRadians(latitude))}")
//        Log.e("LQZ", "arcLength * cos = ${arcLength * cos(Math.toRadians(latitude))}")
//        return arcLength * cos(Math.toRadians(latitude))
        return arcLength
    }

    /**
     * 判断点是否在多边形内部
     */
    fun isPointInPolygon(latLngList: List<ILatLng>, point: ILatLng): Boolean {
        if (latLngList.size < 3) {
            return false//不是多边形
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(latLngList.size + 1)
        latLngList.forEachIndexed { index, latLng ->
            coordinates[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        coordinates[latLngList.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //多边形
        val geo = geometryFactory.createPoint(Coordinate(point.latitude, point.longitude))
        return geo.within(polygon)
    }

    /**
     * 计算三个点的夹角
     */
    fun calculateAngle(pointA: ILatLng, pointB: ILatLng, pointC: ILatLng): Double {
        // 三个点的坐标
        val point1 = Coordinate(pointA.latitude, pointA.longitude)
        val point2 = Coordinate(pointB.latitude, pointB.longitude)
        val point3 = Coordinate(pointC.latitude, pointC.longitude)

        // 计算两个向量
        val vector1 = Vector2D(point2.x - point1.x, point2.y - point1.y)
        val vector2 = Vector2D(point3.x - point2.x, point3.y - point2.y)

        // 计算向量夹角（弧度）
        val angle = vector1.angleTo(vector2)

        // 将弧度转换为度
        val angleDegrees = Math.toDegrees(angle)
        Log.e("LQZ", "弧度:$angle 角度：$angleDegrees 处理后的结果：${180 - abs(angleDegrees)}")
//        return 180 - abs(angleDegrees)
        return abs(angleDegrees)
    }

    /**
     * 多边形和线段是否相交
     */
    fun hasIntersection(polygonList: List<ILatLng>, lineList: List<ILatLng>): Boolean {
        if (polygonList.size < 3) {
            return false//不是多边形
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(polygonList.size + 1)
        polygonList.forEachIndexed { index, latLng ->
            coordinates[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        coordinates[polygonList.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //多边形

        val coordLines = arrayOfNulls<Coordinate>(lineList.size) // 线断
        lineList.forEachIndexed { index, latLng ->
            coordLines[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        if (coordLines.size < 2) {
            return false
        }
        val geoLine = geometryFactory.createLineString(coordLines)

        return geoLine.intersects(polygon)
    }

    /**
     * 是否是凸多边形 ,fixme 这个方法有问题，先放弃，后面有时间了再调试！！！
     */
    fun isConvexPolygon(polygonList: List<ILatLng>): Boolean {
        if (polygonList.size < 3) {
            return false//不是多边形
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(polygonList.size + 1)
        polygonList.forEachIndexed { index, latLng ->
            coordinates[index] = Coordinate(latLng.latitude, latLng.longitude)
        }
        coordinates[polygonList.size] = coordinates[0] //将地块围起来
        val polygon = geometryFactory.createPolygon(coordinates) //多边形

        return isConvexPol(polygon)
    }

    private fun isConvexPol(polygon: Polygon): Boolean {
        // 获取多边形的环
        val exteriorRing: LinearRing = polygon.getExteriorRing()
        val coordinates = exteriorRing.coordinates

        // 遍历多边形的内角
        for (i in 1 until coordinates.size - 1) {
            val prev = coordinates[i - 1]
            val current = coordinates[i]
            val next = coordinates[i + 1]

            // 计算内角
            val angle = Angle.angleBetweenOriented(prev, current, next)

            // 如果有任何一个内角大于180度，则多边形不是凸多边形
            if (angle < 0) {
                return false
            }
        }

        // 所有内角都小于等于180度，多边形是凸多边形
        return true
    }

    //todo 点到线段的距离
    fun getDistancePointToLine(latLngList: List<ILatLng>, point: ILatLng): Double {
        if (latLngList.size < 2) {
            return -1.0 //不是线段
        }
        val geometryFactory = GeometryFactory()
        val coordinates = arrayOfNulls<Coordinate>(latLngList.size)
        latLngList.forEachIndexed { index, latLng ->
            val mercatorPoint = MercatorProjection.toMercatorPoint(latLng)
            coordinates[index] =
                Coordinate(mercatorPoint.x, mercatorPoint.y)
        }
        val line = geometryFactory.createLineString(coordinates) //多边形
        val mercatorPoint = MercatorProjection.toMercatorPoint(point)
        val geo = geometryFactory.createPoint(
            Coordinate(
                mercatorPoint.x,
                mercatorPoint.y
            )
        )
        //点到线段的距离，左右计算的结果都不对！！！
        val distance = geo.distance(line)
        Log.e("LQZ", "点到线墨卡托距离 = $distance")
        Log.e("LQZ", "点到线实际距离 = ${distance * cos(Math.toRadians(point.latitude))}")
        return distance * cos(Math.toRadians(point.latitude))
//        return convertDegreeToMeter(geo.distance(line), point.latitude)
    }

    /**
     * 判断在线段上有没有垂足
     */
    fun isPointStroke(lineList: List<ILatLng>, point: ILatLng): Boolean {
        var temp: ILatLng? = null
        val geometryFactory = GeometryFactory()
        for (index in lineList.indices) {
            if (index == 0) {
                temp = lineList[index]
                continue
            }
            val line = geometryFactory.createLineString(
                arrayOf(
                    Coordinate(temp!!.latitude, temp.longitude),
                    Coordinate(lineList[index].latitude, lineList[index].longitude)
                )
            )
            if (perpendicularProjection(
                    geometryFactory.createPoint(
                        Coordinate(point.latitude, point.longitude)
                    ), line
                )
            ) {
                return true //有垂足，直接返回true
            }

            temp = lineList[index]
        }
        return false
    }

    // 计算点到线段的垂直投影点,并判断是否在线段上
    private fun perpendicularProjection(point: Point, lineString: LineString): Boolean {
        val lineCoordinates = lineString.coordinates
        val x1 = lineCoordinates[0].x
        val y1 = lineCoordinates[0].y
        val x2 = lineCoordinates[1].x
        val y2 = lineCoordinates[1].y
        val x0 = point.coordinate.x
        val y0 = point.coordinate.y
        val u =
            ((x0 - x1) * (x2 - x1) + (y0 - y1) * (y2 - y1)) / (lineString.length * lineString.length)
        val x = x1 + u * (x2 - x1)
        val y = y1 + u * (y2 - y1)
        val perpendicularCoordinate = Coordinate(x, y)
        val cz = point.factory.createPoint(perpendicularCoordinate)
        return lineString.isWithinDistance(cz, 0.000001)
    }
}