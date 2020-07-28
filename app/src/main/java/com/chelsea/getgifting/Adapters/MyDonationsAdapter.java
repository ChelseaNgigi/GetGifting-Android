package com.chelsea.getgifting.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chelsea.getgifting.Constant;
import com.chelsea.getgifting.EditDonation;
import com.chelsea.getgifting.HomeActivity;
import com.chelsea.getgifting.Models.Donation;
import com.chelsea.getgifting.MyDonations;
import com.chelsea.getgifting.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class MyDonationsAdapter extends RecyclerView.Adapter<MyDonationsAdapter.MyDonationsHolder> {
    private Context context;
    private ArrayList<Donation> list;
    private SharedPreferences preferences;


    public MyDonationsAdapter(Context context, ArrayList<Donation> list) {
        this.context = context;
        this.list = list;
        preferences=context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public MyDonationsAdapter.MyDonationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyDonationsAdapter.MyDonationsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mydonation_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyDonationsAdapter.MyDonationsHolder holder, int position) {
        Donation donation=list.get(position);
        Picasso.with(context).load(donation.getPhoto()).into(holder.imgDonation);
        holder.txtTypeOfFood.setText(donation.getTypeOfFood());
        holder.txtQuantity.setText(donation.getQuantity());
        holder.txtRequests.setText(donation.getRequests()+" Requests");
        holder.txtDate.setText(donation.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]{
                        "Edit" ,
                        "Delete"
                };
                AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                builder.setTitle("Donation Options:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0) {
                            Intent intent =new Intent(context, EditDonation.class);
                            intent.putExtra("donationId",donation.getId());
                            intent.putExtra("position",position);
                            intent.putExtra("donationPhoto",donation.getPhoto());
                            intent.putExtra("donationTypeOfFood",donation.getTypeOfFood());
                            intent.putExtra("donationQuantity",donation.getQuantity());
                            intent.putExtra("donorLocation",donation.getLocation());
                            intent.putExtra("donationDescription",donation.getDescription());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }
                            if (i == 1) {
                                deletePost(donation.getId(),position);

                             }
                    }
                });
                builder.show();
            }
        });
    }

    private void deletePost(int donationId,int position){

                Uri.Builder builder=Uri.parse(Constant.DELETE_DONATION).buildUpon();
                builder.appendQueryParameter("id",donationId+"");
                String DELETE_DONATION_URL=builder.build().toString();
                StringRequest request=new StringRequest(Request.Method.POST,DELETE_DONATION_URL,response-> {
                    try{
                        JSONObject object=new JSONObject(response);
                        if(object.getBoolean("success")){
                            list.remove(position);
                            notifyItemRemoved(position);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error deleting the donations" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                },error->{
                    error.printStackTrace();
                    Toast.makeText(context, "Error deleting the donations" + error.toString(), Toast.LENGTH_SHORT).show();

                }){
                    @Override
                    public Map<String,String> getHeaders() throws AuthFailureError{
                        String token= preferences.getString("token","");
                        HashMap<String,String>map=new HashMap<>();
                        return map;
                    }
                    @Override
                    protected Map<String,String>getParams() throws AuthFailureError{
                        HashMap<String,String> map=new HashMap<>();
                        map.put("id",donationId+"");
                        return map;
                    }
                };
                RequestQueue queue= Volley.newRequestQueue(context);
                queue.add(request);


    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyDonationsHolder extends RecyclerView.ViewHolder{
        private TextView txtDate,txtRequests,txtTypeOfFood,txtQuantity;
        private ImageView imgDonation;

        public MyDonationsHolder(@NonNull View itemView){
            super(itemView);

            txtTypeOfFood=itemView.findViewById(R.id.myDonationFoodType);
            txtQuantity=itemView.findViewById(R.id.myDonationFoodQuantity);
            txtRequests=itemView.findViewById(R.id.myRequests);
            txtDate=itemView.findViewById(R.id.myTimePosted);
            imgDonation=itemView.findViewById(R.id.myDonationFoodImage);
        }
    }
}


