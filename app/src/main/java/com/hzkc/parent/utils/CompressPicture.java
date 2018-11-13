package com.hzkc.parent.utils;

/**
 * Created by lenovo-s on 2017/4/24.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lfh on 2017/1/24.
 */
public class CompressPicture {

    /** 处理图片-比例压缩，返回存储路径
     * @param filePath 图片路径
     * @param width 需要的宽
     * @param height 需要的高
     * @return 返回文件路径
     */
    public static String dealPicture(String filePath, float width, float height, String suffix){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int sampleSize = 1;
        if (outWidth/outHeight > width/height) {
            sampleSize = (int) (outWidth/width);
        }else{
            sampleSize = (int) (outHeight/height);
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        String path = compressImage(bitmap,suffix,filePath);
        return path;
    }
    /**
     * 将图片质量压缩在100KB内，并返回存储路径
     * @param bitmap 需要压缩的bitmap
     * @param suffix 存储的文件后缀
     * @return 返回文件路径
     */
    public static String compressImage(Bitmap bitmap, String suffix, String filePath){
        String newFilePath;
        if (filePath.contains("pic")){
            newFilePath = filePath;
        }else{
            newFilePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/HongJing/pic"+"/"+System.currentTimeMillis()+"."+suffix;
        }
        File outFile = new File(newFilePath);
        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();
        }else{
            return outFile.getAbsolutePath();
        }
        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            while (bos.toByteArray().length/1024 > 400 && quality >= 50) {
                bos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                quality -= 5;
            }
            byte[] array = bos.toByteArray();
            fos.write(array);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile.getAbsolutePath();
    }
}

