package com.mycarlocation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
// global uttilities
public class GlobalUttilities {
    private static String TAG = GlobalUttilities.class.getSimpleName();


    public static void setToolBar(AppCompatActivity act, Toolbar toolbar) {
        if (toolbar != null) {
            act.setSupportActionBar(toolbar);
            act.getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            act.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void showSimpleAlert(String msg, Context con) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(con);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void buildAlertMessageNoGps(final Context con) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(con);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        con.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean checkGPS(Context con) {
        LocationManager locationManager = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public static void ocultateclado(Activity act) {
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public static Marker addmaker(LatLng latlong, GoogleMap map, String adress, String title, int ico) {
        Marker marker = null;
        try {
            marker = map.addMarker(new MarkerOptions()
                    .position(latlong)
                    .icon(BitmapDescriptorFactory.fromResource(ico))
                    .snippet(adress)
                    .title(title));
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        return marker;
    }

    public static Marker mostrarMarcador(Context con, GoogleMap map, double lat, double lng, String adress, boolean registered, int ico) {
       Marker marker=null;
        try {
           limpiarMapa(map);
           marker= map.addMarker(new MarkerOptions()
                   .position(new LatLng(lat, lng))
                   .icon(BitmapDescriptorFactory
                           .fromResource(ico))
                   .snippet(adress)
                   .title("Mi coche esta aqui!"));
       }catch (Exception ex){
           Log.e("Error", ex.getMessage());
       }
        return marker;
    }

    public static void limpiarMapa(GoogleMap map) {
        if (map != null) {
            map.clear();

        }

    }

    public static String getLocationString(Context con, double currentLatitude, double currentLongitude) {

        String strAdress = "No hay informacion";
        List<Address> addresses = null;
        try {
            Geocoder gcd = new Geocoder(con, Locale.getDefault());
            addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 10);
            if (addresses.size() > 0 && addresses != null) {
                String feature = addresses.get(0).getFeatureName() == null ? ""
                        : addresses.get(0).getFeatureName();
                String locality = addresses.get(0).getLocality() == null ? ""
                        : addresses.get(0).getLocality();
                String thoroughfare = addresses.get(0).getThoroughfare() == null ? ""
                        : addresses.get(0).getThoroughfare();
                String country = addresses.get(0).getCountryName() == null ? ""
                        : addresses.get(0).getCountryName();

                strAdress = locality + ", "
                        + feature + ", "
                        + thoroughfare + ", "
                        + country;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());
        }

        return strAdress;
    }

    public static String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    public static void updateLocation(LatLng location, GoogleMap map, int zoom, int bearing, int tilt) {
        try {
            CameraPosition camPos = new CameraPosition.Builder().target(location)
                    .zoom(zoom).bearing(bearing).tilt(tilt).build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            map.animateCamera(camUpd3);
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }

    }


}
