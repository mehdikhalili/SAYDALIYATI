package com.iao.saydaliyati.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.iao.saydaliyati.R;
import com.iao.saydaliyati.directionhelper.DirectionsParser;
import com.iao.saydaliyati.entity.Pharmacy;
import com.iao.saydaliyati.repository.PharmacyRepository;
import com.iao.saydaliyati.repository.intefaces.ListPharmaciesCallback;
import com.iao.saydaliyati.ui.profile.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    FusedLocationProviderClient client;

    LatLng currentLocation = new LatLng(34.0392857 ,-6.8200107);

    LinearLayout layout_pharmacy;
    TextView tv_pharmacy_name, tv_pharmacy_city;
    ImageButton btn_afficher, btn_direction;

    private Polyline currentPolyline;

    private Marker selectedMarker;

    Pharmacy pharmacyFromProfile;

    boolean isPharmacyFromProfile = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        layout_pharmacy = root.findViewById(R.id.layout_pharmacy);
        tv_pharmacy_name = root.findViewById(R.id.tv_pharmacy_name);
        tv_pharmacy_city = root.findViewById(R.id.tv_pharmacy_city);
        btn_afficher = root.findViewById(R.id.btn_afficher);
        btn_direction = root.findViewById(R.id.btn_direction);

        pharmacyFromProfile = (Pharmacy) getActivity().getIntent().getSerializableExtra("pharmacy");

        if (pharmacyFromProfile != null) {
            isPharmacyFromProfile = true;
        }

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }

                try {
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
                } catch (Resources.NotFoundException ignored) {}

                PharmacyRepository pharmacyRepository = new PharmacyRepository();

                pharmacyRepository.findGardPharmacies(new ListPharmaciesCallback() {
                    @Override
                    public void myResponseCallback(List<Pharmacy> pharmacies) {
                        LatLng location = currentLocation;
                        for (Pharmacy pharmacy: pharmacies) {
                            Marker marker =  map.addMarker(new MarkerOptions()
                                    .position(pharmacy.getLagLng())
                                    .title("Pharmacie " + pharmacy.getName())
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_garde_marker)));
                            marker.setTag(pharmacy);
                            if (isPharmacyFromProfile) {
                                if (pharmacy.getId().equals(pharmacyFromProfile.getId())) {
                                    location = pharmacy.getLagLng();
                                    tv_pharmacy_name.setText("Pharmacie " + pharmacy.getName());
                                    tv_pharmacy_city.setText(pharmacy.getCity() + ", " + pharmacy.getArrondissement());
                                    selectedMarker = marker;
                                    layout_pharmacy.setVisibility(View.VISIBLE);
                                    marker.showInfoWindow();
                                }
                            }
                        }
                        animateCamera(location);
                    }
                });

                map.getUiSettings().setMapToolbarEnabled(false);

                map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                    @Override
                    public void onInfoWindowClose(Marker marker) {
                        selectedMarker = null;
                        layout_pharmacy.setVisibility(View.INVISIBLE);
                        if (currentPolyline != null){
                            currentPolyline.remove();
                        }
                    }
                });

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        selectedMarker = marker;
                        Pharmacy pharmacy = (Pharmacy) marker.getTag();
                        tv_pharmacy_name.setText("Pharmacie " + pharmacy.getName());
                        tv_pharmacy_city.setText(pharmacy.getCity() + ", " + pharmacy.getArrondissement());
                        layout_pharmacy.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                btn_direction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = getUrl(currentLocation, selectedMarker.getPosition());
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);
                        animateCamera(currentLocation);
                    }
                });

                btn_afficher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pharmacy pharmacy = (Pharmacy) selectedMarker.getTag();
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("pharmacy", pharmacy);
                        startActivity(intent);
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                currentLocation = new LatLng(location1.getLatitude(), location1.getLongitude());
                            }
                        };
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // API key
        String key = "key=AIzaSyAVX5FSWvTtpXBd4nOFwiul1bWSSSANGAo";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + key;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //super.onPostExecute(lists);

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path: lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point: path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat, lng));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(20);
                polylineOptions.color(ContextCompat.getColor(getContext(), R.color.deep_green));
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                currentPolyline = map.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getActivity(), "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void animateCamera(LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(16)
                .tilt(30)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}