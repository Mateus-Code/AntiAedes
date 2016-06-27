package com.example.antiaedes;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.example.antiaedes.dao.DenunciaDao;
import com.example.antiaedes.entities.Denuncia;
import com.example.antiaedes.entities.Endereco;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DenunciationActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView mCamera;
    private Spinner mSpinner;
    private EditText mCep;
    private EditText mNeighborhood;
    private EditText mStreet;
    private EditText mCity;
    private EditText mReference;
    private EditText mDetails;

    private InputStream mImage;
    private GPSLocation gps;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denunciation);

        if (getIntent().hasExtra("session"))
            mSession = (Session) getIntent().getSerializableExtra("session");

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (!(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(DenunciationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mCamera = (ImageView) findViewById(R.id.denunciation_camera);
        mSpinner = (Spinner) findViewById(R.id.denunciation_spinner);
        mCep = (EditText) findViewById(R.id.denunciation_cep);
        mNeighborhood = (EditText) findViewById(R.id.denunciation_neighborhood);
        mStreet = (EditText) findViewById(R.id.denunciation_street);
        mCity = (EditText) findViewById(R.id.denunciation_city);
        mReference = (EditText) findViewById(R.id.denunciation_reference);
        mDetails = (EditText) findViewById(R.id.denunciation_details);

        mCep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    PostOffices postOffices = new PostOffices();
                    Endereco endereco = postOffices.fetchAddress(mCep.getText().toString());
                    fillFields(endereco);
                }
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_of_denunciation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinner.setAdapter(adapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps = new GPSLocation(this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gps);

        if (!(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(DenunciationActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillFields(Address address) {
        if (address != null) {
            mCep.setText(address.getPostalCode());
            //mNeighborhood.setText(address.);
            mStreet.setText(address.getAddressLine(0));
            mCity.setText(address.getLocality());
        } else return;
    }

    public void fillFields(Endereco endereco) {
        if (endereco != null) {
            if (endereco.getBairro() != null)
                mNeighborhood.setText(endereco.getBairro());
            if (endereco.getEnd() != null)
                mStreet.setText(endereco.getEnd());
            if (endereco.getCidade() != null)
                mCity.setText(endereco.getCidade());
        } else return;
    }

    public void captureLocation(View view) {
        LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        List<Address> endereco = geocodeLatLng(latLng);
        if (endereco != null) {
            if (endereco.get(0) != null)
                fillFields(endereco.get(0));
        }
    }

    public List<Address> geocodeLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addressList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void capturePhoto(View view) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            ActivityCompat.requestPermissions(DenunciationActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();
            mImage = new ByteArrayInputStream(bitmapdata);
            mCamera.setImageBitmap(photo);
        }
    }

    public void denunciation(View view) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar date = Calendar.getInstance();
        DenunciaDao denunciaDao = new DenunciaDao();
        Denuncia denuncia = new Denuncia();
        denuncia.setTipo(mSpinner.getSelectedItemPosition());
        denuncia.setBairro(mNeighborhood.getText().toString());
        denuncia.setRua(mStreet.getText().toString());
        denuncia.setReferencia(mReference.getText().toString());
        denuncia.setDescricao(mDetails.getText().toString());
        denuncia.setLatitude(String.valueOf(gps.getLatitude()));
        denuncia.setLongitude(String.valueOf(gps.getLongitude()));
        denuncia.setData(dateFormat.format(date.getTimeInMillis()));
        denuncia.setCodigo(10);

        if (mSession != null) {
            if (!mSession.getCpf().isEmpty()) denuncia.setId_fun(mSession.getId());
            else denuncia.setId_user(mSession.getId());
        }
        denunciaDao.registerDenunciation(denuncia);
    }
}
