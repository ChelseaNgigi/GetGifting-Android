package com.chelsea.getgifting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chelsea.getgifting.Adapters.DonationsAdapter;
import com.chelsea.getgifting.Adapters.MyDonationsAdapter;
import com.chelsea.getgifting.Models.Donation;
import com.chelsea.getgifting.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyDonations extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static ArrayList<Donation> arrayList;
    private MyDonationsAdapter myDonationsAdapter;
    private SharedPreferences sharedPreferences;
    private TextView donationsCount,donations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        sharedPreferences=getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        donationsCount=findViewById(R.id.numberOfDonations);
        donations=findViewById(R.id.donations);

        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList=new ArrayList<>();
        getMyDonations();

    }

    private void getMyDonations() {
        StringRequest request =new StringRequest(Request.Method.GET,Constant.MY_DONATIONS, response->{

            try{
                JSONObject object=new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray donations=object.getJSONArray("donations");
                    for(int i=0;i<donations.length();i++){
                        JSONObject d=donations.getJSONObject(i);

                        Donation donation=new Donation();
                        donation.setId(d.getInt("id"));
                        donation.setTypeOfFood(d.getString("typeOfFood"));
                        donation.setQuantity(d.getString("quantity"));
                        donation.setDescription(d.getString("description"));
                        donation.setPhoto(Constant.URL+"storage/donations/"+d.getString("photo"));
                        donation.setLocation(d.getString("location"));
                        donation.setDate(d.getString("created_at"));
                        donation.setRequests(d.getInt("requestCount"));


                        arrayList.add(donation);
                    }
                    donationsCount.setText(arrayList.size()+"");
                    //donationsCount.setText(Collections.frequency(arrayList,"donation"));
                    myDonationsAdapter=new MyDonationsAdapter(getApplicationContext(),arrayList);
                    recyclerView.setAdapter(myDonationsAdapter);
                }
            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(MyDonations.this, "Error displaying your donations" + e.toString(), Toast.LENGTH_SHORT).show();
            }

        },error->{
            error.printStackTrace();
            Toast.makeText(MyDonations.this, "Error displaying your donations" + error.toString(), Toast.LENGTH_SHORT).show();
        }){
            //Provide token in header
            @Override
            public Map<String,String> getHeaders()throws AuthFailureError {
                String token=sharedPreferences.getString("token","");
                HashMap<String,String> map=new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(MyDonations.this);
        queue.add(request);
    }

}