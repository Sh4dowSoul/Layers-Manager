package com.lovejoy777.rroandlayersmanager.activities;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.lovejoy777.rroandlayersmanager.Backup;
import com.lovejoy777.rroandlayersmanager.Delete;
import com.lovejoy777.rroandlayersmanager.Install;
import com.lovejoy777.rroandlayersmanager.PlaystoreSuperUser;
import com.lovejoy777.rroandlayersmanager.R;
import com.lovejoy777.rroandlayersmanager.Restore;
import com.lovejoy777.rroandlayersmanager.adapters.CardViewAdapter;
import com.lovejoy777.rroandlayersmanager.helper.CardViewContent;
import com.lovejoy777.rroandlayersmanager.helper.RecyclerItemClickListener;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity
{

    public static final String ACTION_PICK_PLUGIN = "androidsrc.intent.action.PICK_PLUGIN";
    static final String KEY_PKG = "pkg";
    static final String KEY_SERVICENAME = "servicename";
    static final String KEY_ACTIONS = "actions";
    static final String KEY_CATEGORIES = "categories";
    static final String BUNDLE_EXTRAS_CATEGORY = "category";
    static final String BUNDLE_EXTRAS_PACKAGENAME = "packageName";
    private Boolean TestBoolean = false;
    static final String LOG_TAG = "PluginApp";
    private DrawerLayout mDrawerLayout;
    private RecyclerView recyclerCardViewList = null;
    RecyclerView recList = null;
    CardViewAdapter ca = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerCardViewList = (RecyclerView) findViewById(R.id.cardList);
        recyclerCardViewList.setHasFixedSize(true);
        recyclerCardViewList.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onListItemClick(position);
                    }
                })
        );
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerCardViewList.setLayoutManager(llm);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuactivity = new Intent(MainActivity.this, Install.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(menuactivity, bndlanimation);
            }


        });

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }


        // IF ROOT ACCESS IS GIVEN / ELSE LAUNCH PLAYSTORE FOR SUPERUSER APP
        if (!RootTools.isAccessGiven()) {
            Toast.makeText(MainActivity.this, "Your device doesn't seem to be rooted", Toast.LENGTH_LONG).show();
            Intent intent0 = new Intent();
            intent0.setClass(this, PlaystoreSuperUser.class);
            startActivity(intent0);
            finish();

        } else {

            String sdOverlays = Environment.getExternalStorageDirectory() + "/Overlays";
            String sdcard = Environment.getExternalStorageDirectory() + "";

            RootTools.remount(sdcard, "RW");

            // CREATES /SDCARD/OVERLAYS
            File dir = new File(sdOverlays);
            if (!dir.exists() && !dir.isDirectory()) {
                CommandCapture command3 = new CommandCapture(0, "mkdir " + sdOverlays);
                try {
                    RootTools.getShell(true).add(command3);
                    while (!command3.isFinished()) {
                        Thread.sleep(1);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (RootDeniedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

                String sdOverlays1 = Environment.getExternalStorageDirectory() + "/Overlays/Backup";
                // CREATES /SDCARD/OVERLAYS/BACKUP
                File dir1 = new File(sdOverlays1);
                if (!dir1.exists() && !dir1.isDirectory()) {
                    CommandCapture command4 = new CommandCapture(0, "mkdir " + sdOverlays1);
                    try {
                        RootTools.getShell(true).add(command4);
                        while (!command4.isFinished()) {
                            Thread.sleep(1);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (RootDeniedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                RootTools.remount("/system", "RW");
                String vendover = "/vendor/overlay";

                // CREATES /VENDOR/OVERLAY
                File dir2 = new File(vendover);
                if (!dir2.exists() && !dir2.isDirectory()) {
                    CommandCapture command5 = new CommandCapture(0, "mkdir " + vendover);
                    try {
                        RootTools.getShell(true).add(command5);
                        while (!command5.isFinished()) {
                            Thread.sleep(1);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (RootDeniedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
        fillPluginList();

        final SwipeRefreshLayout mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setColorSchemeResources(R.color.accent);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                services.clear();
                fillPluginList();
                onItemsLoadComplete();
            }

            void onItemsLoadComplete(){
                ca.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }
        });
        packageBroadcastReceiver = new PackageBroadcastReceiver();
        packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
        packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
        packageFilter.addCategory( Intent.CATEGORY_DEFAULT );
        packageFilter.addDataScheme( "package" );


    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_about:
                                Intent menuactivity4 = new Intent(MainActivity.this, AboutActivity.class);
                                Bundle bndlanimation4 =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(menuactivity4, bndlanimation4);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_delete:
                                Intent menuactivity = new Intent(MainActivity.this, Delete.class);
                                Bundle bndlanimation =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(menuactivity, bndlanimation);
                                break;
                            case R.id.nav_backup:
                                Intent backup = new Intent(MainActivity.this, Backup.class);
                                Bundle bndlanimation2 =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(backup, bndlanimation2);
                                break;
                            case R.id.nav_restore:
                                Intent menuactivity2 = new Intent(MainActivity.this, Restore.class);

                                Bundle bndlanimation3 =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(menuactivity2, bndlanimation3);
                        }

                        return false;
                    }
        });
    }




    private List createList(int size, String name[], String developer[], String packages[]) {

        List result = new ArrayList();
        Drawable Image[] = new Drawable[size];
        for (int i=1; i <= size; i++) {
            CardViewContent ci = new CardViewContent();
            ci.themeName = name[i-1];
            ci.themeDeveloper = developer[i-1];

            int j = i + 1;
            final String packName = packages[i-1];
            String mDrawableName = "icon";
            PackageManager manager = getPackageManager();
            Resources mApk1Resources = null;
            try {
                mApk1Resources = manager.getResourcesForApplication(packName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            int mDrawableResID = mApk1Resources.getIdentifier(mDrawableName, "drawable", packName);
            Drawable myDrawable = mApk1Resources.getDrawable(mDrawableResID);
            Image[i-1] = myDrawable;

            ci.themeImage = myDrawable ;

            result.add(ci);
        }
        return result;
    }

    private List createList2(int size, String[] message1, String[] message2) {

        List result = new ArrayList();
        Drawable Image[] = new Drawable[size];
        Drawable myDrawable;
        for (int i=1; i <= size; i++) {
            CardViewContent ci = new CardViewContent();
            ci.message1 = message1[i-1];
            ci.message2 = message2[i-1];
            Image[0] = getResources().getDrawable(R.drawable.toobad);
            Image[1] = getResources().getDrawable(R.drawable.ic_launcher);
            Image[2] = getResources().getDrawable(R.drawable.playstore);
            myDrawable = Image[i-1];
            ci.themeImage = myDrawable ;
            result.add(ci);
        }
        return result;
    }



    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        registerReceiver( packageBroadcastReceiver, packageFilter );
    }

    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        unregisterReceiver( packageBroadcastReceiver );
    }

    protected void onListItemClick (int position) {
        if (!TestBoolean){
            String package2 = packages[position];
            String category = categories.get(position);
            if( category.length() > 0 ) {

                Intent intent = new Intent(MainActivity.this, Delete.class);
                intent.setClassName("com.lovejoy777.rroandlayersmanager",
                        "com.lovejoy777.rroandlayersmanager.activities.OverlayDetailActivity");
                intent.putExtra(BUNDLE_EXTRAS_CATEGORY, category);
                intent.putExtra(BUNDLE_EXTRAS_PACKAGENAME, package2);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);

            }
        }
        else{
            if(position==2) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=Layers+Theme&c=apps&docType=1&sp=CAFiDgoMTGF5ZXJzIFRoZW1legIYAIoBAggB:S:ANO1ljK_ZAY")));
            } if(position==1){
                NotAvailableSnackbar();
            }

        }
    }

    private void fillPluginList()  {

        services = new ArrayList<HashMap<String,String>>();
        categories = new ArrayList<String>();

        PackageManager packageManager = getPackageManager();
        Intent baseIntent = new Intent( ACTION_PICK_PLUGIN );
        baseIntent.setFlags( Intent.FLAG_DEBUG_LOG_RESOLUTION );
        List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,
                PackageManager.GET_RESOLVED_FILTER );

        final String name[] = new String[list.size()];
        final String developer[] = new String[list.size()];


        for( int i = 0 ; i < list.size() ; ++i ) {

            ResolveInfo info = list.get( i );
            ServiceInfo sinfo = info.serviceInfo;
            IntentFilter filter = info.filter;
            Log.d(LOG_TAG, "fillPluginList: i: " + i + "; sinfo: " + sinfo + ";filter: " + filter);

            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(sinfo.packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Bundle bundle = ai.metaData;
            name[i] = bundle.getString("Layers_Name");
            developer[i] = bundle.getString("Layers_Developer");

            if( sinfo != null ) {
                HashMap<String,String> item = new HashMap<String,String>();
                item.put( KEY_PKG, name[i] );
                item.put( KEY_SERVICENAME, developer[i] );

                String firstCategory = null;
                if( filter != null ) {
                    StringBuilder actions = new StringBuilder();
                    for( Iterator<String> actionIterator = filter.actionsIterator() ; actionIterator.hasNext() ; ) {
                        String action = actionIterator.next();
                        if( actions.length() > 0 )
                            actions.append( "," );
                        actions.append( action );
                    }
                    StringBuilder categories = new StringBuilder();
                    for( Iterator<String> categoryIterator = filter.categoriesIterator() ;
                         categoryIterator.hasNext() ; ) {
                        String category = categoryIterator.next();
                        if( firstCategory == null )
                            firstCategory = category;
                        if( categories.length() > 0 )
                            categories.append( "," );
                        categories.append( category );
                    }
                    try {
                        packages[i] = getPackageManager().getApplicationInfo(sinfo.packageName,0).packageName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    item.put(KEY_ACTIONS, "<null>");
                    item.put(KEY_CATEGORIES, "<null>");
                }
                if( firstCategory == null )
                    firstCategory = "";
                categories.add( firstCategory );
                services.add( item );
            }
        }
        String Test1[] = new String[3];
        String Test2[] = new String[3];

        Test1[0] = "Too bad.";
        Test2[0] = "It seems like you haven´t installed any compatibel theme yet.";
        Test1[1] = "Layers Theme Showcase";
        Test2[1] = "Find some new, beautiful themes in the all new Layers Showcase App.";
        Test1[2] = "Play Store";
        Test2[2] = "Have a look at the PlayStore.";

        if (list.size()>0) {
            ca = new CardViewAdapter(createList(list.size(), name, developer, packages));
        }else {
            ca = new CardViewAdapter(createList2(3, Test1, Test2));
            TestBoolean = true;
        }
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        recList.setAdapter(ca);
    }




    private PackageBroadcastReceiver packageBroadcastReceiver;
    private IntentFilter packageFilter;
    private ArrayList<HashMap<String,String>> services;
    private ArrayList<String> categories;
    private SimpleAdapter itemAdapter;
    private String[] packages = new String[100];

    class PackageBroadcastReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "PackageBroadcastReceiver";

        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive: " + intent);
            services.clear();
            fillPluginList();

            ca.notifyDataSetChanged();
            System.out.println("TEST");
        }
    }


    private void NotAvailableSnackbar() {

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Sry, not available yet.", Snackbar.LENGTH_SHORT)
                .show();
    }
}
