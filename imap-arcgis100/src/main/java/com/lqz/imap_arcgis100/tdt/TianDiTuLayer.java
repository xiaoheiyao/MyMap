package com.lqz.imap_arcgis100.tdt;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.ImageTiledLayer;
import com.lqz.tianditu.TianDiTuConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiang on 2017/4/17.
 */

public final class TianDiTuLayer extends ImageTiledLayer {
    private TianDiTuLayerInfo layerInfo;

    private Context context;

    public TianDiTuLayer(TileInfo tileInfo, Envelope fullExtent, Context context) {
        super(tileInfo, fullExtent);
        this.context = context;
    }


    public void setLayerInfo(TianDiTuLayerInfo layerInfo) {
        this.layerInfo = layerInfo;
    }

    public TianDiTuLayerInfo getLayerInfo() {
        return this.layerInfo;
    }

    @Override
    protected byte[] getTile(TileKey tileKey) {
        int level = tileKey.getLevel();
        int col = tileKey.getColumn();
        int row = tileKey.getRow();
        try {
            return getTile(level, col, row);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private byte[] getTile(int level, int col, int row) throws Exception {
        byte[] tileImage = null;
        if (layerInfo != null) {
            if (level > layerInfo.getMaxZoomLevel()
                    || level < layerInfo.getMinZoomLevel())
                return new byte[0];
            String path0 = layerInfo.getUrl()
                    + "?service=wmts&request=gettile&version=1.0.0&layer="
                    + layerInfo.getLayerName() + "&format=tiles&tilematrixset="
                    + layerInfo.getTileMatrixSet() + "&tilecol=" + col
                    + "&tilerow=" + row + "&tilematrix=" + (level) + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a";

//                String path = "https://t1.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default" +
//                        "&TILEMATRIXSET=w" +
//                        "&FORMAT=png" +
//                        "&TILEMATRIX=" + level +
//                        "&TILEROW=" + row +
//                        "&TILECOL=" + col + "&tk=f60c12fad90f2fbb624a1b8fb11d8f7a";

            String path = layerInfo.getUrl() + "SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default" +
                    "&TILEMATRIXSET=w" +
//                    "&FORMAT=png" +
                    "&FORMAT=tiles" +
                    "&TILEMATRIX=" + level +
                    "&TILEROW=" + row +
                    "&TILECOL=" + col + "&tk=" + TianDiTuConstants.KEY;
            Log.e("LQZ", "url = " + path);
            return lqz(path);
//            URL url = new URL(path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5000);
//            //获取服务器返回回来的流
//            InputStream is = conn.getInputStream();
//            tileImage = getBytes(is);
        }
//        return tileImage;
        return null;
    }


    private byte[] lqz(String url) {
        Headers headers = new LazyHeaders.Builder().build();
        GlideUrl glideUrl = new GlideUrl(url,
                headers);

        FutureTarget<File> submit;

        submit = Glide.with(context).asFile().load(glideUrl).listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                return false;
            }
        }).submit(256, 256);

        try {
            return file2Bytes(submit.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] file2Bytes(File file) {
        if (file == null) {
            return null;
        }
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        is.close();
        bos.flush();
        byte[] result = bos.toByteArray();
        System.out.println(new String(result));
        return result;
    }
}
