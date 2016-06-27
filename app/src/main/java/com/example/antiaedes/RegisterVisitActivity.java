package com.example.antiaedes;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.antiaedes.dao.VisitaDao;
import com.example.antiaedes.entities.Denuncia;
import com.example.antiaedes.entities.Visita;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterVisitActivity extends AppCompatActivity {

    private Spinner mSituation;
    private EditText mCep;
    private EditText mNumHouse;
    private EditText mNeighborhood;
    private EditText mStreet;
    private EditText mObservation;

    private GPSLocation gps;
    private Session mSession;
    private Denuncia hasDenunciation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_visit);

        if(getIntent().hasExtra("session")) mSession = (Session) getIntent().getSerializableExtra("session");
        if(getIntent().hasExtra("denunciation")) hasDenunciation = (Denuncia) getIntent().getSerializableExtra("denunciation");

        mSituation = (Spinner) findViewById(R.id.visit_spinner);
        mCep = (EditText) findViewById(R.id.visit_cep);
        mNumHouse = (EditText) findViewById(R.id.visit_num_house);
        mNeighborhood = (EditText) findViewById(R.id.visit_neighborhood);
        mStreet = (EditText) findViewById(R.id.denunciation_street);
        mObservation = (EditText) findViewById(R.id.visit_observation);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.visit_situation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSituation.setAdapter(adapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps = new GPSLocation(this);
        if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gps);
    }

    public void readQRCode(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                try {
                    String[] vl = contents.split("-");
                    int situation = Integer.parseInt(vl[0]);
                    String cep = vl[1];
                    int number = Integer.parseInt(vl[2]);
                    String neighborhood = vl[3];
                    String street = vl[4];
                    fillFields(situation,cep,number,neighborhood,street);
                } catch (ClassCastException e) {
                    Toast.makeText(getBaseContext(), R.string.qrcode_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), R.string.qrcode_canceled, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void fillFields(int situation, String cep, int num_house, String neighborhood, String street){
        mSituation.setId(situation);
        mCep.setText(cep);
        mNumHouse.setText(num_house);
        mNeighborhood.setText(neighborhood);
        mStreet.setText(street);
    }

    public void registerVisit(View view){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar date = Calendar.getInstance();
        Visita visita = new Visita();
        visita.setId_fun(mSession.getId());
        visita.setSituation(mSituation.getSelectedItemPosition());
        visita.setData(dateFormat.format(date.getTimeInMillis()));
        visita.setObservacao(mObservation.getText().toString());
        VisitaDao visitaDao = new VisitaDao();

        Denuncia denuncia = new Denuncia();
        denuncia.setCep(mCep.getText().toString());
        denuncia.setData(dateFormat.format(date.getTimeInMillis()));
        denuncia.setNum_casa(Integer.parseInt(mNumHouse.getText().toString()));
        denuncia.setBairro(mNeighborhood.getText().toString());
        denuncia.setDescricao(mObservation.getText().toString());
        denuncia.setId_fun(mSession.getId());
        visitaDao.registerVisit(visita,denuncia);
    }
}
