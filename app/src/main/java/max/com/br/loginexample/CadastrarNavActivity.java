package max.com.br.loginexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class CadastrarNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RequestQueue rq;
    private EditText nomeEt, pesoEt, alturaEt, especieEt;
    private Treinador treinador;
    private ImageView image;
    private String pathImage;
    String url;
    AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent it = getIntent();

        treinador = (Treinador) it.getSerializableExtra("treinador");

        TextView tv = findViewById(R.id.bemvindo);

        nomeEt = (EditText) findViewById(R.id.nomePokemonEt);
        pesoEt = (EditText) findViewById(R.id.pesoPokemonEt);
        alturaEt = (EditText) findViewById(R.id.alturaPokemonEt);
        especieEt = (EditText) findViewById(R.id.especiePokemonEt);
        image = (ImageView) findViewById(R.id.imageView2);

        rq = Volley.newRequestQueue(CadastrarNavActivity.this);
        pathImage=null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.initial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sair) {
        } else if (id == R.id.nav_cadastrar) {
            Intent it = new Intent(this, CadastrarNavActivity.class);
            it.putExtra("treinador", treinador);
            startActivity(it);
        } else if (id == R.id.nav_consultar) {

        } else if (id == R.id.nav_pesquisar) {
            Intent it = new Intent(this, PesquisarNavActivity.class);
            it.putExtra("treinador", treinador);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cadastrarPokemon(View view) {
        String ip = Sistema.IP;
        String post_url = "http://"+ip+":8080/SistemaCentral/webresources/pokemons";
        Pokemon poke = new Pokemon();

        poke.setNome(nomeEt.getText().toString());
        poke.setEspecie(especieEt.getText().toString());
        poke.setAltura(Float.parseFloat(alturaEt.getText().toString()));
        poke.setPeso(Float.parseFloat(pesoEt.getText().toString()));
        poke.setImagem(pathImage);
        poke.setTreinador(treinador);

        Gson gson = new Gson();
        String jsonInString = gson.toJson(poke);
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonInString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(post_url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Script", "SUCCESS: " + response.toString());
                        Toast.makeText(CadastrarNavActivity.this, "Pokemon inserido", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 409) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject pokemon = null;
                        String nomeTreinador = null;
                        try{
                            pokemon = new JSONObject(jsonError);
                            nomeTreinador = pokemon.getJSONObject("treinador").get("nome").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(CadastrarNavActivity.this, "Erro: Pokemon já cadastrado pelo treinador: "+nomeTreinador, Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(CadastrarNavActivity.this, "Erro: Pokemon já cadastrado", Toast.LENGTH_LONG).show();
                }
            }


        });
        request.setTag("tag");
        rq.add(request);

    }

    static final int REQUEST_GALLERY = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void dispatchTakePictureIntent(View view) {

        criaAlertFinal("Capturar", "Escolha como capturar seu Pokemon.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pathImage = imageToString(imageBitmap);

            image.setImageBitmap(imageBitmap);
        } else {
            if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY) {
                Uri selectedImage = data.getData();

                image.setImageURI(selectedImage);
            }
        }

    }

    private void criaAlertFinal(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_GALLERY);//one can be replaced with any action code
            }
        });

        alerta = builder.create();
        alerta.show();
    }

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    //EXEMPLO DE COMO RECUPERAR UMA IMAGEM DO BD
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
