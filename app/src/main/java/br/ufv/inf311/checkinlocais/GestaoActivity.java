package br.ufv.inf311.checkinlocais;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import br.ufv.inf311.checkinlocais.data.DatabaseHelper;
import br.ufv.inf311.checkinlocais.model.Checkin;

public class GestaoActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private LinearLayout layoutListaGestao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("GestaoCheckin");

        databaseHelper = new DatabaseHelper(this);
        layoutListaGestao = findViewById(R.id.layoutListaGestao);

        carregarCheckins();
    }

    private void carregarCheckins() {
        layoutListaGestao.removeAllViews();

        List<Checkin> checkins = databaseHelper.listarCheckins();

        if (checkins.isEmpty()) {
            layoutListaGestao.addView(criarTextViewVazio("Nenhum check-in cadastrado."));
            return;
        }

        for (Checkin checkin : checkins) {
            layoutListaGestao.addView(criarLinhaGestao(checkin));
        }
    }

    private LinearLayout criarLinhaGestao(Checkin checkin) {
        LinearLayout linha = new LinearLayout(this);
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setGravity(Gravity.CENTER_VERTICAL);
        linha.setPadding(dp(6), dp(4), dp(2), dp(4));
        linha.setMinimumHeight(dp(52));
        linha.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView nomeLocal = criarTextViewLocal(checkin.getLocal());
        ImageButton botaoExcluir = criarBotaoExcluir(checkin.getLocal());

        linha.addView(nomeLocal);
        linha.addView(botaoExcluir);

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

    private ImageButton criarBotaoExcluir(String local) {
        ImageButton button = new ImageButton(this);
        button.setImageResource(android.R.drawable.ic_menu_delete);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setContentDescription("Excluir " + local);
        button.setTag(local);
        button.setOnClickListener(this::confirmarExclusao);
        button.setPadding(dp(12), dp(10), dp(12), dp(10));
        button.setLayoutParams(new LinearLayout.LayoutParams(
                dp(64),
                dp(48)
        ));
        return button;
    }

    private void confirmarExclusao(View view) {
        String local = String.valueOf(view.getTag());

        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + local + "?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> {
                    databaseHelper.deletarCheckin(local);
                    Toast.makeText(this, "Check-in excluído.", Toast.LENGTH_SHORT).show();
                    reiniciarTela();
                })
                .show();
    }

    private void reiniciarTela() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
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
