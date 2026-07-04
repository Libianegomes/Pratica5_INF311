package br.ufv.inf311.checkinlocais;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Locale;

import br.ufv.inf311.checkinlocais.data.DatabaseHelper;
import br.ufv.inf311.checkinlocais.model.Categoria;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private DatabaseHelper databaseHelper;
    private AutoCompleteTextView autoLocal;
    private Spinner spinnerCategoria;
    private TextView txtLatitude;
    private TextView txtLongitude;
    private Button btnCheckin;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean atualizandoLocalizacao = false;

    private Double latitudeAtual;
    private Double longitudeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("CheckInLocais");

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        autoLocal = findViewById(R.id.autoLocal);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        btnCheckin = findViewById(R.id.btnCheckin);

        carregarCamposDoBanco();
        configurarLocalizacao();

        btnCheckin.setOnClickListener(view -> realizarCheckin());
    }

    private void carregarCamposDoBanco() {
        List<String> locais = databaseHelper.listarLocaisVisitados();
        ArrayAdapter<String> locaisAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                locais
        );
        autoLocal.setAdapter(locaisAdapter);

        List<Categoria> categorias = databaseHelper.listarCategorias();
        ArrayAdapter<Categoria> categoriasAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriasAdapter);
    }

    private void realizarCheckin() {
        String local = autoLocal.getText().toString().trim();
        Categoria categoriaSelecionada = (Categoria) spinnerCategoria.getSelectedItem();

        if (local.isEmpty()) {
            Toast.makeText(this, "Informe o nome do local.", Toast.LENGTH_SHORT).show();
            autoLocal.requestFocus();
            return;
        }

        if (categoriaSelecionada == null) {
            Toast.makeText(this, "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkinExiste(local)) {
            databaseHelper.incrementarVisita(local);
            Toast.makeText(this, "Check-in atualizado com sucesso.", Toast.LENGTH_SHORT).show();
            reiniciarTelaPrincipal();
            return;
        }

        if (latitudeAtual == null || longitudeAtual == null) {
            Toast.makeText(this, "Aguarde a obtenção da localização atual.", Toast.LENGTH_LONG).show();
            return;
        }

        databaseHelper.inserirCheckin(
                local,
                categoriaSelecionada.getId(),
                String.valueOf(latitudeAtual),
                String.valueOf(longitudeAtual)
        );

        Toast.makeText(this, "Check-in cadastrado com sucesso.", Toast.LENGTH_SHORT).show();
        reiniciarTelaPrincipal();
    }

    private void reiniciarTelaPrincipal() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void configurarLocalizacao() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    atualizarLocalizacaoNaTela(location);
                }
            }
        };

        if (temPermissaoLocalizacao()) {
            iniciarAtualizacoesDeLocalizacao();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    private boolean temPermissaoLocalizacao() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void iniciarAtualizacoesDeLocalizacao() {
        if (atualizandoLocalizacao || !temPermissaoLocalizacao()) {
            return;
        }

        atualizandoLocalizacao = true;

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                atualizarLocalizacaoNaTela(location);
            }
        });

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void atualizarLocalizacaoNaTela(Location location) {
        latitudeAtual = location.getLatitude();
        longitudeAtual = location.getLongitude();

        txtLatitude.setText(String.format(Locale.US, "%.7f", latitudeAtual));
        txtLongitude.setText(String.format(Locale.US, "%.7f", longitudeAtual));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temPermissaoLocalizacao()) {
            iniciarAtualizacoesDeLocalizacao();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            atualizandoLocalizacao = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarAtualizacoesDeLocalizacao();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_mapa) {
            abrirMapa();
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

        return super.onOptionsItemSelected(item);
    }

    private void abrirMapa() {
        if (latitudeAtual == null || longitudeAtual == null) {
            Toast.makeText(this, "Aguarde a obtenção da localização para abrir o mapa.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, MapaCheckinActivity.class);
        intent.putExtra("latitudeAtual", latitudeAtual);
        intent.putExtra("longitudeAtual", longitudeAtual);
        startActivity(intent);
    }
}
