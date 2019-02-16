package jlopez.com.yupayapp.activities;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jlopez.com.yupayapp.R;
import jlopez.com.yupayapp.utils.Config;
import jlopez.com.yupayapp.utils.FilePathPdf;
import jlopez.com.yupayapp.utils.Upload;
import jlopez.com.yupayapp.utils.VolleyS;

public class RegistrarRecursoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btn_esc_video;
    private Button btn_up_video;
    private TextView textView;
    private TextView textViewResponse;

    private Button buttonChoose;
    private Button buttonUpload;
    private static final int SELECT_VIDEO = 3;

    private String selectedPath;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static final String UPLOAD_URL = "https://www.lumeninnovations.org/yupay/apiRest/uploadPdf.php";
    public static final String PDF_FETCH_URL = "https://www.lumeninnovations.org/yupay/apiRest/getPdf.php";
    private int PICK_PDF_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private EditText editText;
    private Uri filePath;
    Uri selectedImageUri;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    List<String> listaTemas;
    ArrayAdapter<String> comboAdapter;
    Spinner spn_temas;
    String tema, grado, tipo;
    Spinner  spnTipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_recurso);

        requestStoragePermission();

        listaTemas = new ArrayList<>();
        //Initializing views
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        btn_esc_video = (Button) findViewById(R.id.btn_esc_video);
        btn_up_video = (Button) findViewById(R.id.btn_up_video);
        spn_temas = (Spinner) findViewById(R.id.spn_tema);

        spnTipo = (Spinner) findViewById(R.id.spn_tipo);

        spn_temas.setOnItemSelectedListener(this);
        spnTipo.setOnItemSelectedListener(this);

        editText = (EditText) findViewById(R.id.editTextName);
        textView = (TextView) findViewById(R.id.textView);
        textViewResponse = (TextView) findViewById(R.id.textViewResponse);
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            // do your stuff..
        }
        btn_esc_video.setOnClickListener(this);
        btn_up_video.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        btn_up_video.setEnabled(false);
        buttonUpload.setEnabled(false);
        volley = VolleyS.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        makeRequest();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpn=parent.getId();
       if (idSpn==R.id.spn_tipo){
            tipo = spnTipo.getSelectedItem().toString();
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
        Toast.makeText(RegistrarRecursoActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void makeRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,Config.GET_TOPIC, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = response.getJSONArray("datos");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        //Declaring a json object corresponding to every pdf object in our json Array
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //Declaring a Pdf object to add it to the ArrayList  pdfList
                        tema = jsonObject.getString("tema");
                        grado = jsonObject.getString("grado");
                        listaTemas.addAll(Collections.singleton("Tema: " + tema + "\nGrado: " + grado));
                    }
                    comboAdapter = new ArrayAdapter<String>(RegistrarRecursoActivity.this,android.R.layout.simple_spinner_item, listaTemas);
                    //Cargo el spinner con los datos
                    spn_temas.setAdapter(comboAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                comboAdapter.notifyDataSetChanged();
                onConnectionFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onConnectionFailed(volleyError.toString());
            }
        });
     //   fRequestQueue = volley.getRequestQueue();
        fRequestQueue = volley.getRequestQueue();
        fRequestQueue.add(request);
        //addToQueue(request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(RegistrarRecursoActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    private void showFileChooser() {

        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intentTxt = new Intent(Intent.ACTION_GET_CONTENT);
        intentTxt.setType("text/plain");
        intentTxt.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intentXls = new Intent(Intent.ACTION_GET_CONTENT);
        intentXls.setType("application/x-excel");
        intentXls.addCategory(Intent.CATEGORY_OPENABLE);

        PackageManager packageManager = getPackageManager();

        List activitiesPDF = packageManager.queryIntentActivities(intentPDF,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafePDF = activitiesPDF.size() > 0;

        List activitiesTxt = packageManager.queryIntentActivities(intentTxt,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafeTxt = activitiesTxt.size() > 0;

        List activitiesXls = packageManager.queryIntentActivities(intentXls,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafeXls = activitiesXls.size() > 0;

        if (!isIntentSafePDF || !isIntentSafeTxt || !isIntentSafeXls){

            // Potentially direct the user to the Market with a Dialog

        }

    }
    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_VIDEO) {
            System.out.println("SELECT_VIDEO");
            selectedImageUri = data.getData();
            selectedPath = getPath(selectedImageUri);
            textView.setText(selectedPath);
            btn_up_video.setEnabled(true);
        }
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            buttonUpload.setEnabled(true);

        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(RegistrarRecursoActivity.this, "Cargando video", "Por favor espere...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));

              /*  Uri uri = Uri.parse(s);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);*/
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());

                insertResource(s, "video");
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void insertResource(String url, String type) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,Config.INSERT_RESOURCE+"?tema="+tema +"&url="+url+"&type="+type+"&grado="+grado+"&tipo_aprendizaje="+tipo, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // JSONObject obj = new JSONObject(response);
                    String mensaje = response.getString("estado");

                    switch (mensaje) {
                        case "1":
                            Toast.makeText(RegistrarRecursoActivity.this, "se registro sactisfactoriamente", Toast.LENGTH_LONG).show();

                            spnTipo.setSelection(0);

                        case "2":
                            String mensaje2 = response.getString("mensaje");
                            Toast.makeText(RegistrarRecursoActivity.
                                            this,
                                    mensaje2,
                                    Toast.LENGTH_LONG).show();
                            break;

                        case "3":
                            String mensaje3 = response.getString("mensaje");
                            Toast.makeText(RegistrarRecursoActivity.
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


    public void uploadMultipart() {
        //getting name for the pdf
        String name = editText.getText().toString().trim();

        //getting the actual path of the pdf
        final String path = FilePathPdf.getPath(this, filePath);
       // editText.setText(path);

        if (path == null) {

            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, name, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("tema", tema) //Adding text parameter to the request
                        .addParameter("tipo_aprendizaje", tipo) //Adding text parameter to the request
                        .addParameter("grado", grado) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2).setDelegate(new UploadStatusDelegate() {
                    @Override
                    public void onProgress(UploadInfo uploadInfo) {

                    }

                    @Override
                    public void onError(UploadInfo uploadInfo, Exception exception) {

                    }

                    @Override
                    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                        String[] parts = path.split("0/");
                        String part1 = parts[0]; // 19
                        String nombrePdf = parts[1];
                        insertResource("https://www.lumeninnovations.org/yupay/apiRest/pdf/"+nombrePdf, "PDF");
                    }

                    @Override
                    public void onCancelled(UploadInfo uploadInfo) {

                    }
                })
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method to show file chooser
    private void showFileChooserPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onClick(View v) {
        if (v == btn_esc_video) {
            chooseVideo();

        }
        if (v == btn_up_video) {
            uploadVideo();
        }
        if (v == buttonChoose) {
            showFileChooserPdf();
        }
        if (v == buttonUpload) {
           uploadMultipart();
        }

    }
}
