package com.mycarlocation;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mycarlocation.polylines.DownloadUrlTask;

import static com.mycarlocation.GlobalUttilities.buildAlertMessageNoGps;
import static com.mycarlocation.GlobalUttilities.getDirectionsUrl;
import static com.mycarlocation.GlobalUttilities.getLocationString;
import static com.mycarlocation.GlobalUttilities.limpiarMapa;
import static com.mycarlocation.GlobalUttilities.mostrarMarcador;
import static com.mycarlocation.GlobalUttilities.ocultateclado;
import static com.mycarlocation.GlobalUttilities.setToolBar;
import static com.mycarlocation.GlobalUttilities.updateLocation;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnConnectionFailedListener, ConnectionCallbacks {
    private Toolbar toolbar;
    private static String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private DrawerLayout drawerLayout;
    private Marker marker;
    private ProgressBar loadprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setNavigatorDrawner(navigationView);
        setToolBar(this, toolbar);
        loadprogress=(ProgressBar) findViewById(R.id.progressLoad);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void onclick(View v) {
        if (v.getId() == R.id.buttonAddMarker) {

            CameraPosition camPos = map.getCameraPosition();
            LatLng coordenadas = camPos.target;
            double latitud = coordenadas.latitude;
            double longitud = coordenadas.longitude;
            String strAdress = getLocationString(getApplicationContext(), latitud, longitud);
            Marker marker = mostrarMarcador(getApplicationContext(), map, latitud, longitud, strAdress, false, R.drawable.ic_location_car);
            this.marker = marker;
        } else if (v.getId() == R.id.buttonRemoveMarker) {
            limpiarMapa(map);

        }

    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.setMapType(Integer.parseInt(prefs.getString("map_type", "1")));
        map.setMyLocationEnabled(true);
        if (!GlobalUttilities.checkGPS(getApplicationContext())) {
            buildAlertMessageNoGps(this);
        }
        Location loc = map.getMyLocation();

        if (loc != null) {
            LatLng LatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 13));
        }
        this.map = map;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ocultateclado(this);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private void setNavigatorDrawner(NavigationView nav) {
        if (nav != null) {
            nav.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {

                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {

                            menuItem.setChecked(true);

                            switch (menuItem.getItemId()) {
                                case R.id.nav_history:
                                    startActivity(new Intent(getApplicationContext(), Hystory_Activity.class));
                                    break;
                                case R.id.nav_navigator:
                                    if (marker != null) {
                                        LatLng loc = marker.getPosition();
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr=" + map.getMyLocation().getLatitude() + "," + map.getMyLocation().getLongitude() + "&daddr=" + loc.latitude + "," + loc.longitude));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "No hay posiciones guardadas", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case R.id.nav_car_position:
                                    if (marker != null) {
                                        LatLng source = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                                        updateLocation(marker.getPosition(), map, 19, 45, 60);
                                        String url = getDirectionsUrl(source, marker.getPosition());
                                        DownloadUrlTask downloadTask = new DownloadUrlTask(MainActivity.this, map,loadprogress);

                                        downloadTask.execute(url);

                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "No hay marcador", Toast.LENGTH_LONG).show();
                                    }
                                    break;

                            }
                            drawerLayout.closeDrawers();
                            return true;
                        }
                    }
            );
        }
    }
}
