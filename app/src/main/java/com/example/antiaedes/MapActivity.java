package com.example.antiaedes;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.example.antiaedes.dao.DenunciaDao;
import com.example.antiaedes.entities.Denuncia;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapActivity extends FragmentActivity {

    private Session mSession;
    private ArrayList<InfoMarker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        markers = new ArrayList<>();
        GoogleMap mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (mapFragment != null)
            onMapReady(mapFragment);
    }

    public void onMapReady(final GoogleMap googleMap) {
        DenunciaDao denunciaDao = new DenunciaDao();
        ArrayList<Denuncia> denuncias = denunciaDao.getAllDenunciation();
        if (denuncias != null) {
            for (Denuncia denuncia : denuncias) {
                boolean sucess = true;
                String latitude = denuncia.getLatitude();
                String longitude = denuncia.getLongitude();
                LatLng latLngDen = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                InfoMarker marker = new InfoMarker(denunciaDao.getAllDenunciationsActives());
                marker.addDenunciation(denuncia);
                for (Denuncia den : denuncias) {
                    LatLng latLng = new LatLng(Double.parseDouble(den.getLatitude()), Double.parseDouble(den.getLongitude()));
                    if (CalculationByDistance(latLngDen, latLng) > 100) {
                        if (!marker.containsDenunciation(den)) {
                            marker.addDenunciation(den);
                            denuncias.remove(den);
                        }
                    }
                }
                markers.add(marker);
            }

            for (InfoMarker infoMarker : markers) {
                infoMarker.setMarker(googleMap.addMarker(new MarkerOptions().position(infoMarker.getLatLng()).title(infoMarker.getId()+"")));
            }
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.windowmaplayout, null);
                TextView tvFoco = (TextView) view.findViewById(R.id.tvFoco);
                TextView tvFocoR = (TextView) view.findViewById(R.id.tvFocosResolved);

                TextView tvSuspect = (TextView) view.findViewById(R.id.tvSuspicion);
                TextView tvSuspectR = (TextView) view.findViewById(R.id.tvSuspicionResolved);

                InfoMarker infoMarker = searchMarker(Integer.parseInt(marker.getTitle()));
                if (infoMarker != null) {
                    tvFoco.setText(infoMarker.getFocus_den());
                    tvFocoR.setText(infoMarker.getFocus_res());
                    tvSuspect.setText(infoMarker.getSuspicion_den());
                    tvSuspectR.setText(infoMarker.getSuspicion_res());
                }
                return view;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Marker marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSLocation gps = new GPSLocation(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
            ;
        else
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gps);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15.0f));

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return meterInDec;
    }

    public InfoMarker searchMarker(int id) {
        if (markers != null) {
            for (InfoMarker info : markers) {
                if (info.getId() == id)
                    return info;
            }
        }
        return null;

    }
}
