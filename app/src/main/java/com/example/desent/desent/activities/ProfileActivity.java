package com.example.desent.desent.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.SessionManagement;
import com.example.desent.desent.utils.Utility;

import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    SharedPreferences sharedPreferences;
    TextView name, tvAvgCf, tv_coin_score,
            tvProgress, tvGoalExplanation,
            email, address, zipcode, city,
            birthdate, gender;
    ImageView changeInfo, profilePic;
    LinearLayout ll_coin_score;
    DrawerLayout drawer;
    ProgressBar personalGoal;
    Indicator indicator;
    String personalGoalString = "";
    EditText editTextPersonalGoal;

    private int progressCount = 0;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setUpNavigationView();

        editTextPersonalGoal = (EditText)findViewById(R.id.et_personalGoal);

        changeInfo = findViewById(R.id.changeUserInfo);
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });

        profilePic = findViewById(R.id.imgProfilePicture);

        Uri imageUri = null;
        try {
            imageUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_profile_picture", "android.resource://com.example.desent.desent/drawable/earth"));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
            profilePic.setImageBitmap(Utility.getCroppedBitmap(bitmap));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        name = findViewById(R.id.tvName);
        name.setText(sharedPreferences.getString("pref_key_personal_name",""));

        tvAvgCf = findViewById(R.id.avgCF);
        tvAvgCf.setText("Avg. carbon footprint: ");

        tvGoalExplanation = findViewById(R.id.tvExplanation);

        tv_coin_score = findViewById(R.id.tv_coin_score);
        tv_coin_score.setText(String.valueOf(sharedPreferences.getInt("pref_key_personal_score",0)));

        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Today you have received Earth Coins (EC) from:")
                .setMessage("Walking:" + "\n" +
                "3 km: 1 EC" + "\n" +
                "8 km: 2 EC" + "\n" + "\n" +
                "Cycling:" + "\n" +
                "3 km: 1 EC" + "\n" +
                "8 km: 2 EC" + "\n" + "\n" +
                "Active minutes:" + "\n" +
                "Over 30 min: 1 EC" + "\n" + "\n" +
                "Total EC earned today: 7 EC")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

        ll_coin_score = (LinearLayout) findViewById(R.id.ll_coin_score);
        ll_coin_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });

        tvProgress = findViewById(R.id.tvProgress);

        personalGoal = (ProgressBar) findViewById(R.id.personalGoalPB);

        //personalGoal.setScrollBarSize(100);
        //personalGoal.setMinimumHeight(100);
        /*
        progressStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100){
                    progressStatus += 1;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            personalGoal.setProgress(progressStatus);
                            tvProgress.setText(progressStatus+"");
                        }
                    });
                }
            }
        }).start();
        */

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPersonalGoal);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.personal_goal_array, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "Selected item " + adapterView.getItemAtPosition(i) + ", position " + i, Toast.LENGTH_LONG).show();
                switch (i){
                    case 0:
                        //save to buy a solar panel
                        //if 100 EC is earned: receive a coupon that give 20 % discount
                        tvProgress.setVisibility(View.VISIBLE);
                        personalGoal.setVisibility(View.VISIBLE);
                        personalGoal.setMax(100);
                        setProgressBar("solarPanel");
                        break;
                    case 1:
                        //save to buy an electric vehicle
                        //if 200 EC is earned: receive a coupon that give 20 % discount
                        tvProgress.setVisibility(View.VISIBLE);
                        personalGoal.setVisibility(View.VISIBLE);
                        personalGoal.setMax(200);
                        setProgressBar("electricVehicle");
                        break;
                    case 2:
                        //keep average carbon footprint below 2 kgco2
                        //each day the avg is below 2, get one step closer to earn EC
                        tvProgress.setVisibility(View.VISIBLE);
                        personalGoal.setVisibility(View.VISIBLE);
                        personalGoal.setMax(100);
                        setProgressBar("avgBelow2");
                        break;
                    case 3:
                        //keep average carbon footprint below 4 kg co2
                        //each day the avg is below 4, get one step closer to earn EC
                        tvProgress.setVisibility(View.VISIBLE);
                        personalGoal.setVisibility(View.VISIBLE);
                        personalGoal.setMax(100);
                        setProgressBar("avgBelow4");
                        break;
                    case 4:
                        //be active for at least 30 min every day
                        //each day being active for at least 30 min, get one step closer to earn EC
                        tvProgress.setVisibility(View.VISIBLE);
                        personalGoal.setVisibility(View.VISIBLE);
                        personalGoal.setMax(100);
                        setProgressBar("activeMinutes");
                        break;
                    case 5:
                        //create your own goal
                        //display dialogbox with title, how to measure
                        //change text in spinner and display goal instead of progress bar

                        LayoutInflater layoutInflater = LayoutInflater.from(ProfileActivity.this);
                        final View inflator = layoutInflater.inflate(R.layout.dialog_personal_goal, null);
                        final AlertDialog.Builder chooseGoalBuilder = new AlertDialog.Builder(ProfileActivity.this);
                        chooseGoalBuilder.setTitle("Choose your own goal");
                        chooseGoalBuilder.setView(inflator);

                        final EditText input = (EditText) inflator.findViewById(R.id.et_personalGoal);
                        /*new EditText(ProfileActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        chooseGoalBuilder.setView(input);
                        personalGoalString = input.getText().toString();
                        */

                        //chooseGoalBuilder.setView(R.layout.dialog_personal_goal);
                        chooseGoalBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                personalGoalString = input.getText().toString();
                                tvGoalExplanation.setText(personalGoalString);
                                tvProgress.setVisibility(View.GONE);
                                personalGoal.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Code to retrieve data from sql db
    /*
    private void loadUserScores() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, //maybe change to GET?
                Constants.URL_RETRIEVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userObject = new JSONObject(response);
                            int id = userObject.getInt("id");

                            String email = userObject.getString("email");
                            name.setText(email);

                            int num_coins = userObject.getInt("num_coins");

                            double avg_cf = userObject.getDouble("avg_cf");
                            tvAvgCf.setText("Avg. carbon footprint: " + String.valueOf(avg_cf) + " " + getResources().getString(R.string.carbon_footprint_unit));

                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println(error.getMessage());

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sharedPreferences.getString("pref_key_personal_email",""));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }*/

    protected void setUpNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.title_nav_header)).setText(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_personal_name", ""));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.subtitle_nav_header)).setText(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_personal_email", ""));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewNumEarthCoins)).setText(String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_key_personal_score", 0)));

        ImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.imageView);

        Uri imageUri = null;
        try {
            imageUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_key_profile_picture", "android.resource://com.example.desent.desent/drawable/earth"));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
            profilePicture.setImageBitmap(Utility.getCroppedBitmap(bitmap));
            bitmap.recycle();
            bitmap = null;
            System.gc();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_user_profile) {
            //startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(ProfileActivity.this, FriendsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_call_to_arms) {
            startActivity(new Intent(ProfileActivity.this, CallToArmsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_leaderboard) {
            startActivity(new Intent(ProfileActivity.this, LeaderboardActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_information) {
            startActivity(new Intent(ProfileActivity.this, InformationActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(ProfileActivity.this, AboutUsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_log_out){
            SessionManagement session = new SessionManagement(getApplicationContext());
            session.logoutUser();
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    public int getCoinScore() {
        int dailyCoinScore = 0;
        /*Indicator indicator = new Indicator() {
            @Override
            public void calculateValues() {

            }
        };*/
        //float dailyWalkingDistance = indicator.getWalkingDistance();
        float dailyWalkingDistance = 12;
        if (dailyWalkingDistance >= 3){
            dailyCoinScore += 1;
        }
        if (dailyWalkingDistance >= 8){
            dailyCoinScore += 2;
        }

        float dailyCycleDistance = 12;
        if (dailyCycleDistance >= 3){
            dailyCoinScore += 1;
        }
        if (dailyCycleDistance >= 8){
            dailyCoinScore += 2;
        }

        float activeMinutes = 33;
        if (activeMinutes >= 30){
            dailyCoinScore += 1;
        }
        return dailyCoinScore;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //Toast.makeText(getApplicationContext(), "Selected item " + adapterView.getItemAtPosition(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setProgressBar(String progressBar) {
        switch (progressBar){
            case "solarPanel":
                tvGoalExplanation.setText("Earn 100 Earth Coins to receive a coupon with a discount on solar panels.");
                progressStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("pref_key_personal_score",0); //retrieve totNumCoins
                tvProgress.setText(progressStatus+"/100");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressCount < progressStatus){
                            progressCount += 1;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    personalGoal.setProgress(progressCount, true);
                                }
                            });
                        }
                    }
                }).start();
                break;
            case "electricVehicle":
                tvGoalExplanation.setText("Earn 200 Earth Coins to receive a coupon with a discount on electric car.");
                progressStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("pref_key_personal_score",0); //retrieve totNumCoins
                tvProgress.setText(progressStatus+"/200");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressCount < progressStatus){
                            progressCount += 1;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @SuppressLint("NewApi")
                                @Override
                                public void run() {
                                    personalGoal.setProgress(progressCount, true);
                                }
                            });
                        }
                    }
                }).start();
                break;
            case "avgBelow2":
                tvGoalExplanation.setText("Each day your carbon footprint is below 2 kgCO2, get one step closer to earn 50 Earth Coins.");
                progressStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("pref_key_personal_score",0); //retrieve totNumCoins
                tvProgress.setText(progressStatus+"/100");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressCount < progressStatus){
                            progressCount += 1;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    personalGoal.setProgress(progressCount);

                                }
                            });
                        }
                    }
                }).start();
                break;
            case "avgBelow4":
                tvGoalExplanation.setText("Each day your carbon footprint is below 4 kgCO2, get one step closer to earn 25 Earth Coins.");
                progressStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("pref_key_personal_score",0); //retrieve totNumCoins
                tvProgress.setText(progressStatus+"/100");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressCount < progressStatus){
                            progressCount += 1;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    personalGoal.setProgress(progressCount);
                                }
                            });
                        }
                    }
                }).start();
                break;
            case "activeMinutes":
                tvGoalExplanation.setText("Each day you are active more than 30 minutes, get one step closer to earn 20 Earth Coins.");
                progressStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("pref_key_personal_score",0); //retrieve totNumCoins
                tvProgress.setText(progressStatus+"/100");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressCount < progressStatus){
                            progressCount += 1;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    personalGoal.setProgress(progressCount);
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }
}
