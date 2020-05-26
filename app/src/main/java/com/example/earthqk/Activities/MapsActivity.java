package com.example.earthqk.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthqk.R;
import com.example.earthqk.models.EarthQuakes;
import com.example.earthqk.ui.coustom_info_window;
import com.example.earthqk.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RequestQueue requestQueue;

    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private BitmapDescriptor[] iconColors;
    private Button showlist;





    private GoogleMap mMap;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        iconColors = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                // BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)


        };
        showlist = findViewById(R.id.showtxt);
        showlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ShowList.class));
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestQueue = Volley.newRequestQueue(this);
        getEarthQk();

            }

    private void getEarthQk() {
        final EarthQuakes earthQuakes = new EarthQuakes();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , Constants.url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray features = response.getJSONArray("features");
                    for (int i = 0; i < Constants.LIMIT; i++) {
                        JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                        Log.d("place", properties.getString("place"));


                        //Get geomety object
                        JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");

                        //get coordinates array
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);

                        earthQuakes.setPlace(properties.getString("place"));
                        earthQuakes.setLat(lat);
                        earthQuakes.setLon(lon);
                        earthQuakes.setDetailLink(properties.getString("detail"));
                        earthQuakes.setType(properties.getString("type"));
                        earthQuakes.setMeg(properties.getDouble("mag"));
                        earthQuakes.setTime(properties.getLong("time"));
                        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                        String formatedDate = dateFormat.format(new Date(
                                Long.valueOf(properties.getLong("time"))).getTime());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.icon(iconColors[Constants.randomInt(iconColors.length,0)]);
                        markerOptions.position(new LatLng(lon,lat));
                        markerOptions.title(earthQuakes.getPlace());
                        markerOptions.snippet("Magnitude: " + earthQuakes.getMeg()+ "/n" + "Date: " + formatedDate);
                        // red color change
                        if(earthQuakes.getMeg()>3) {
                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.center(new LatLng(earthQuakes.getLat(), earthQuakes.getLon()));
                            circleOptions.radius(3000);
                            circleOptions.strokeWidth(3.6f);
                            circleOptions.fillColor(Color.RED);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            mMap.addCircle(circleOptions);
                        }

                            Marker marker = mMap.addMarker(markerOptions);
                            marker.setTag(earthQuakes.getDetailLink());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lon, lat), 1));




                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        requestQueue.add(jsonObjectRequest);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(String.valueOf(locationManager), 0, 0, locationListener);

            }


            }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new coustom_info_window(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this,
                                                  Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                mMap.addMarker(new MarkerOptions()
                                       .position(latLng)
                                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                       .title("Hello"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

            }
        }



    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        getQuakeDetail(marker.getTag().toString());
       //Toast.makeText(getApplicationContext(),marker.getTitle().toString(),Toast.LENGTH_LONG).show();
    }

    private void getQuakeDetail(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailLink = "";
                try {
                    JSONObject properties = response.getJSONObject("properties");
                    JSONObject products = properties.getJSONObject("products");
                    JSONArray geoserve = products.getJSONArray("geoserve");
                    for(int i =0; i<geoserve.length();i++){
                        JSONObject gerserveObj = geoserve.getJSONObject(i);
                        JSONObject contents = gerserveObj.getJSONObject("contents");
                        JSONObject geoJason = contents.getJSONObject("geoserve.json");
                        detailLink = geoJason.getString("url");
                    }
                    Log.d("detail", detailLink);
                    GetMoreDetail(detailLink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public void GetMoreDetail(String url){
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                builder = new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.popup,null);
                final Button dismissCross = view.findViewById(R.id.popClose);
                Button dismissButton = view.findViewById(R.id.dismiss);
                TextView popList = view.findViewById(R.id.popupList);
                WebView webView = view.findViewById(R.id.webview);
                StringBuilder stringBuilder = new StringBuilder();

                try {
                    if(response.has("tectonicSummary")&& response.getString("tectonicSummary")!=null){
                        JSONObject tecttonic = response.getJSONObject("tectonicSummary");
                        if(tecttonic.has("text")&&tecttonic.getString("text")!=null){
                            String text = tecttonic.getString("text");
                            webView.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);
                        }
                    }
                    JSONArray cities = response.getJSONArray("cities");
                    for(int i=0;i<cities.length();i++){
                        JSONObject cityOBJ = cities.getJSONObject(i);

                        stringBuilder.append("City: ").append(cityOBJ.getString("name")).append("\n").append("Distance: ")
                                .append(cityOBJ.getString("distance"))
                                .append("\n").append("Population: ").append(cityOBJ.getString("population"));
                        stringBuilder.append("\n\n");
                    }
                    dismissCross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }

                    });
                    dismissButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    popList.setText(stringBuilder);
                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
