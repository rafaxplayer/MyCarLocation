package com.mycarlocation.polylines;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mycarlocation.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserJSONTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private GoogleMap map;
    private ProgressBar prog;

    // Parsing the data in non-ui thread
    public ParserJSONTask(GoogleMap map, ProgressBar pro) {
        pro.setVisibility(View.VISIBLE);
        this.prog = pro;
        this.map = map;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(5);
            lineOptions.color(R.color.colorAccent);
            lineOptions.geodesic(true);

        }
        if (lineOptions != null) {
            map.addPolyline(lineOptions);
        }

        if (prog != null) {
            prog.setVisibility(View.GONE);
        }
    }
}
