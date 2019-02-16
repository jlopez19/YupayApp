package jlopez.com.yupayapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jlopez.com.yupayapp.R;
import jlopez.com.yupayapp.adapters.Adaptador;
import jlopez.com.yupayapp.utils.Config;
import jlopez.com.yupayapp.utils.Preference;
import jlopez.com.yupayapp.utils.Recursos;
import jlopez.com.yupayapp.utils.VolleyS;

public class MenuEstudianteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    List<Recursos> listaTemas;
    Context context;
    private RecyclerView recycler;
    private Adaptador adapter;
    private RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
    private SearchView mSearchView;
    Recursos recursos;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    String nombre, grado, tipo_aprendizaje, nivel_motivacional, tipo_intelijencia;
    TextView tv_nombre, tv_grado, tv_tipoa, tv_tipoi, tv_nivel_m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_estudiante);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        tv_nombre = (TextView) findViewById(R.id.nombre);
        tv_grado = (TextView) findViewById(R.id.grado);
        tv_tipoa = (TextView) findViewById(R.id.tipo_aprendizaje);
        tv_tipoi = (TextView) findViewById(R.id.tipo_intelijencia);
        tv_nivel_m = (TextView) findViewById(R.id.nivel_motivacional);
        lManager = new LinearLayoutManager(MenuEstudianteActivity.this);
        volley = VolleyS.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        listaTemas = new ArrayList<>();
        makeRequest();
        setupSearchView();

        loadPreference();
    }
    @SuppressLint("SetTextI18n")
    private void loadPreference() {
        preferences = getSharedPreferences(Preference.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        nombre = preferences.getString(Preference.LOGIN, "");
        grado =preferences.getString(Preference.GRADO, "");
        tipo_aprendizaje =preferences.getString(Preference.TIPO_APRENDIZAJE, "");
        nivel_motivacional =preferences.getString(Preference.TIPO_INTELIGENCIA, "");
        tipo_intelijencia =preferences.getString(Preference.NIVEL_MOTIVACIONAL, "");
        tv_nombre.setText("Bienvenid@: "+nombre);
        tv_grado.setText(grado);
        tv_tipoa.setText(tipo_aprendizaje);
        tv_tipoi.setText(nivel_motivacional);
        tv_nivel_m.setText(tipo_intelijencia);
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
        Toast.makeText(MenuEstudianteActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void makeRequest(){

        JsonObjectRequest contextRequest = new JsonObjectRequest
                (Request.Method.GET, Config.GET_RESOURCES,null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                recursos=new Recursos();
                                recursos.setTema(jsonObject.getString("tema"));
                                recursos.setUrl(jsonObject.getString("url"));
                                recursos.setType(jsonObject.getString("type"));
                                recursos.setGrado(jsonObject.getString("grado"));
                                recursos.setTipo_aprendizaje(jsonObject.getString("tipo_aprendizaje"));
                                listaTemas.add(recursos);
                            }
                            Collections.sort(listaTemas, new Comparator<Recursos>() {
                                @Override
                                public int compare(Recursos a, Recursos b) {
                                    return (tipo_aprendizaje).compareTo(b.getTipo_aprendizaje());
                                }
                            });

                            adapter = new Adaptador(context, MenuEstudianteActivity.this, listaTemas);
                            StaggeredGridLayoutManager gridLayoutManager =new StaggeredGridLayoutManager( 1, StaggeredGridLayoutManager.VERTICAL);
                            recycler.setLayoutManager(gridLayoutManager);
                            recycler.setItemAnimator(new DefaultItemAnimator());

                            recycler.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Some error to access URL : Room may not exists...
                    }
                });
        addToQueue(contextRequest);
    }
    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Buscar...");
    }

    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
