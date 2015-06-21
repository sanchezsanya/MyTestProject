package com.example.alexander.vkclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Alexander on 20.06.15.
 */
public class wallItemInfoActivity extends Activity
{
    private int position=0;
    WallInfo wallInfo = new WallInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wall_item_info);
        wallInfo.authorPhotoPath = getIntent().getStringExtra("authorPhotoPath");
        wallInfo.text = getIntent().getStringExtra("text");
        wallInfo.dateUnix = getIntent().getLongExtra("dateUnix", 0);
        wallInfo.likesCount = getIntent().getIntExtra("likesCount", 0);
        wallInfo.photoPath = getIntent().getStringExtra("photoPath");

        TextView itemText, itemDate, itemLikeCount;
        ImageView itemAuthorImage, itemImage;
        itemText = (TextView)findViewById(R.id.ItemText);
        itemDate = (TextView)findViewById(R.id.ItemDate);
        itemLikeCount = (TextView)findViewById(R.id.ItemLikeCount);
        itemAuthorImage = (ImageView)findViewById(R.id.ItemAuthorImage);
        itemImage = (ImageView)findViewById(R.id.ItemImage);

        itemText.setText(wallInfo.text);
        itemDate.setText(wallInfo.date());
        itemLikeCount.setText(String.valueOf(wallInfo.likesCount));

        ImageLoader il = ImageLoader.getInstance();
        il.displayImage(wallInfo.authorPhotoPath, itemAuthorImage);
        if (wallInfo.photoPath!="")
            il.displayImage(wallInfo.photoPath, itemImage);
    }
}
