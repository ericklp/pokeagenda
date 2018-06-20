package max.com.br.loginexample;

import java.util.Map;

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

public class MainActivity extends Activity {
    private RequestQueue rq;
    private Map<String, String> params;
    private EditText etEmail;
    private EditText etPassword;
    private String url;
    private Treinador treinador;
    public static String ip = Sistema.IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://"+ip+":8080/SistemaCentral/webresources/treinador/";

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        rq = Volley.newRequestQueue(MainActivity.this);
    }

    public void callByStringRequest(View view) {
        // RECEBE do webservice, via GET, os atributos do treinador
        Button btn = (Button) findViewById(R.id.button);
        Boolean valido;

        if(etEmail.getText().length() == 0 || etPassword.getText().length() == 0 ) {
            Toast.makeText(MainActivity.this, "Insira o login e senha", Toast.LENGTH_LONG).show();
            valido = false;
        }
        else {
            valido = true;
        }

        if(valido) {
            String ws_url = url + etEmail.getText().toString() + "/" + etPassword.getText().toString();
            JsonObjectRequest request = new JsonObjectRequest(ws_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Script", "SUCCESS: " + response.toString());
                            String name = null;
                            String senha = null;
                            String login = null;
                            int id = 1;
                            int id_pref = 1;
                            try {
                                name = (String) response.get("nome");
                                senha = (String) response.get("senha");
                                login = (String) response.get("login");
                                id = (int) response.get("id");
                                id_pref = (int) response.get("id_preferido");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            treinador = new Treinador();
                            treinador.setNome(name);
                            treinador.setSenha(senha);
                            treinador.setLogin(login);
                            treinador.setId(id);
                            treinador.setId_preferido(id_pref);
                            System.out.println("Nome:" + name + " senha:" + senha + " login:" + login);

                            if (etPassword.getText().toString().equals(senha)) {
                                Intent it = new Intent(MainActivity.this, InitialActivity.class);
                                it.putExtra("treinador", treinador);
                                startActivity(it);
                            }
                            else
                                Toast.makeText(MainActivity.this, "Erro: Senha incorreta", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 404)
                        Toast.makeText(MainActivity.this, "Erro: Usuário ou senha incorreta", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }


            });
            request.setTag("tag");
            rq.add(request);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        rq.cancelAll("tag");
    }
}
