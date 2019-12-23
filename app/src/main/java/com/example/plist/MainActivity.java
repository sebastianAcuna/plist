package com.example.plist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plist.barcode.BarcodeCaptureFragment;
import com.example.plist.bd.MyAppBD;
import com.example.plist.retrofit.APIClient;
import com.example.plist.retrofit.TaskService;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final int RC_BARCODE_CAPTURE_PRIMERO = 9000;
    public static final int RC_BARCODE_CAPTURE_SEGUNDO = 9001;

    ProgressDialog progreso;

    private static String CARPETA_PRINCIPAL = "misImagenesPlist/";
    private static String CARPETA_IMAGEN = "imagenes";
    private static String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;
    private File fileImagen;
    private static final int COD_FOTO = 20;
    public static MyAppBD myAppDB;

    TextView txtOne,txtDos;
    ImageButton imbtnOne,imbtnTwo,imbtnTres;
    ImageView imgPreview;
    Button btn_guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAppDB = Room.databaseBuilder(getApplicationContext(), MyAppBD.class,"plist.db").allowMainThreadQueries().build();

        imbtnOne = (ImageButton) findViewById(R.id.imbtnOne);
        imbtnTwo = (ImageButton) findViewById(R.id.imbtnTwo);
        imbtnTres = (ImageButton) findViewById(R.id.imbtnTres);
        txtOne = (TextView) findViewById(R.id.txtOne);
        txtDos = (TextView) findViewById(R.id.txtDos);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btn_guardar = (Button) findViewById(R.id.btn_guardar);



        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(Objects.requireNonNull(MainActivity.this).getApplicationContext(), "Permiso consedido", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }

        imbtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readBarCode(1);
            }
        });
        imbtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readBarCode(2);
            }
        });
        imbtnTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(txtOne.getText().toString()) || TextUtils.isEmpty(txtDos.getText().toString())){
                    Toast.makeText(MainActivity.this, "Debe primero escanear ambos codigos", Toast.LENGTH_SHORT).show();
                }else{
                    abrirCamara();
                }

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });
    }

    void limpiar(){
        txtDos.setText("");
        imgPreview.setImageBitmap(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.search) {
            subirTodo();
        }
        return super.onOptionsItemSelected(item);
    }


    void subirTodo(){

        subirImagenes();

    }


    public void abrirCamara(){
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if (!isCreada){
            isCreada=miFile.mkdirs();
        }

        if(isCreada){

            String etiquetaUno = txtOne.getText().toString();

            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString()+"_"+etiquetaUno+".jpg";
            String path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre;

            fileImagen=new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(intent, COD_FOTO);
        }
    }


    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Objects.requireNonNull(MainActivity.this), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(MainActivity.this), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode){
            case COD_FOTO :
                String nombre = fileImagen.getName();



                Fotos foto = new Fotos();
                foto.setRuta(fileImagen.getAbsolutePath());
                foto.setNombre(nombre);
                foto.setNombreContainer(txtDos.getText().toString());
                foto.setNombrePlist(txtOne.getText().toString());

                try{
                    long  idInserted = myAppDB.myDao().insertFoto(foto);
                    if(idInserted > 0){
                        agregarImagenToList();
                    }
                } catch (Exception e){
                    System.out.println("ERROR DE LA BD " + e);
                }
                break;

            case RC_BARCODE_CAPTURE_PRIMERO:
            case RC_BARCODE_CAPTURE_SEGUNDO:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureFragment.BarcodeObject);
                        int type = barcode.valueFormat;
                        if (type == Barcode.TEXT) {

                            switch (requestCode){
                                case RC_BARCODE_CAPTURE_PRIMERO:
                                    txtOne.setText(barcode.displayValue);
                                    break;

                                case RC_BARCODE_CAPTURE_SEGUNDO:
                                    txtDos.setText(barcode.displayValue);
                                    break;
                            }
                        }else if(type == Barcode.PRODUCT){

                            switch (requestCode){
                                case RC_BARCODE_CAPTURE_PRIMERO:
                                    txtOne.setText(barcode.displayValue);
                                    break;

                                case RC_BARCODE_CAPTURE_SEGUNDO:
                                    txtDos.setText(barcode.displayValue);
                                    break;
                            }
                        }
                    }
                }  else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            break;

        }



    }

    public void agregarImagenToList(){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final Fotos packageIn =  myAppDB.myDao().getFotosById(txtOne.getText().toString());
                    Objects.requireNonNull(MainActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load("file:///"+packageIn.getRuta()).resize(800,600).centerCrop().into(imgPreview);
                        }
                    });
                }
            });
    }





    public void readBarCode(int cual){

        Intent intent = new Intent(this, BarcodeCaptureFragment.class);
        switch (cual){
            case 1:
                startActivityForResult(intent, RC_BARCODE_CAPTURE_PRIMERO);
                break;
            case 2:
                startActivityForResult(intent, RC_BARCODE_CAPTURE_SEGUNDO);
                break;
            default:
                    startActivityForResult(intent, RC_BARCODE_CAPTURE_PRIMERO);
                break;
        }



    }




    public class submir extends AsyncTask<List<Fotos>, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso  = new ProgressDialog(MainActivity.this);
            progreso.setMessage("Subiendo Fotografias");
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.show();
        }

        @Override
        protected Boolean doInBackground(List<Fotos>... getPack) {

            final Boolean[] boleanTask = {true};


            if (getPack[0].size() > 0) {

                progreso.setMax(getPack[0].size());


                for (Fotos fts : getPack[0]) {
                    File foto = new File(fts.getRuta());
                    if (foto.exists()) {
                        String etiqueta = fts.getNombrePlist();

                        String imagen = "";
                        try {
                            imagen = imageToString(fts.getRuta());
                            //Thread.sleep(50);
                        } catch (Exception e) {
                            //Toast.makeText(this, "No se pudo comprimir la imagen etiqueta ' " + etiqueta + " ' ", Toast.LENGTH_SHORT).show();
                            boleanTask[0] = false;
                        }
                        if (imagen.length() > 0) {
                            String titulo = fts.getNombre();
                            String nombre_plist = fts.getNombrePlist();
                            final long idFoto = fts.getId();
                            String nombreContainer = fts.getNombreContainer();


                            TaskService taskService = APIClient.getApiClient().create(TaskService.class);
                            Call<Respuesta> call = taskService.uploadImages(idFoto, titulo, imagen, nombre_plist, nombreContainer);

                            call.enqueue(new Callback<Respuesta>() {
                                @Override
                                public void onResponse(@NonNull Call<Respuesta> call, @NonNull Response<Respuesta> response) {
                                    Respuesta respuesta = response.body();
                                    if (respuesta != null) {
                                        switch (respuesta.getEstado()) {
                                            case 1:
                                                try {
                                                    int update = myAppDB.myDao().updateFotoByIdAndEstado(idFoto);
                                                    if (update > 0) {
                                                        publishProgress(1);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    boleanTask[0] = false;
                                                    //Toast.makeText(MainActivity.this, "Foto con id n° " + respuesta.getMensaje() + " No pudo actualizarse ", Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                            case 2:
                                            default:
                                                boleanTask[0] = false;
                                                //Toast.makeText(MainActivity.this, "Foto No pudo actualizarse, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    } else {
                                        boleanTask[0] = false;
                                        //Toast.makeText(MainActivity.this,  "Foto No pudo actualizarse, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Respuesta> call, @NonNull Throwable t) {
                                    //Toast.makeText(MainActivity.this, "NO ENCONTRO RESPUESTA", Toast.LENGTH_SHORT).show();
                                    boleanTask[0] = false;
                                }
                            });


                        }

//                        else {
//
//                            if (progreso.isShowing()){
//                                progreso.dismiss();
//                            }
//                        }

                    }

                }
//                if (progreso.isShowing()){
//                    progreso.dismiss();
//                }
            }
            return boleanTask[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progreso.incrementProgressBy(values[0]);
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progreso.dismiss();
            if(aBoolean){
                Toast.makeText(MainActivity.this, "Todo subido con exito", Toast.LENGTH_SHORT).show();
                txtOne.setText("");
            }else{
                Toast.makeText(MainActivity.this, "No se subio todo, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void subirImagenes(){

        new submir().execute(myAppDB.myDao().getFotoByEstado());

    }


    private String imageToString(String ruta){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(ruta,null);

        bitmap.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

}
