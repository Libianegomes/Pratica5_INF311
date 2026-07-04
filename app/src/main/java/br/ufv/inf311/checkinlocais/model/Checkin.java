package br.ufv.inf311.checkinlocais.model;

public class Checkin {
    private final String local;
    private final int qtdVisitas;
    private final int catId;
    private final String categoria;
    private final String latitude;
    private final String longitude;

    public Checkin(String local, int qtdVisitas, int catId, String categoria, String latitude, String longitude) {
        this.local = local;
        this.qtdVisitas = qtdVisitas;
        this.catId = catId;
        this.categoria = categoria;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocal() {
        return local;
    }

    public int getQtdVisitas() {
        return qtdVisitas;
    }

    public int getCatId() {
        return catId;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
