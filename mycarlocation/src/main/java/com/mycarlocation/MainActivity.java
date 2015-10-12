package com.mycarlocation;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mycarlocation.classes.BottomSheetHelper;
import com.mycarlocation.classes.GlobalUttilities;
import com.mycarlocation.classes.Installation;
import com.mycarlocation.polylines.DownloadUrlTask;

import me.drakeet.materialdialog.MaterialDialog;

import static com.mycarlocation.classes.GlobalUttilities.buildAlertMessageNoGps;
import static com.mycarlocation.classes.GlobalUttilities.getDirectionsUrl;
import static com.mycarlocation.classes.GlobalUttilities.getLocationString;
import static com.mycarlocation.classes.GlobalUttilities.limpiarMapa;
import static com.mycarlocation.classes.GlobalUttilities.mostrarMarcador;
import static com.mycarlocation.classes.GlobalUttilities.ocultateclado;
import static com.mycarlocation.classes.GlobalUttilities.setToolBar;
import static com.mycarlocation.classes.GlobalUttilities.updateLocation;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;
    private GoogleMap map;

    private DrawerLayout drawerLayout;
    private Marker marker;
    private ActionBarDrawerToggle mDrawerToggle;
    private SupportMapFragment mapFragment;

    private Firebase myFirebaseRef;
    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ID=new Installation().id(this);
        myFirebaseRef = new Firebase("https://mycarlocation.firebaseio.com/"+ID+"/locations");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setNavigatorDrawner(navigationView);
        setToolBar(this,true);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void onclick(View v) {
        if (v.getId() == R.id.buttonAddMarker) {

            CameraPosition camPos = map.getCameraPosition();
            LatLng coordenadas = camPos.target;

            double latitud = coordenadas.latitude;
            double longitud = coordenadas.longitude;
            final String strAdress = getLocationString(getApplicationContext(), latitud, longitud);
            Marker marker = mostrarMarcador(getApplicationContext(), map, latitud, longitud, strAdress, false, R.drawable.ic_location_car);
            this.marker = marker;
            final LatLng loc=marker.getPosition();

            updateLocation(loc, map, 19, 45, 60);

            final MaterialDialog materialdialognow = new MaterialDialog(this);
            materialdialognow.setTitle(getString(R.string.location))
                    .setMessage(getString(R.string.question_save_location))
                    .setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobalUttilities.firebaseSetLocation(getApplicationContext(),loc,myFirebaseRef);
                            if (materialdialognow != null)
                                materialdialognow.dismiss();
                        }
                    })
                    .setNegativeButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialdialognow.dismiss();
                        }
                    });
            materialdialognow.show();

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
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                markerMenuCreate(marker);
            }
        });

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
    public void onPause() {
        super.onPause();

    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void markerMenuCreate(final Marker marker) {
        new BottomSheet.Builder(this)
                .title(getString(R.string.actions_marker))
                .sheet(R.menu.menu_action_marker)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.share_location:
                                if (marker != null) {
                                    dialog.dismiss();
                                    LatLng loc = marker.getPosition();

                                    String uri = "http://maps.google.com/?saddr=" + loc.latitude
                                            + "," + loc.longitude;
                                    shareLocationMenuCreate(uri);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "No hay posiciones guardadas", Toast.LENGTH_LONG).show();
                                }

                                break;
                            case R.id.delete_location:
                                limpiarMapa(map);
                                break;
                            case R.id.navigator_use:
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
                        }
                    }
                }).show();
    }

    private void shareLocationMenuCreate(String text) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        BottomSheetHelper.shareAction(this, shareIntent).title("Compatir con :").show();

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
                                        DownloadUrlTask downloadTask = new DownloadUrlTask(MainActivity.this, map);

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
