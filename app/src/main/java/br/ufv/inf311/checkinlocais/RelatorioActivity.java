package br.ufv.inf311.checkinlocais;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Locale;

import br.ufv.inf311.checkinlocais.data.DatabaseHelper;
import br.ufv.inf311.checkinlocais.model.Checkin;

public class RelatorioActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private LinearLayout layoutListaRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Relatorio");

        databaseHelper = new DatabaseHelper(this);
        layoutListaRelatorio = findViewById(R.id.layoutListaRelatorio);

        carregarRelatorio();
    }

    private void carregarRelatorio() {
        layoutListaRelatorio.removeAllViews();

        List<Checkin> checkins = databaseHelper.listarRelatorio();

        if (checkins.isEmpty()) {
            layoutListaRelatorio.addView(criarTextViewVazio("Nenhum check-in cadastrado."));
            return;
        }

        for (Checkin checkin : checkins) {
            layoutListaRelatorio.addView(criarLinhaRelatorio(checkin));
        }
    }

    private LinearLayout criarLinhaRelatorio(Checkin checkin) {
        LinearLayout linha = new LinearLayout(this);
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setGravity(Gravity.CENTER_VERTICAL);
        linha.setPadding(dp(6), dp(4), dp(2), dp(4));
        linha.setMinimumHeight(dp(52));
        linha.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        linha.addView(criarTextViewLocal(checkin.getLocal()));
        linha.addView(criarTextViewVisitas(String.format(Locale.getDefault(), "%d", checkin.getQtdVisitas())));

        return linha;
    }

    private TextView criarTextViewVazio(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(15f);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(dp(6), dp(12), dp(6), dp(12));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return textView;
    }

    private TextView criarTextViewLocal(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(15f);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setSingleLine(false);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setPadding(0, 0, dp(8), 0);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        return textView;
    }

    private TextView criarTextViewVisitas(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(15f);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                dp(72),
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return textView;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_voltar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_voltar) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
