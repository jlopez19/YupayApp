package jlopez.com.yupayapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jlopez.com.yupayapp.R;

public class MenuAdministradorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);
    }

    public void irEstudiante(View view) {
        Intent ir= new Intent(MenuAdministradorActivity.this, RegistrarEstudianteActivity.class);
        startActivity(ir);
    }
    public void irTema(View view) {
        Intent ir= new Intent(MenuAdministradorActivity.this, RegistrarTemaActivity.class);
        startActivity(ir);
    }
    public void irRecuros(View view) {
        Intent ir= new Intent(MenuAdministradorActivity.this, RegistrarRecursoActivity.class);
        startActivity(ir);
    }
}
