package com.example.alexander.vkclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Alexander on 19.06.15.
 */
public class WallAdapter extends ArrayAdapter<WallInfo> {
    Context context;
    int layoutResourceId;
    List<WallInfo> data  = null;
    ImageLoader il = ImageLoader.getInstance();
    LayoutInflater inflater;


    public WallAdapter(Context context, int layoutResourceId, List<WallInfo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item = convertView;
        WallHolder holder = null;

        if(item == null)
        {


            item = inflater.inflate(layoutResourceId, parent, false);

            holder = new WallHolder();
            holder.imageViewImageAuthor = (ImageView)item.findViewById(R.id.imageViewImageAuthor);
            holder.textViewItemMessage = (TextView)item.findViewById(R.id.textViewItemMessage);
            holder.textViewPostDate = (TextView)item.findViewById(R.id.textViewPostDate);
            item.setTag(holder);
        }
        else
        {
            holder = (WallHolder)item.getTag();
        }

        WallInfo wall = data.get(position);
        il.displayImage(wall.authorPhotoPath,holder.imageViewImageAuthor);
        holder.textViewItemMessage.setText(wall.text);
        holder.textViewPostDate.setText(wall.date());

        return item;
    }

    static class WallHolder
    {
        ImageView imageViewImageAuthor;
        TextView textViewItemMessage;
        TextView textViewPostDate;
    }
}
