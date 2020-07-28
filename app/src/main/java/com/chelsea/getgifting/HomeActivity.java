package com.chelsea.getgifting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chelsea.getgifting.Adapters.DonationsAdapter;
import com.chelsea.getgifting.Models.Donation;
import com.chelsea.getgifting.Models.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;



public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static RecyclerView recyclerView;
    //RecyclerView.LayoutManager layoutManager;
    public static ArrayList<Donation> arrayList;
    private DonationsAdapter donationsAdapter;
    private SharedPreferences sharedPreferences;
    private TextView userProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("DONATIONS");
        setSupportActionBar(toolbar);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddDonations.class);
                    startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        userProfileName=headerView.findViewById(R.id.userProfileName);
        CircleImageView userProfileImage =headerView.findViewById(R.id.userProfileImage);

        sharedPreferences=getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList=new ArrayList<>();
        getDonations();



    }

    private void getDonations(){

        StringRequest request =new StringRequest(Request.Method.GET,Constant.DONATIONS, response->{
            try{
                JSONObject object=new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray array=new JSONArray(object.getString("donations"));
                    for(int i=0;i<array.length();i++){
                        JSONObject donationObject=array.getJSONObject(i);
                        JSONObject userObject= donationObject.getJSONObject("user");

                        User user=new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name"));
                        userProfileName.setText(userObject.getString("name"));


                        Donation donation=new Donation();
                        donation.setId(donationObject.getInt("id"));
                        donation.setUser(user);
                        donation.setTypeOfFood(donationObject.getString("typeOfFood"));
                        donation.setQuantity(donationObject.getString("quantity"));
                        donation.setDescription(donationObject.getString("description"));
                        donation.setPhoto(donationObject.getString("photo"));
                        donation.setLocation(donationObject.getString("location"));
                        donation.setDate(donationObject.getString("created_at"));
                        donation.setRequests(donationObject.getInt("requestCount"));
                        donation.setSelfRequest(donationObject.getBoolean("selfDonationRequest"));

                        arrayList.add(donation);
                    }


                    donationsAdapter=new DonationsAdapter(getApplicationContext(),arrayList);
                    recyclerView.setAdapter(donationsAdapter);
                }
            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, "Error displaying all donations" + e.toString(), Toast.LENGTH_SHORT).show();
            }

        },error->{
            error.printStackTrace();
            Toast.makeText(HomeActivity.this, "Error displaying all donations" + error.toString(), Toast.LENGTH_SHORT).show();
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
            RequestQueue queue= Volley.newRequestQueue(HomeActivity.this);
            queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                donationsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_home)
        {

        }

        else if (id == R.id.nav_myDonations)
        {
                Intent intent = new Intent(HomeActivity.this, MyDonations.class);
                startActivity(intent);
        }
        else if (id == R.id.nav_myRequests)
        {

        }
        else if (id == R.id.nav_logout)
        {

                logout();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        StringRequest request=new StringRequest(Request.Method.GET,Constant.LOGOUT,res-> {
            try{
                JSONObject object=new JSONObject(res);
                if(object.getBoolean("success")){
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Paper.book().destroy();
                    Intent intent=new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error->{
            error.printStackTrace();

        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                String token=sharedPreferences.getString("token","");
                HashMap<String,String> map=new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(HomeActivity.this);
        queue.add(request);
    }

}