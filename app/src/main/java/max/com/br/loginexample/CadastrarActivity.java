package max.com.br.loginexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class CadastrarActivity extends AppCompatActivity {
    private RequestQueue rq;
    private EditText nomeEt, pesoEt, alturaEt, especieEt;
    private Treinador treinador;
    private ImageView image;
    private String pathImage;
    String url;
    AlertDialog alerta;
    int cameraOrGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        Intent it = getIntent();

        treinador = (Treinador) it.getSerializableExtra("treinador");

        TextView tv = findViewById(R.id.bemvindo);

        nomeEt = (EditText) findViewById(R.id.nomePokemonEt);
        pesoEt = (EditText) findViewById(R.id.pesoPokemonEt);
        alturaEt = (EditText) findViewById(R.id.alturaPokemonEt);
        especieEt = (EditText) findViewById(R.id.especiePokemonEt);
        image = (ImageView) findViewById(R.id.imageView2);

        rq = Volley.newRequestQueue(CadastrarActivity.this);
        cameraOrGallery=0;
        pathImage=null;
    }


    public void cadastrarPokemon(View view) {
        String ip = Sistema.IP;
        String post_url = "http://" + ip + ":8080/SistemaCentral/webresources/pokemons";
        Pokemon poke = new Pokemon();
        Boolean valido;

        if(nomeEt.getText().length() == 0 || especieEt.getText().length() == 0 || alturaEt.getText().length() == 0
                || pesoEt.getText().length() == 0 ) {
            Toast.makeText(CadastrarActivity.this, "Insira os dados do pokemon.", Toast.LENGTH_LONG).show();
            valido = false;
        }
        else {
            valido = true;
        }

        if(valido) {
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
                            Toast.makeText(CadastrarActivity.this, "Pokemon inserido", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String jsonError = new String(error.networkResponse.data);
                    JSONObject pokemon = null;
                    String nomeTreinador = null;
                    try {
                        pokemon = new JSONObject(jsonError);
                        nomeTreinador = pokemon.getJSONObject("treinador").get("nome").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastrarActivity.this, "Erro: Pokemon já cadastrado pelo treinador: " + nomeTreinador, Toast.LENGTH_LONG).show();
                }


            });
            request.setTag("tag");
            rq.add(request);
        }

    }

    static final int REQUEST_GALLERY = 0;
    static final int REQUEST_IMAGE_CAPTURE = 0;

    public void dispatchTakePictureIntent(View view) {

        criaAlertFinal("Capturar", "Escolha como capturar seu Pokemon.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(cameraOrGallery==1)
        {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data!=null) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                pathImage = imageToString(imageBitmap);

                image.setImageBitmap(imageBitmap);
            }
        }
        else if(cameraOrGallery==2)
        {
            if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY && data!=null) {
                Uri selectedImage = data.getData();
                Bitmap imageBitmap=null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(imageBitmap);
                pathImage = imageToString(imageBitmap);
            }
        }
        else
        {
            Toast.makeText(CadastrarActivity.this, "Erro: Selecione uma imagem: ", Toast.LENGTH_LONG).show();
        }
    }

    private void criaAlertFinal(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                cameraOrGallery=1;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                cameraOrGallery=2;
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
