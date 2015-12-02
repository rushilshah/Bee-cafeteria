package com.cmpe272.beecafeteria.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.fragments.DealsFragment;
import com.cmpe272.beecafeteria.fragments.NavigationDrawerFragment;
import com.cmpe272.beecafeteria.fragments.OrderDetailFragment;
import com.cmpe272.beecafeteria.fragments.OrdersFragment;
import com.cmpe272.beecafeteria.fragments.OutletFragment;
import com.cmpe272.beecafeteria.fragments.OutletsListFragment;
import com.cmpe272.beecafeteria.modelResponse.Order;
import com.cmpe272.beecafeteria.modelResponse.Outlet;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.OutletApiRequests;
import com.cmpe272.beecafeteria.others.SessionManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OrdersFragment.OrderFragmentCallbacks, OutletsListFragment.OutletsCallback {

    private static final String TAG = "UserActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //private BeaconManager bm;

    private PlaceManager placeManager;
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //bm = new BeaconManager();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        if (getIntent().getAction() != null) {

            if (getIntent().getAction().equals("OPEN_DEALS")) {
                onNavigationDrawerItemSelected(1);
            } else if (getIntent().getAction().equals("OPEN_OUTLETS")) {
                onNavigationDrawerItemSelected(0);
            }
        }
        setBeacon();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                setFragment(OutletsListFragment.newInstance("Outlets"), false);
                break;
            case 1:
                setFragment(DealsFragment.newInstance("Daily Deals"), false);
                break;
            case 2:
                setFragment(OrdersFragment.newInstance("My Orders"), false);
                break;
        }
        onSectionAttached(position);
    }

    @Override
    public void onOrderSelected(Order order) {
        setFragment(OrderDetailFragment.newInstance(order), true);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.nav_item_outlets);
                break;
            case 1:
                mTitle = getString(R.string.nav_item_dd);
                break;
            case 2:
                mTitle = getString(R.string.nav_item_myOrders);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                SessionManager.logoutUser(UserActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment(Fragment fragment, boolean isAdd) {

        //TODO: Unhandled for generic fragment only pop one fragment. have to handle it generic.
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.container) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAdd) {
            fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            ;
        }
        //fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }


    @Override
    public void onOutletSelected(Outlet outlet) {
        setFragment(OutletFragment.newInstance(outlet), true);
    }

    private void setBeacon() {

        Gimbal.setApiKey(this.getApplication(), "964f61f1-0440-480a-8f3d-46e944c7f212");
        placeEventListener = new PlaceEventListener() {

            @Override
            public void onVisitStart(Visit visit) {

                super.onVisitStart(visit);

                Log.d("1", String.valueOf(visit.getArrivalTimeInMillis()));
                Log.d("2", String.valueOf(visit.getDepartureTimeInMillis()));
                Log.d("3", String.valueOf(visit.getPlace()));
                Log.d("4", String.valueOf(visit.getDwellTimeInMillis()));
                /*BeaconSighting demo = new BeaconSighting();
                Beacon beac = demo.getBeacon();
                Log.d("", beac.getUuid());*/
            }

            @Override
            public void onVisitEnd(Visit visit) {
                super.onVisitEnd(visit);
                Log.d("ending", visit.getPlace().getName());
                if (visit.getPlace().getName().contains("First")) {
                    sp.edit().putBoolean("First Beacon Pref", false).apply();
                }
                if (visit.getPlace().getName().contains("Second")) {
                    sp.edit().putBoolean("Second Beacon Pref", false).apply();
                }
            }

            @Override
            public void onBeaconSighting(BeaconSighting sighting, List<Visit> visits) {
                // This will be invoked when a beacon assigned to a place within a current visit is sighted.
                //super.onBeaconSighting(sighting,visits);
                Log.d("beacon1", sighting.getBeacon().toString());
                Log.d("beacon1RSSI", sighting.getRSSI().toString());
                generateNotification(sighting);
            }

        };

        communicationListener = new CommunicationListener() {
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> collection, Visit visit) {
                return super.presentNotificationForCommunications(collection, visit);
            }

            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> collection, Push push) {
                Log.d("push", push.toString());
                return super.presentNotificationForCommunications(collection, push);
            }

            @Override
            public void onNotificationClicked(List<Communication> list) {
                for (Communication communication : list) {
                    Log.d("Communication Desc", communication.getDescription());
                    Log.d("Communication Ident", communication.getIdentifier());
                    Log.d("Communication Title", communication.getTitle());
                    Log.d("Communication URL", communication.getURL());
                    Intent intent = new Intent(UserActivity.this, UserActivity.class);
                    intent.setAction("OPEN_DEALS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

                super.onNotificationClicked(list);
            }
        };

        /*
        bm.addListener(new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {

                //super.onBeaconSighting(beaconSighting);

                Log.d("beacon1", beaconSighting.getBeacon().toString());
                Log.d("beacon1RSSI", beaconSighting.getRSSI().toString());
            }
        });*/
        placeManager = PlaceManager.getInstance();
        placeManager.addListener(placeEventListener);
        placeManager.startMonitoring();

        //bm.startListening();

        CommunicationManager.getInstance().startReceivingCommunications();
        CommunicationManager.getInstance().addListener(communicationListener);
    }

    private void generateNotification(BeaconSighting sighting) {
        if (!sp.getBoolean("First Beacon Pref", false) && sighting.getBeacon().getName().contains("First")) {
            if (sighting.getRSSI() < -30 && sighting.getRSSI() > -60) {
                getMenuNotification(sighting.getBeacon().getName());
            }
        }
        if (!!sp.getBoolean("Second Beacon Pref", false) && sighting.getBeacon().getName().contains("Second")) {
            if (sighting.getRSSI() < -30 && sighting.getRSSI() > -60) {
                getMenuNotification(sighting.getBeacon().getName());
            }
        }
    }

    private void getMenuNotification(String beaconName) {

        beaconName = beaconName.replace(" ", "%20");
        String strUser = SessionManager.getUserDetails(this).get(SessionManager.KEY_EMAIL);

        final String finalBeaconName = beaconName;
        final GsonPostRequest gsonPostRequest =
                OutletApiRequests.postNotificationRequest
                        (
                                new Response.Listener<PostResponse>() {
                                    @Override
                                    public void onResponse(PostResponse dummyObject) {

                                        if (finalBeaconName.contains("First")) {
                                            sp.edit().putBoolean("First Beacon Pref", true).apply();
                                        } else if (finalBeaconName.contains("Second")) {
                                            sp.edit().putBoolean("Second Beacon Pref", true).apply();
                                        }

                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                },
                                beaconName, strUser
                        );

        App.addRequest(gsonPostRequest, TAG);
    }
}
