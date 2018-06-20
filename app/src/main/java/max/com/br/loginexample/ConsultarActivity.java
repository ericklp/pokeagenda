package max.com.br.loginexample;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConsultarActivity extends AppCompatActivity {
    private Treinador treinador;
    private ImageView image;
    private ListView list;
    private RequestQueue rq;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);

        mDialog = new Dialog(this);

        Intent it = getIntent();

        treinador = (Treinador) it.getSerializableExtra("treinador");

        rq = Volley.newRequestQueue(ConsultarActivity.this);

        String url = "http://"+MainActivity.ip+":8080/SistemaCentral/webresources/pokemons";

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Script", "SUCCESS: " + response.toString());
                        ArrayList<JSONObject> arrayList = new ArrayList(response.length());
                        for (int i=0;i < response.length();i++) {
                            try {
                                arrayList.add(response.getJSONObject(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayList<Pokemon> pokemonArray = new ArrayList<>();
                        for (int i=0; i < arrayList.size() ; i++) {
                            Pokemon poke = new Pokemon();
                            try {
                                poke.setId( Integer.parseInt(arrayList.get(i).get("id").toString()));
                                poke.setNome((String) arrayList.get(i).get("nome"));
                                poke.setEspecie((String) arrayList.get(i).get("especie"));
                                poke.setAltura(Float.parseFloat(arrayList.get(i).get("altura").toString()));
                                poke.setPeso(Float.parseFloat(arrayList.get(i).get("peso").toString()));
                                poke.setImagem((String) arrayList.get(i).get("imagem"));
                                Treinador tmp_treinador = new Treinador();
                                tmp_treinador.setNome((String)arrayList.get(i).getJSONObject("treinador").get("nome"));
                                poke.setTreinador(tmp_treinador);
                                pokemonArray.add(poke);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        final ListCell adapter = new ListCell(ConsultarActivity.this, pokemonArray);
                        list=findViewById(R.id.list);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //Toast.makeText(MainActivity.this , "Clicou na "+time[position], Toast.LENGTH_SHORT).show();
                                        openDialog();
                                        Intent it = new Intent(ConsultarActivity.this, ExibirPokemonActivity.class);
                                        //Object obj = MainActivity.this.getListAdapter().getItem(position);
                                        Pokemon poke = (Pokemon)parent.getItemAtPosition(position);
                                        it.putExtra("pokemon", poke);
                                        it.putExtra("nomeTreinador", poke.getTreinador().getNome());
                                        it.putExtra("treinador", treinador);
                                        startActivity(it);
                                    }
                                }
                        );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.i("Script", "error: " + error.getMessage());
                    Toast.makeText(ConsultarActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }


        });

        request.setTag("tag");
        rq.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDialog.dismiss();
    }

    public void openDialog(){
        mDialog = new Dialog(this);
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog);
        //DEixamos transparente
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // não permitimos fechar esta dialog
        mDialog.setCancelable(false);
        //temos a instancia do ProgressBar!
        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));

        mDialog.show();
    }
}
