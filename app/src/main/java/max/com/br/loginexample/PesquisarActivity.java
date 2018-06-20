package max.com.br.loginexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PesquisarActivity extends AppCompatActivity {

    private EditText nomePokemon;
    private RequestQueue rq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);
        nomePokemon = findViewById(R.id.nomePokePesqEt);
    }

    public void pesquisar(View view) {
        String nome = nomePokemon.getText().toString();
        if(nome.length()!=0)
        {
            rq = Volley.newRequestQueue(PesquisarActivity.this);

            String url = "http://"+MainActivity.ip+":8080/SistemaCentral/webresources/pokemon/";

            String ws_url = url + nomePokemon.getText().toString();
            JsonObjectRequest request = new JsonObjectRequest(ws_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Script", "SUCCESS: " + response.toString());
                            try {

                                Pokemon poke = new Pokemon();

                                poke.setNome((String) response.get("nome"));
                                poke.setEspecie((String) response.get("especie"));
                                poke.setAltura(Float.parseFloat(response.get("altura").toString()));
                                poke.setPeso(Float.parseFloat(response.get("peso").toString()));
                                poke.setImagem((String) response.get("imagem"));

                                Intent it = new Intent(PesquisarActivity.this, ExibirPokemonActivity.class);
                                it.putExtra("pokemon", poke);
                                it.putExtra("nomeTreinador", (String)response.getJSONObject("treinador").get("nome"));
                                startActivity(it);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 404)
                        Toast.makeText(PesquisarActivity.this, "Erro: Pokemon não encontrado", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(PesquisarActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

            request.setTag("tag");
            rq.add(request);
        }
        else
            Toast.makeText(PesquisarActivity.this, "Erro: insira um nome", Toast.LENGTH_LONG).show();

    }
}
