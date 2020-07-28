package com.chelsea.getgifting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private MaterialButton btnLogin;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.login_email);
        edtPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.user_btnLogin);
        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setMessage("Logging In..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validateLogin();

        }
    }

    private void validateLogin() {

        final String email = this.edtEmail.getText().toString().trim();
        final String password = this.edtPassword.getText().toString().trim();

        Uri.Builder builder=Uri.parse(Constant.LOGIN).buildUpon();
        builder.appendQueryParameter("email",email);
        builder.appendQueryParameter("password",password);
        String LOGIN_URL=builder.build().toString();

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONObject user = object.getJSONObject("user");
                                //Make shared preferences
                                SharedPreferences userPref =getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();
                                editor.putString("token", object.getString("token"));
                                editor.putString("name", user.getString("name"));
                                editor.putInt("id", user.getInt("id"));
                                editor.apply();
                                //IF Success
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                loadingBar.dismiss();

                            }
                        }catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Login failed" + ex.toString(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Login failed" + error.toString(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        //Add this request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}