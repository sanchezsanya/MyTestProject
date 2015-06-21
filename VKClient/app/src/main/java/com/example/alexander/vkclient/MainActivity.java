package com.example.alexander.vkclient;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKObject;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import com.vk.sdk.util.VKJsonHelper;
import com.vk.sdk.util.VKUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    private static final String VK_APP_ID = "4959707";
    Thread thread;

    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            startLoading();
        }
    };
    private VKRequest currentRequest;
    SlidingMenu menu;
    List<WallInfo> wallInfo = new ArrayList<WallInfo>();
    private ListView listView1;
    WallAdapter wallAdapter;
    WallInfo temp;
    Context cont;
    Button buttonRefresh;
    View header;
    LinearLayout mLoadingFooter;
    int offset=0;
    private static String[] sMyScope = new String[]{VKScope.WALL, VKScope.NOTES, VKScope.PAGES};

    private final VKSdkListener sdkListener = new VKSdkListener() {

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken " + token);
            thread = new Thread(null,doBackgroundThreadProcessing,"myBackProc");
            thread.start();
            String[] items2 = {"Стена","Выйти"};
            ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                    new ArrayAdapter<Object>(
                            getApplicationContext(),
                            R.layout.sidemenu_item,
                            R.id.text,
                            items2
                    )
            );
            //startLoading();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
            thread = new Thread(null,doBackgroundThreadProcessing,"myBackProc");
            thread.start();
            //startLoading();
            String[] items2 = {"Стена","Выйти"};
            ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                    new ArrayAdapter<Object>(
                            getApplicationContext(),
                            R.layout.sidemenu_item,
                            R.id.text,
                            items2
                    )
            );
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken " + token);
            thread = new Thread(null,doBackgroundThreadProcessing,"myBackProc");
            thread.start();
            String[] items2 = {"Стена","Выйти"};
            ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                    new ArrayAdapter<Object>(
                            getApplicationContext(),
                            R.layout.sidemenu_item,
                            R.id.text,
                            items2
                    )
            );
            //startLoading();
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.d("VkDemoApp", "onCaptchaError " + captchaError);
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.d("VkDemoApp", "onTokenExpired " + expiredToken);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Log.d("VkDemoApp", "onAccessDenied " + authorizationError);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sidemenu);
        menu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);
        String[] items = {"Стена",getString(R.string.button_exit)};
        ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                new ArrayAdapter<Object>(
                        this,
                        R.layout.sidemenu_item,
                        R.id.text,
                        items
                )
        );

        ListView menuList = (ListView)findViewById(R.id.sidemenu);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuToggle();
                if (position==1)
                {
                    if (VKSdk.isLoggedIn())
                        VKSdk.logout();
                    VKSdk.authorize(sMyScope, true, true);
                    wallInfo.clear();
                    wallAdapter.clear();
                    String[] items2 = {"Стена","Войти"};
                    ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                            new ArrayAdapter<Object>(
                                    getApplicationContext(),
                                    R.layout.sidemenu_item,
                                    R.id.text,
                                    items2
                            )
                    );
                }
            }
        });

        VKSdk.initialize(sdkListener, VK_APP_ID);
        VKUIHelper.onCreate(this);
        VKSdk.authorize(String.format(VKScope.WALL + " " + VKScope.NOTES + " " + VKScope.PAGES));
        header = getLayoutInflater().inflate(R.layout.listview_header, null);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoadingFooter = (LinearLayout) layoutInflater.inflate(R.layout.listview_footer, null);
        wallAdapter = new WallAdapter(this,R.layout.listview_wall_item,wallInfo);
        listView1 = (ListView)findViewById(R.id.myList);
        listView1.addFooterView(mLoadingFooter);
        listView1.addHeaderView(header);
        listView1.setAdapter(wallAdapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!thread.isAlive()) {
                    listView1.deferNotifyDataSetChanged();
                    Intent intent = new Intent(MainActivity.this, wallItemInfoActivity.class);
                    intent.putExtra("authorPhotoPath", wallInfo.get(position - 1).authorPhotoPath);
                    intent.putExtra("text", wallInfo.get(position - 1).text);
                    intent.putExtra("dateUnix", wallInfo.get(position - 1).dateUnix);
                    intent.putExtra("likesCount", wallInfo.get(position - 1).likesCount);
                    intent.putExtra("photoPath", wallInfo.get(position - 1).photoPath);
                    startActivity(intent);
                }
            }
        });
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && !thread.isAlive()) {
                    thread = new Thread(null,doBackgroundThreadProcessing,"myBackProc");
                    thread.start();
                }
            }
        });

        cont = this;

        buttonRefresh = (Button)findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thread.isAlive())
                wallInfo.clear();
                offset=0;
                wallAdapter.notifyDataSetChanged();
                thread = new Thread(null,doBackgroundThreadProcessing,"myBackProc");
                thread.start();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menuToggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void menuToggle(){
        if(menu.isMenuShowing())
            menu.showContent();
        else
            menu.showMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        if (currentRequest != null) {
            currentRequest.cancel();
        }
    }


    private void startLoading() {
        final boolean[] flag = {true};
        final int[] count = {0};
        if (currentRequest != null) {
            currentRequest.cancel();
        }
        currentRequest = VKApi.wall().get(VKParameters.from(VKApiConst.COUNT,20, VKApiConst.OFFSET,offset*20));
        temp = new WallInfo();
        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    count[0] = response.json.getJSONObject("response").getJSONArray("items").length();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                for (int i = 0; i < count[0]; i++) {
                    temp = new WallInfo();
                    try {

                        temp.id = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getInt("id");
                        temp.dateUnix = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getInt("date");
                        temp.text = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getString("text");
                        temp.likesCount = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getJSONObject("likes").getInt("count");
                        temp.authorId = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getInt("from_id");
                        if (response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).has("attachments")) {
                            temp.attachmentsType = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getJSONArray("attachments").getJSONObject(0).getString("type");
                            if (temp.attachmentsType.equals("photo"))
                                temp.photoPath = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(i).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_604");
                        }
                        //wallInfo.add(temp);
                        wallAdapter.add(temp);
                    } catch (JSONException e) {
                        //wallInfo.add(temp);
                        wallAdapter.add(temp);
                    }
                }
                flag[0]=false;
            }

            @Override
            public void onError(VKError error) {
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            }
        });

        while(flag[0]) {
            try {
                thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        offset+=1;

        String usersIds = "";
        for (int i=0;i<count[0];i++)
        {
            usersIds += String.valueOf(wallInfo.get(i).authorId);
            if (i!=count[0]-1)
                usersIds+=",";
        }
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,usersIds,VKApiConst.FIELDS,"photo_100"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response2) {
                for (int i = 0; i < count[0]; i++) {
                    try {
                        wallInfo.get(i).authorPhotoPath = response2.json.getJSONArray("response").getJSONObject(0).getString("photo_100");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                wallAdapter.notifyDataSetChanged();
                flag[0] = true;
            }

            @Override
            public void onError(VKError error) {
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            }
        });

        while(!flag[0])
        {
            try {
                thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



