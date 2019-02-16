package jlopez.com.yupayapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jlopez.com.yupayapp.R;
import jlopez.com.yupayapp.utils.Config;
import jlopez.com.yupayapp.utils.VolleyS;

public class RegistrarEstudianteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spnGrado, spnTipo,spnNivel;
    EditText etRegistrar;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    String grado, tipo, nivel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_estudiante);
        spnGrado = (Spinner) findViewById(R.id.spn_grado);
        spnTipo = (Spinner) findViewById(R.id.spn_tipo);
        spnNivel = (Spinner) findViewById(R.id.spn_nivel);
        etRegistrar = (EditText) findViewById(R.id.etRegistrar);

        spnGrado.setOnItemSelectedListener(this);
        spnTipo.setOnItemSelectedListener(this);
        spnNivel.setOnItemSelectedListener(this);
        volley = VolleyS.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpn=parent.getId();
        if (idSpn==R.id.spn_grado){
            grado = spnGrado.getSelectedItem().toString();
        } else if (idSpn==R.id.spn_tipo){
            tipo = spnTipo.getSelectedItem().toString();
        } else if(idSpn==R.id.spn_nivel){
            nivel = spnNivel.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            onPreStartConnection();
            fRequestQueue.add(request);
        }
    }
    public void onPreStartConnection() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void onConnectionFinished() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void onConnectionFailed(String error) {
        setProgressBarIndeterminateVisibility(false);
        Toast.makeText(RegistrarEstudianteActivity.this, error, Toast.LENGTH_SHORT).show();
    }
    public void registrarEstudiante(View view)
    {
        final String login=etRegistrar.getText().toString().trim();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,Config.GET_LOGIN+"?login="+login, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // JSONObject obj = new JSONObject(response);
                    String mensaje = response.getString("estado");

                    switch (mensaje) {
                        case "1":
                            // Obtener objeto "meta"
                            JSONArray jsonArray = response.getJSONArray("datos");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //Declaring a json object corresponding to every pdf object in our json Array
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //Declaring a Pdf object to add it to the ArrayList  pdfList
                                String pdfName = jsonObject.getString("login");
                                if (pdfName.equals(login)) {
                                    Toast.makeText(RegistrarEstudianteActivity.this, "El usuario ya existe existe", Toast.LENGTH_SHORT).show();
                                } else {
                                    insertStudent();
                                }
                            }
                            break;

                        case "2":
                            String mensaje2 = response.getString("mensaje");
                           // Toast.makeText(RegistrarEstudianteActivity.this, mensaje2, Toast.LENGTH_LONG).show();
                            insertStudent();
                            break;

                        case "3":
                            String mensaje3 = response.getString("mensaje");
                            Toast.makeText(RegistrarEstudianteActivity.
                                            this,
                                    mensaje3,
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                onConnectionFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onConnectionFailed(volleyError.toString());
            }
        });
        addToQueue(request);
    }
    public void insertStudent()
    {
        final String login=etRegistrar.getText().toString().trim();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,Config.INSERT_STUDENT+"?login="+login +"&grado="+grado+"&tipo_aprendizaje="+tipo+"&nivel_motivacional=siete&tipo_inteligencia="+nivel, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // JSONObject obj = new JSONObject(response);
                    String mensaje = response.getString("estado");

                    switch (mensaje) {
                        case "1":
                            Toast.makeText(RegistrarEstudianteActivity.this, "se registro sactisfactoriamente", Toast.LENGTH_LONG).show();
                            etRegistrar.setText("");
                            spnGrado.setSelection(0);
                            spnTipo.setSelection(0);
                            spnNivel.setSelection(0);

                        case "2":
                            String mensaje2 = response.getString("mensaje");
                            Toast.makeText(RegistrarEstudianteActivity.
                                            this,
                                    mensaje2,
                                    Toast.LENGTH_LONG).show();
                            break;

                        case "3":
                            String mensaje3 = response.getString("mensaje");
                            Toast.makeText(RegistrarEstudianteActivity.
                                            this,
                                    mensaje3,
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                onConnectionFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onConnectionFailed(volleyError.toString());
            }
        });
        addToQueue(request);
    }

}
