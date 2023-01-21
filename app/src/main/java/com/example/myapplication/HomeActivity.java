package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static boolean flag = false;

    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final String FIRST_LAUNCH = "FirstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            flag = true;
        }

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            showAlertDialogue();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_LAUNCH, false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Clear the saved login information
            SharedPreferences.Editor editor = getSharedPreferences("loginPreference",Context.MODE_PRIVATE).edit();
            editor.putBoolean("is_logged_in",false);
            editor.clear();
            editor.apply();

            // Redirect to the login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialogue() {
        String[] steps = {"Step 1: Click on Edit Alert", "Step 2: Select trusted contacts (max 5)", "Step 3: Enter an emergency message and hit submit.", "In case of emergency either click One tap 'Send Alert' or click on 'Services' to view nearby emergency services to your location."};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Use the App").setItems(steps, null).setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // your code here
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    } else {
                        Toast.makeText(this, " Permission Denied", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                    }
                }
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void EditAlert(View v) {
        if (flag) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    public void SendAlert(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE);

        List<ContactModel> contacts = new DataBaseHelper(this).getAllContacts(sharedPreferences.getString("userEmail", ""));
        if(contacts.size()>0) sendSMSAlert();
        else Toast.makeText(this, "You haven't selected any emergency contacts", Toast.LENGTH_SHORT).show();

    }

    private void phoneCallAlert(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }


    public void sendSMSAlert() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE);

        List<ContactModel> contacts = new DataBaseHelper(this).getAllContacts(sharedPreferences.getString("userEmail", ""));

        ArrayList<String> contactNumbersForMessage = new ArrayList<>();

        for(ContactModel model: contacts){
            contactNumbersForMessage.add(model.getContactNumber());
        }

        Collections.reverse(contactNumbersForMessage);

        File messageFile = new File(getFilesDir(), "MessageFile.txt");
        String alertMessage;
        try {
            // Create a BufferedReader to read the file
            BufferedReader reader = new BufferedReader(new FileReader(messageFile));

            // Use a StringBuilder to concatenate the lines of the file
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            // Save the contents of the file to a string variable
            alertMessage = sb.toString();

            SmsManager smsManager = SmsManager.getDefault();
            int counter = 4;
            for (String contactNumber : contactNumbersForMessage) {
                if (counter-- != 0) {
                    smsManager.sendTextMessage(contactNumber, null, alertMessage, null, null);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Sending alert!!!", Toast.LENGTH_SHORT).show();
        phoneCallAlert(contactNumbersForMessage.get(0));
    }

    public void navigateServices(View view) {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Select an emergency type");
        builder.setMessage("Please choose one of the following options:");

        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.service_layout, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupService);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do something when user click OK
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(HomeActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                    String selectedOption = selectedRadioButton.getText().toString();
//                    if (myLocation != null) {
//                        double latitude = myLocation.getLatitude();
//                        double longitude = myLocation.getLongitude();
                    String query = "";

                    switch (selectedOption) {
                        case "Nearby Hospitals":
                            query = "hospital";
                            break;
                        case "Police Station":
                            query = "police station";
                            break;
                        case "Fire Department":
                            query = "fire department";
                            break;
                        case "Medical/Pharmacy":
                            query = "pharmacy";
                            break;
                    }
                    query += "&radius=5000";
                    Uri location = Uri.parse("geo:" + 0 + "," + 0 + "?q=" + query);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                    startActivity(mapIntent);
//                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do something when user click Cancel
                Toast.makeText(HomeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void test(View v) {
        Uri location = Uri.parse("geo:" + 0 + "," + 0 + "?q=" + "hospital");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }
}