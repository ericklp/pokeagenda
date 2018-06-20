package max.com.br.loginexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ExibirPokemonActivity extends AppCompatActivity {

    private EditText nomeEt, pesoEt, alturaEt, especieEt, nomeTreinadorEt;
    private ImageView image;
    private Treinador treinador;
    private String nomePokemon;
    private Pokemon poke;
    String url;
    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_pokemon);

        Intent it = getIntent();

        rq = Volley.newRequestQueue(ExibirPokemonActivity.this);

        poke = new Pokemon();
        poke = (Pokemon) it.getSerializableExtra("pokemon");
        String nomeTreinador = it.getStringExtra("nomeTreinador");
        treinador = new Treinador();
        treinador = (Treinador) it.getSerializableExtra("treinador");

        TextView tv = findViewById(R.id.bemvindo);
        nomeEt = (EditText) findViewById(R.id.nomePokemonEt);
        pesoEt = (EditText) findViewById(R.id.pesoPokemonEt);
        especieEt = (EditText) findViewById(R.id.especiePokemonEt);
        alturaEt = (EditText) findViewById(R.id.alturaPokemonEt);
        nomeTreinadorEt = (EditText) findViewById(R.id.nomeTreinadorEt);
        image = (ImageView) findViewById(R.id.imagemPokemon);

        nomeEt.setText(poke.getNome());
        especieEt.setText(poke.getEspecie());
        pesoEt.setText(Float.toString(poke.getPeso()));
        alturaEt.setText(Float.toString(poke.getAltura()));
        nomeTreinadorEt.setText(nomeTreinador);
        image.setImageBitmap(stringToBitMap(poke.getImagem()));

    }

    public void escolherPokemonFavorito(View view)
    {
        String ip = Sistema.IP;
        String post_url = "http://" + ip + ":8080/SistemaCentral/webresources/treinador";

        treinador.setId(treinador.getId());
        treinador.setLogin(treinador.getLogin());
        treinador.setNome(treinador.getNome());
        treinador.setSenha(treinador.getSenha());
        treinador.setId_preferido(poke.getId());

        Gson gson = new Gson();
        String jsonInString = gson.toJson(treinador);
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
                        Toast.makeText(ExibirPokemonActivity.this, "Treinador atualizado", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
                Toast.makeText(ExibirPokemonActivity.this, "Erro: Não foi possível atualizar: ", Toast.LENGTH_LONG).show();
            }
        });
        request.setTag("tag");
        rq.add(request);
    }

    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
