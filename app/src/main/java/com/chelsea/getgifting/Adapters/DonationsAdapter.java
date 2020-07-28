package com.chelsea.getgifting.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chelsea.getgifting.Constant;
import com.chelsea.getgifting.DonationDescription;
import com.chelsea.getgifting.EditDonation;
import com.chelsea.getgifting.HomeActivity;
import com.chelsea.getgifting.Models.Donation;
import com.chelsea.getgifting.MyDonations;
import com.chelsea.getgifting.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsAdapter.DonationsHolder>{
    private Context context;
    private ArrayList<Donation>list;
    private ArrayList<Donation>listAll;
    private SharedPreferences preferences;

    public DonationsAdapter(Context context, ArrayList<Donation> list) {
        this.context = context;
        this.list = list;
        this.listAll=new ArrayList<>(list);
        preferences=context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public DonationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DonationsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DonationsHolder holder, int position) {
        Donation donation=list.get(position);
        Picasso.with(context).load(Constant.URL+"storage/donations/"+donation.getPhoto()).into(holder.imgDonation);
        holder.txtUsername.setText(donation.getUser().getUserName());
        holder.txtTypeOfFood.setText(donation.getTypeOfFood());
        holder.txtQuantity.setText(donation.getQuantity());
        holder.txtRequests.setText(donation.getRequests()+" Requests");
        holder.txtDate.setText(donation.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View v) {
             Intent intent =new Intent(context, DonationDescription.class);
             intent.putExtra("donationId",donation.getId());
             intent.putExtra("position",position);
             intent.putExtra("donationPhoto",Constant.URL+"storage/donations/"+donation.getPhoto());
             intent.putExtra("donationTypeOfFood",donation.getTypeOfFood());
             intent.putExtra("donationQuantity",donation.getQuantity());
             intent.putExtra("donorLocation",donation.getLocation());
             intent.putExtra("donationDescription",donation.getDescription());
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             v.getContext().startActivity(intent);

    }
});

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Donation> filteredList=new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            }
            else{
                for(Donation donation:listAll){
                    if(donation.getTypeOfFood().toLowerCase().contains(constraint.toString().toLowerCase())
                            ||donation.getUser().getUserName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(donation);
                    }
                }

            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Donation>) results.values);
            notifyDataSetChanged();
        }
    };
    public Filter getFilter(){
        return filter;
    }

    class DonationsHolder extends RecyclerView.ViewHolder{
        private TextView txtUsername,txtDate,txtRequests,txtTypeOfFood,txtQuantity;
        private ImageView imgDonation;

        public DonationsHolder(@NonNull View itemView){
            super(itemView);
            txtUsername=itemView.findViewById(R.id.donorUsername);
            txtTypeOfFood=itemView.findViewById(R.id.donationFoodType);
            txtQuantity=itemView.findViewById(R.id.donationFoodQuantity);
            txtRequests=itemView.findViewById(R.id.requests);
            txtDate=itemView.findViewById(R.id.timePosted);
            imgDonation=itemView.findViewById(R.id.donationFoodImage);
        }
    }
}
