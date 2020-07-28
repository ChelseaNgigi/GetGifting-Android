package com.chelsea.getgifting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chelsea.getgifting.Models.Donation;
import com.chelsea.getgifting.Models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddDonations extends AppCompatActivity {
    private EditText donationFoodType,donationFoodQuantity,donorLocation,donationDescription;
    private ImageView donationImage;
    private Button addDonation;
    private Bitmap bitmap=null;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;
    private Uri imageUri;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donations);
        preferences=getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        donationFoodType=findViewById(R.id.donationFoodType);
        donationFoodQuantity=findViewById(R.id.donationFoodQuantity);
        donorLocation=findViewById(R.id.location);
        donationDescription=findViewById(R.id.donationDescription);
        addDonation=findViewById(R.id.add_new_donation);
        loadingBar = new ProgressDialog(this);

        donationImage=findViewById(R.id.donation_image);



        addDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });
    }


    public void openGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK && data!=null )
        {
            imageUri = data.getData();
            donationImage.setImageURI(imageUri);
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void ValidateProductData()
    {

        String typeOfFood = donationFoodType.getText().toString();
        String quantity=donationFoodQuantity.getText().toString();
       String location=donorLocation.getText().toString();
       String description=donationDescription.getText().toString();



        if (imageUri == null)
        {
            Toast.makeText(this, "Food donation image is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(typeOfFood))
        {
            Toast.makeText(this, "Please enter type of food being donated...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(quantity))
        {
            Toast.makeText(this, "Please enter product donation food quantity...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location))
        {
            Toast.makeText(this, "Please enter your current location...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please enter the donation food description ...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }
    private void StoreProductInformation() {

        loadingBar.setMessage("Adding Donation");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String typeOfFood = donationFoodType.getText().toString().trim();
        String quantity=donationFoodQuantity.getText().toString().trim();
        String location=donorLocation.getText().toString().trim();
        String description=donationDescription.getText().toString().trim();
        String image=bitmapToString(bitmap);


        Uri.Builder builder=Uri.parse(Constant.ADD_DONATION).buildUpon();
        builder.appendQueryParameter("typeOfFood",typeOfFood);
        builder.appendQueryParameter("quantity",quantity);
        builder.appendQueryParameter("location",location);
        builder.appendQueryParameter("description",description);
        String ADD_DONATION_URL=builder.build().toString();

        StringRequest request =new StringRequest(Request.Method.POST,ADD_DONATION_URL,response -> {
            try {
                JSONObject object=new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject donationObject= object.getJSONObject("donation");
                    JSONObject userObject= donationObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name"));

                    Donation donation=new Donation();
                    donation.setUser(user);
                    donation.setId(donationObject.getInt("id"));
                    donation.setSelfRequest(false);
                    donation.setPhoto(donationObject.getString("photo"));
                    donation.setTypeOfFood(donationObject.getString("typeOfFood"));
                    donation.setQuantity(donationObject.getString("quantity"));
                    donation.setLocation(donationObject.getString("location"));
                    donation.setDescription(donationObject.getString("description"));
                    donation.setRequests(0);
                    donation.setDate(donationObject.getString("created_at"));

                    HomeActivity.arrayList.add(0,donation);
                    HomeActivity.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeActivity.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Donation successful..Thank you for donating", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                loadingBar.dismiss();

        },error->{
                error.printStackTrace();
                loadingBar.dismiss();
        }){
            //Add token header
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                String token =preferences.getString("token","");
                HashMap<String,String> map=new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
        //Add param
            @Override
            protected Map<String,String>getParams() throws AuthFailureError{
                HashMap<String,String> map=new HashMap<>();
                map.put("typeOfFood",donationFoodType.getText().toString().trim());
                map.put("quantity",donationFoodQuantity.getText().toString().trim());
                map.put("location",donorLocation.getText().toString().trim());
                map.put("description",donationDescription.getText().toString().trim());
                map.put("photo",bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(AddDonations.this);
        queue.add(request);
    }
    private String bitmapToString(Bitmap bitmap){
        if(bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[]array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }



}