package com.chelsea.getgifting;

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

public class EditDonation extends AppCompatActivity {
    private EditText donationFoodType,donationFoodQuantity,donorLocation,donationDescription;
    private ImageView donationImage;
    private Button editDonation;
    private Bitmap bitmap=null;
    private static final int GalleryEditPick = 2;
    private ProgressDialog loadingBar;
    private Uri imageUri;
    private SharedPreferences preferences;
    private int position=0,id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation);
        preferences=getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        donationFoodType=findViewById(R.id.donationFoodType);
        donationFoodQuantity=findViewById(R.id.donationFoodQuantity);
        donorLocation=findViewById(R.id.location);
        donationDescription=findViewById(R.id.donationDescription);
        editDonation=findViewById(R.id.save_donation);
        loadingBar = new ProgressDialog(this);
        donationImage=findViewById(R.id.donation_edit_image);

        position=getIntent().getIntExtra("position",0);
        id=getIntent().getIntExtra("donationId",0);

        Picasso.with(EditDonation.this).load(getIntent().getStringExtra("donationPhoto")).into(donationImage);
        donationFoodType.setText(getIntent().getStringExtra("donationTypeOfFood"));
        donationFoodQuantity.setText(getIntent().getStringExtra("donationQuantity"));
        donorLocation.setText(getIntent().getStringExtra("donorLocation"));
        donationDescription.setText(getIntent().getStringExtra("donationDescription"));


        editDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });
    }

    public void openGallery(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryEditPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryEditPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imageUri = data.getData();
            donationImage.setImageURI(imageUri);
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
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

        loadingBar.setMessage("Editing Donation");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String typeOfFood = donationFoodType.getText().toString().trim();
        String quantity=donationFoodQuantity.getText().toString().trim();
        String location=donorLocation.getText().toString().trim();
        String description=donationDescription.getText().toString().trim();
        String image=bitmapToString(bitmap);

        Uri.Builder builder=Uri.parse(Constant.UPDATE_DONATION).buildUpon();
        builder.appendQueryParameter("id",id+"");
        builder.appendQueryParameter("typeOfFood",typeOfFood);
        builder.appendQueryParameter("quantity",quantity);
        builder.appendQueryParameter("location",location);
        builder.appendQueryParameter("description",description);
        String UPDATE_DONATION_URL=builder.build().toString();

        StringRequest request =new StringRequest(Request.Method.POST,UPDATE_DONATION_URL, response -> {
            try {
                JSONObject object=new JSONObject(response);
                if(object.getBoolean("success")){
                    //Update the donation in the recycler view
                    Donation myDonation=MyDonations.arrayList.get(position);
                    myDonation.setTypeOfFood(donationFoodType.getText().toString());
                    myDonation.setQuantity(donationFoodQuantity.getText().toString());
                    myDonation.setLocation(donorLocation.getText().toString());
                    myDonation.setDescription(donationDescription.getText().toString());
                    MyDonations.arrayList.set(position,myDonation);
                    MyDonations.recyclerView.getAdapter().notifyItemChanged(position);
                    MyDonations.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                    Donation donation=HomeActivity.arrayList.get(position);
                    donation.setTypeOfFood(donationFoodType.getText().toString());
                    donation.setQuantity(donationFoodQuantity.getText().toString());
                    donation.setLocation(donorLocation.getText().toString());
                    donation.setDescription(donationDescription.getText().toString());
                    HomeActivity.arrayList.set(position,donation);
                    HomeActivity.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeActivity.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditDonation.this, "Error editing your donations" + e.toString(), Toast.LENGTH_SHORT).show();
            }
            loadingBar.dismiss();

        },error->{
            error.printStackTrace();
            Toast.makeText(EditDonation.this, "Error editing your donations" + error.toString(), Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
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
                map.put("typeOfFood",donationFoodType.getText().toString().trim());
                map.put("quantity",donationFoodQuantity.getText().toString().trim());
                map.put("location",donorLocation.getText().toString().trim());
                map.put("description",donationDescription.getText().toString().trim());
                map.put("photo",bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(EditDonation.this);
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