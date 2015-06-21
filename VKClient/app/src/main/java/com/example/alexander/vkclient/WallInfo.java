package com.example.alexander.vkclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alexander on 18.06.15.
 */
public class WallInfo {
    public int id;
    public long dateUnix;
    public String text;
    public int likesCount;
    public String attachmentsType;
    public String photoPath;
    public int authorId;
    public String authorPhotoPath;
    public Bitmap bitmapImageAuthor;
    DateFormat df;

    public String date()
    {

        df = new SimpleDateFormat("dd.MM.yyyy");
        String strDate = df.format(new Date(dateUnix*1000));
        return strDate;
    }
}
