package jlopez.com.yupayapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegistrarTemaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    Spinner spnGrado;
    EditText etTema;
    String grado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_tema);

        spnGrado = (Spinner) findViewById(R.id.spn_grado);
        etTema = (EditText) findViewById(R.id.et_tema);

        spnGrado.setOnItemSelectedListener(this);
        volley = VolleyS.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
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
        Toast.makeText(RegistrarTemaActivity.this, error, Toast.LENGTH_SHORT).show();
    }
    public void registrarTema(View view)
    {
        final String tema=etTema.getText().toString().trim();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,Config.INSERT_TOPIC+"?tema="+tema+"&grado="+grado, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // JSONObject obj = new JSONObject(response);
                    String mensaje = response.getString("estado");

                    switch (mensaje) {
                        case "1":
                            // Obtener objeto "meta"
                            Toast.makeText(RegistrarTemaActivity.this, "se registro sactisfactoriamente", Toast.LENGTH_LONG).show();
                            etTema.setText("");
                            spnGrado.setSelection(0);

                            break;

                        case "2":
                            String mensaje2 = response.getString("mensaje");
                             Toast.makeText(RegistrarTemaActivity.this, mensaje2, Toast.LENGTH_LONG).show();

                            break;

                        case "3":
                            String mensaje3 = response.getString("mensaje");
                            Toast.makeText(RegistrarTemaActivity.
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpn=parent.getId();
        if (idSpn==R.id.spn_grado){
            grado = spnGrado.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
