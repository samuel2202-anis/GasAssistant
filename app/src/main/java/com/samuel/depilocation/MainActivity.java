package com.samuel.depilocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity implements AirLocation.Callback {
    AirLocation airLocation;
    TextView locationText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText=findViewById(R.id.locationText);
    }

    public void where(View view) {
        airLocation = new AirLocation(this,this,true,0,"");
        airLocation.start();
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
    public void onSuccess(@NonNull ArrayList<Location> locations) {
        double lat=locations.get(0).getLatitude();
        double lon=locations.get(0).getLongitude();
        locationText.setText("latitude="+lat+"-longitude="+lon);

        Geocoder geocoder=new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            locationText.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            locationText.setText(e.getMessage());
            System.out.println(e.getMessage());
        }
//        Intent a=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+lat+","+lon));
//        Intent a=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=cairo+stadium+egypt"));
//        startActivity(a);



    }
}