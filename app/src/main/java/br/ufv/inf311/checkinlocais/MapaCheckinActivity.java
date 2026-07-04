package br.ufv.inf311.checkinlocais;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import br.ufv.inf311.checkinlocais.data.DatabaseHelper;
import br.ufv.inf311.checkinlocais.model.Checkin;

public class MapaCheckinActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private DatabaseHelper databaseHelper;
    private double latitudeAtual;
    private double longitudeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("MapaCheckin");

        databaseHelper = new DatabaseHelper(this);
        latitudeAtual = getIntent().getDoubleExtra("latitudeAtual", 0.0);
        longitudeAtual = getIntent().getDoubleExtra("longitudeAtual", 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        habilitarMinhaLocalizacaoSePermitido();

        LatLng posicaoAtual = new LatLng(latitudeAtual, longitudeAtual);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoAtual, 16f));
        carregarMarcadores();
    }

    private void habilitarMinhaLocalizacaoSePermitido() {
        if (googleMap == null) {
            return;
        }

        boolean permitido = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permitido) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {
                // A validação de permissão já foi realizada acima.
            }
        }
    }

    private void carregarMarcadores() {
        List<Checkin> checkins = databaseHelper.listarCheckins();

        for (Checkin checkin : checkins) {
            try {
                double latitude = Double.parseDouble(checkin.getLatitude());
                double longitude = Double.parseDouble(checkin.getLongitude());

                String snippet = String.format(
                        Locale.getDefault(),
                        "Categoria: %s Visitas: %d",
                        checkin.getCategoria(),
                        checkin.getQtdVisitas()
                );

                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(checkin.getLocal())
                        .snippet(snippet));
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Local ignorado por possuir coordenadas inválidas: " + checkin.getLocal(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_voltar_principal) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.action_gestao) {
            startActivity(new Intent(this, GestaoActivity.class));
            return true;
        }

        if (id == R.id.action_relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        }

        if (id == R.id.action_mapa_normal) {
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            return true;
        }

        if (id == R.id.action_mapa_hibrido) {
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
