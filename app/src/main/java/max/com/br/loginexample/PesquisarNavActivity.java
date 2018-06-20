package max.com.br.loginexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PesquisarNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText nomePokemon;
    private RequestQueue rq;
    private Treinador treinador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar_nav);
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

        nomePokemon = findViewById(R.id.nomePokePesqEt);
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

    public void pesquisar(View view) {
        String nome = nomePokemon.getText().toString();
        if(nome.length()!=0)
        {
            rq = Volley.newRequestQueue(PesquisarNavActivity.this);

            final String ip = Sistema.IP;
            String url = "http://"+ip+":8080/SistemaCentral/webresources/pokemon/";

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

                                Intent it = new Intent(PesquisarNavActivity.this, ExibirPokemonActivity.class);
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
                        Toast.makeText(PesquisarNavActivity.this, "Erro: Pokemon não encontrado", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(PesquisarNavActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

            request.setTag("tag");
            rq.add(request);
        }
        else
            Toast.makeText(PesquisarNavActivity.this, "Erro: insira um nome", Toast.LENGTH_LONG).show();

    }
}
