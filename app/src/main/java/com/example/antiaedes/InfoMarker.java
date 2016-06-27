package com.example.antiaedes;

import com.example.antiaedes.dao.DenunciaDao;
import com.example.antiaedes.entities.Denuncia;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class InfoMarker {

    private static int id = 0;
    private int number_denunciations;
    private int number_denunciations_resolved;
    private int suspicion_den = 0;
    private int suspicion_res = 0;
    private int focus_den = 0;
    private int focus_res = 0;
    private ArrayList<Denuncia> denunciations;
    private ArrayList<Denuncia> denunciationsR;
    private Marker marker;

    public InfoMarker(ArrayList<Denuncia> denunciationsR) { //Internet economy
        this.denunciations = new ArrayList<>();
        this.denunciationsR = denunciationsR;
        this.id = id + 1;
    }

    public int getNumber_denunciations() {
        return number_denunciations;
    }

    public int getNumber_denunciations_resolved() {
        if (denunciationsR != null && denunciations != null) {
            for (Denuncia denunciation : denunciations) {
                for (Denuncia den : denunciationsR) {
                    if (denunciation == den) number_denunciations_resolved++;
                }
            }
        }
        return number_denunciations_resolved;
    }

    public ArrayList<Denuncia> getDenunciations() {
        return denunciations;
    }

    public void addDenunciation(Denuncia denunciation) {
        if (denunciation != null) {
            denunciations.add(denunciation);
            teste(denunciation);
            number_denunciations++;
        }
    }

    public void teste(Denuncia denuncia){
        boolean bool = true;
        for(Denuncia d : denunciationsR){
            bool = true;
            if(d.getId() == denuncia.getId()){
                if(d.getTipo() == 0) {
                    suspicion_res++;
                    bool = false;
                    break;
                }else{
                    focus_res++;
                    bool = false;
                    break;
                }
            }
        }
        if(bool){
            if(denuncia.getTipo() == 0)
                suspicion_den++;
            else
                focus_den++;
        }
    }

    public boolean containsDenunciation(Denuncia denunciation) {
        for (Denuncia d : denunciations) {
            if (d == denunciation)
                return true;
        }
        return false;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(denunciations.get(0).getLatitude()), Double.parseDouble(denunciations.get(0).getLongitude()));
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public int getId() {
        return this.id;
    }

    public int getSuspicion_den() {
        return suspicion_den;
    }

    public int getSuspicion_res() {
        return suspicion_res;
    }

    public int getFocus_den() {
        return focus_den;
    }

    public int getFocus_res() {
        return focus_res;
    }
}
