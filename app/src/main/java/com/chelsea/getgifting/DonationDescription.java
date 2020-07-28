package com.chelsea.getgifting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chelsea.getgifting.Models.Donation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DonationDescription extends AppCompatActivity {
    private TextView donationFoodType,donationFoodQuantity,donorLocation,donationDescription;
    private ImageView donationImage;
    private Button requestDonation;
    private SharedPreferences preferences;
    private int position=0,id=0;
    int imagelink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_description);
        preferences=getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        donationFoodType=findViewById(R.id.donationDescriptionFoodType);
        donationFoodQuantity=findViewById(R.id.donationDescriptionFoodQuantity);
        donorLocation=findViewById(R.id.descriptionLocation);
        donationDescription=findViewById(R.id.donationDescription);
        donationImage=findViewById(R.id.donation_description_image);

        position=getIntent().getIntExtra("position",0);
        id=getIntent().getIntExtra("donationId",0);

        Picasso.with(DonationDescription.this).load(getIntent().getStringExtra("donationPhoto")).into(donationImage);
        donationFoodType.setText(getIntent().getStringExtra("donationTypeOfFood"));
        donationFoodQuantity.setText(getIntent().getStringExtra("donationQuantity"));
        donorLocation.setText(getIntent().getStringExtra("donorLocation"));
        donationDescription.setText(getIntent().getStringExtra("donationDescription"));

        //storeDescriptionInfo();
    }
    private void storeDescriptionInfo() {

        String typeOfFood = donationFoodType.getText().toString().trim();
        String quantity=donationFoodQuantity.getText().toString().trim();
        String location=donorLocation.getText().toString().trim();
        String description=donationDescription.getText().toString().trim();


        Uri.Builder builder=Uri.parse(Constant.DONATIONS).buildUpon();
        builder.appendQueryParameter("id",id+"");
        String DONATION_DESCRIPTION_URL=builder.build().toString();

        StringRequest request =new StringRequest(Request.Method.GET,DONATION_DESCRIPTION_URL, response -> {
            try {
                JSONObject object=new JSONObject(response);
                if(object.getBoolean("success")){
                    //Update the donation in the recycler view

                    Donation donation=HomeActivity.arrayList.get(position);
                    donation.setTypeOfFood(donationFoodType.getText().toString());
                    donation.setQuantity(donationFoodQuantity.getText().toString());
                    donation.setLocation(donorLocation.getText().toString());
                    donation.setDescription(donationDescription.getText().toString());
                    



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error->{
            error.printStackTrace();

        }){
            //Add token header
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                String token =preferences.getString("token","");
                HashMap<String,String> map=new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
            //Addparam
            @Override
            protected Map<String,String>getParams() throws AuthFailureError{
                HashMap<String,String> map=new HashMap<>();
                map.put("id",id+"");
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(DonationDescription.this);
        queue.add(request);
    }


}