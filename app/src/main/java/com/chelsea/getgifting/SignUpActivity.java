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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtUsername,edtEmail,edtPassword,edtConfirmPassword;
    private MaterialButton btnSignUp;
    private Context context;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtUsername=findViewById(R.id.user_username);
        edtEmail=findViewById(R.id.user_email);
        edtPassword=findViewById(R.id.user_password);
        edtConfirmPassword=findViewById(R.id.user_confirm_password);
        btnSignUp=findViewById(R.id.user_btnSignUp);
        loadingBar = new ProgressDialog(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }
    private void signUpUser()
    {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Username must be provided", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Email address must be provided", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Required at least 8 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.equals(password,confirmPassword))
        {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setMessage("Signing Up...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validateSignUp();
        }
    }
    private void validateSignUp(){

        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();


        Uri.Builder builder=Uri.parse(Constant.REGISTER).buildUpon();
        builder.appendQueryParameter("name",username);
        builder.appendQueryParameter("email",email);
        builder.appendQueryParameter("password",password);
        String REGISTER_URL=builder.build().toString();

        StringRequest request =new StringRequest(Request.Method.POST,REGISTER_URL, response->{
            //If connection is a success
            try{
                JSONObject object =new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user=object.getJSONObject("user");
                    //Make shared preferences
                    SharedPreferences userPref=this.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=userPref.edit();
                    editor.putString("token",object.getString("token"));
                    editor.putString("name",user.getString("name"));
                    editor.putInt("id",user.getInt("id"));
                    editor.apply();
                    //IF Success
                    Toast.makeText(this, "Signed Up successfully", Toast.LENGTH_SHORT).show();



                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
            loadingBar.dismiss();


        },error->{
            //If connection is not a success
            error.printStackTrace();
            Toast.makeText(this, "Signed Up failed", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();


        }){
            //Add parameters
            @Override
            protected Map<String,String> getParams(){
                HashMap<String,String> map= new HashMap<>();
                map.put("name",edtUsername.getText().toString().trim());
                map.put("email",edtEmail.getText().toString().trim());
                map.put("password",edtPassword.getText().toString());
                return map;
            }
            //Add headers
            @Override
            public Map<String,String>getHeaders() throws AuthFailureError{
                HashMap<String,String> headers =new HashMap<>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }

        };
        //Add this request to request queue
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        queue.add(request);
    }
}