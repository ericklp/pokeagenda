package max.com.br.loginexample;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InitialActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Treinador treinador;
    private Pokemon poke;
    private ImageView image;
    private RequestQueue rq;
    AlertDialog alerta;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        poke = new Pokemon();

        mDialog = new Dialog(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent it = getIntent();

        treinador = new Treinador();
        treinador = (Treinador) it.getSerializableExtra("treinador");

        TextView tv = findViewById(R.id.bemvindo);
        image = findViewById(R.id.imageViewPokePref);

        tv.setText("Olá, seja bem vindo "+ treinador.getNome()+"!");

        pesquisar(treinador.getId_preferido());

    }

    public void pesquisar(int id) {
        if(id!=0)
        {
            final Pokemon pokemon = new Pokemon();
            rq = Volley.newRequestQueue(InitialActivity.this);

            String url = "http://"+MainActivity.ip+":8080/SistemaCentral/webresources/pokemons/";

            String ws_url = url + id;
            JsonObjectRequest request = new JsonObjectRequest(ws_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Script", "SUCCESS: " + response.toString());
                            try {
                                poke.setNome((String) response.get("nome"));
                                poke.setEspecie((String) response.get("especie"));
                                poke.setAltura(Float.parseFloat(response.get("altura").toString()));
                                poke.setPeso(Float.parseFloat(response.get("peso").toString()));
                                poke.setImagem((String) response.get("imagem"));
                                image.setImageBitmap(stringToBitMap(poke.getImagem()));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 404)
                        Toast.makeText(InitialActivity.this, "Erro: Pokemon não encontrado", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(InitialActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }


            });

            request.setTag("tag");
            rq.add(request);
        }
        else
        {
            Toast.makeText(InitialActivity.this, "Escolha o pokemon preferido", Toast.LENGTH_LONG).show();
        }
    }

    public void pesquisarTreinador() {
        String nome = treinador.getNome();
        if(nome.length()!=0)
        {
            rq = Volley.newRequestQueue(InitialActivity.this);

            String url = "http://"+MainActivity.ip+":8080/SistemaCentral/webresources/treinador/";

            String ws_url = url + nome;
            JsonObjectRequest request = new JsonObjectRequest(ws_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Script", "SUCCESS: " + response.toString());
                            try {

                                treinador.setId((int) response.get("id"));
                                treinador.setLogin((String) response.get("login"));
                                treinador.setNome((String) response.get("nome"));
                                treinador.setSenha((String) response.get("senha"));
                                treinador.setId_preferido((int) response.get("id_preferido"));

                                pesquisar(treinador.getId_preferido());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 404)
                        Toast.makeText(InitialActivity.this, "Erro: Treinador não encontrado", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(InitialActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            request.setTag("tag");
            rq.add(request);
        }
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
            criaAlertFinal("Pokemon App","Deseja realmente sair?");
        } else if (id == R.id.nav_cadastrar) {
            Intent it = new Intent(this, CadastrarActivity.class);
            it.putExtra("treinador", treinador);
            startActivity(it);
        } else if (id == R.id.nav_consultar) {
            openDialog();
            Intent it = new Intent(this, ConsultarActivity.class);
            it.putExtra("treinador", treinador);
            startActivity(it);

        } else if (id == R.id.nav_pesquisar) {
            Intent it = new Intent(this, PesquisarActivity.class);
            it.putExtra("treinador", treinador);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void criaAlertFinal(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        alerta = builder.create();
        alerta.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pesquisarTreinador();
        System.out.println("TREINADOR PASSANDO POR AQUI NOVAMENTE: " + treinador.getId_preferido());
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
