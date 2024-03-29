package id.technoit.dev.appiresto.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.technoit.dev.appiresto.DaftarMahasiswa;
import id.technoit.dev.appiresto.Main2Activity;
import id.technoit.dev.appiresto.R;
import id.technoit.dev.appiresto.TransActivity;
import id.technoit.dev.appiresto.home;
import id.technoit.dev.appiresto.transaksi;

public class LoginActivity extends AppCompatActivity {

    // deklarasi objek
    TextInputLayout validasiIDUser, validasiPassword;
    EditText txtIDUser, txtPassword;
    Button btnLogin;
    TextView txtRegistrasi;

    // deklarasi variabel
    String username, password;

    // deklarasi variabel alamat host
    public static String URL = "http://192.168.43.139/project_rental/api_mobil/index.php/api/api_login";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // inisialisasi variabel objek
        validasiIDUser = findViewById(R.id.validasiIDUser);
        validasiPassword = findViewById(R.id.validasiPassword);
        txtIDUser = findViewById(R.id.txtIDUser);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegistrasi = findViewById(R.id.txtRegister);

        // jika tombol login diklik
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = txtIDUser.getText().toString().trim();
                password = txtPassword.getText().toString().trim();

                if(username.isEmpty()){
                    validasiIDUser.setError("ID. User harus diisi!");
                }else if(password.isEmpty()){
                    validasiIDUser.setError("Password harus diisi!");
                }else{
                    auth_user(username, password);
                }
            }
        });

        // jika tombol register diklik
        txtRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sintak untuk pindah activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

    // method login
    private void auth_user(final String username, final String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if(success.equals("1")){
                                for(int i=0 ; i<jsonArray.length() ; i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    String nama= jsonObject1.getString("nama").trim();

                                    sessionManager.createSession(username, nama);

                                    Toast.makeText(LoginActivity.this,
                                            "Login berhasil ! \n Nama : "+nama,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    LoginActivity.this.startActivity(intent);


                                }
                            }else{
                                Toast.makeText(LoginActivity.this,
                                        "Password tidak ditemukan! ",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "Id User dan Password salah",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,
                                "Error login : " + error.toString(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("username", username);     // sesuaikan dengan $_POST pada PHP
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
