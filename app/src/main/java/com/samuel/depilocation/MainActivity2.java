package com.samuel.depilocation;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import mumayank.com.airlocationlibrary.AirLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MainActivity2 extends AppCompatActivity implements AirLocation.Callback {

    EditText addText;
    AirLocation airLocation;
    Button add,seeResult;
    TextView resultText,gasText;
    LottieAnimationView lottieAnimationView;
    List<Location> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        addText=findViewById(R.id.addText);
        add=findViewById(R.id.add);
        seeResult=findViewById(R.id.get);
        gasText=findViewById(R.id.gas);
        lottieAnimationView=findViewById(R.id.lottie);


        resultText=findViewById(R.id.resultText);
        resultText.setVisibility(View.INVISIBLE);
        locations = new ArrayList<>(); // Initialize the locations list
        airLocation = new AirLocation(this,this,true,0,"");
        airLocation.start();
        Toast.makeText(this, "Please enter your first location", Toast.LENGTH_LONG).show();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void address(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        seeResult.setEnabled(false);
        add.setText("New");

        StringBuilder result = new StringBuilder();
        float totalDistance = 0;
        boolean best=false;
        for (int i = 0; i < locations.size() - 1; i++) {
            Location loc1 = locations.get(i);
            Location loc2 = locations.get(i + 1);

            float distanceInMeters = loc1.distanceTo(loc2);
            float distanceInKm = distanceInMeters / 1000;
            totalDistance += distanceInKm;
            result.append("Distance from point ").append(i + 1).append(" to ").append(i + 2).append(" is ").append(distanceInKm).append(" km\n");
        }
        if (locations.size()>2){
            Location loc1 = locations.get(0);
            Location loc2 = locations.get(1);
            Location loc3 = locations.get(2);
            float distanceInMeters2 = loc1.distanceTo(loc3);
            float distanceInMeters = loc1.distanceTo(loc2);
            result.append("Distance from point ").append(1).append(" to ").append(3).append(" is ").append(distanceInMeters2/1000).append(" km\n");
            if(distanceInMeters>distanceInMeters2){
                best=true;
            }

        }
        resultText.append("\n The Distances are: \n");
        resultText.append(result.toString());
        if(best){
            resultText.append("\nThe best Route is from point 1 to point 3 then to point 2.");
        }
        resultText.append("\nTotal Distance is: " + totalDistance + " km");
        float totalTime = totalDistance / 40;
        int hours = (int) totalTime;
        int minutes = (int) ((totalTime - hours) * 60);
        resultText.append("\nThe total time is: " + hours + " hours " + minutes + " minutes");
        int gas = (int) Math.ceil(totalDistance / 12.5);
        gasText.setText("\nThe total gas needed is: " + gas + " liters");
        lottieAnimationView.setVisibility(View.VISIBLE);

    }

    public void add(View view) {
        Geocoder geocoder = new Geocoder(this);
        resultText.setVisibility(View.VISIBLE);
        if(add.getText().toString().equals("New"))
        {
            resultText.setText("");
            locations.clear();
            add.setText("Add");
            lottieAnimationView.setVisibility(View.INVISIBLE);
            gasText.setText("");
            airLocation = new AirLocation(this,this,true,0,"");
            airLocation.start();
        }
        try {
            if ((addText.getText().toString().isEmpty())) {
                Toast.makeText(this, "Please enter a location.", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Address> addresses = geocoder.getFromLocationName(addText.getText().toString() + " egypt", 1);
            if (!addresses.isEmpty()) {
                double lat = addresses.get(0).getLatitude();
                double lon = addresses.get(0).getLongitude();
                //resultText.setText("lat=" + lat + "-long=" + lon);
                Location loc1 = new Location("");
                loc1.setLatitude(lat);
                loc1.setLongitude(lon);
                locations.add(loc1);

                Toast.makeText(this, "Location added successfully.", Toast.LENGTH_SHORT).show();
                resultText.append(" → "+addText.getText()+"  ");
                addText.setText("");

                if(locations.size()==2){
                    seeResult.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "No location found for this address.", Toast.LENGTH_SHORT).show();
               // resultText.setText("No location found for the first address.");
            }
        } catch (IOException e) {
            if (e.getCause() instanceof TimeoutException) {
                Toast.makeText(this, "Geocoder service is currently unavailable. Please try again later.", Toast.LENGTH_SHORT).show();
            } else {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {

        Toast.makeText(this, locationFailedEnum.name(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(@NonNull ArrayList<Location> locationss) {
        double lat=locationss.get(0).getLatitude();
        double lon=locationss.get(0).getLongitude();
        resultText.append("Your Current Location: \n");
        locations.add(locationss.get(0));




        Geocoder geocoder=new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            resultText.append(addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName() + "\n");
            resultText.append("Your Destinations: \n");
            resultText.append(addresses.get(0).getLocality() + " ");

        } catch (IOException e) {

            System.out.println(e.getMessage());
        }



    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}