package jlopez.com.yupayapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jlopez.com.yupayapp.R;
import jlopez.com.yupayapp.adapters.PdfAdapter;
import jlopez.com.yupayapp.utils.Config;
import jlopez.com.yupayapp.utils.Pdf;
import jlopez.com.yupayapp.utils.Preference;
import jlopez.com.yupayapp.utils.VolleyS;

public class LoginActivity extends AppCompatActivity {

    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    EditText etLogin;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLogin = (EditText)findViewById(R.id.etLogin);
        preferences =  getSharedPreferences(Preference.PREFERENCE_NAME, Context.MODE_PRIVATE);
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
        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
    }
    public void irLogin(View view)
    {
        String login=etLogin.getText().toString().trim();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Config.GET_LOGIN+"?login="+login,null, new Response.Listener<JSONObject>() {
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
                                String nombre = jsonObject.getString("login");
                                if (nombre.equals("admin")){
                                    Intent ir= new Intent(LoginActivity.this, MenuAdministradorActivity.class);
                                    startActivity(ir);
                                }else if (nombre.equals(nombre)) {
                                    editor = preferences.edit();
                                    editor.putString(Preference.LOGIN, jsonObject.getString("login"));
                                    editor.putString(Preference.GRADO, jsonObject.getString("grado"));
                                    editor.putString(Preference.TIPO_APRENDIZAJE, jsonObject.getString("tipo_aprendizaje"));
                                    editor.putString(Preference.NIVEL_MOTIVACIONAL, jsonObject.getString("nivel_motivacional"));
                                    editor.putString(Preference.TIPO_INTELIGENCIA, jsonObject.getString("tipo_inteligencia"));
                                    editor.apply();
                                    Intent ir= new Intent(LoginActivity.this, MenuEstudianteActivity.class);
                                    startActivity(ir);
                                }
                                else{
                                        Toast.makeText(LoginActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            break;

                        case "2":
                            String mensaje2 = response.getString("mensaje");
                            Toast.makeText(LoginActivity.
                                    this,
                                    mensaje2,
                                    Toast.LENGTH_LONG).show();
                            break;

                        case "3":
                            String mensaje3 = response.getString("mensaje");
                            Toast.makeText(LoginActivity.
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
