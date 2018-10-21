package sudo.nasaspaceapps.cryosphere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sudo.nasaspaceapps.cryosphere.rest.api.WeatherApi;
import sudo.nasaspaceapps.cryosphere.rest.model.Weather;

public class MainActivity extends AppCompatActivity {

    //----------------------DRAWER----------------------------------------
    private DrawerLayout mDrawerLayout;
    //-----------------------Permissions
    private final int MY_PERMISSIONS=124;
    //----------------------PLACES SDK
    PlaceAutocompleteFragment placeAutoComplete;
    private double latitude;
    private double longitude;
    private String city;


    //------------------------UI's----------------------------------
    TextView mainData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beginPermission();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //UI's----------------------------------
        mainData = findViewById(R.id.FrontData);


        //----------------------------NavigationView----------------------------------------
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_UV:

                                break;

                            case R.id.nav_Blogs:{
                                Intent i = new Intent(MainActivity.this,BlogActivity.class);
                                startActivity(i);
                            }

                                break;

                            case R.id.nav_skin:

                        }

                        return true;
                    }
                });

        //------------------------------------AUTOCOMPLETE PLACES----------------------------------------------------------------
        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocompletemain);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {


                Log.d("Maps", "Place selected: " + place.getName());
                city = String.valueOf(place.getName());
                LatLng latLng = place.getLatLng();
                //latLng.latitude();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                Intent i = new Intent(MainActivity.this, MapsFragment.class);
                i.putExtra("latitude", String.valueOf(latitude));
                i.putExtra("longitude", String.valueOf(longitude));
                i.putExtra("place", city);
                startActivity(i);

            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
                Toast.makeText(MainActivity.this, "Please Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        });

        getData();
    }


    //---------------------------MENU----------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
//            case R.id.call_an_ambulance:
//                phoneIntent.setData(Uri.parse("tel:102"));
//                startActivity(phoneIntent);
//                return true;
//            case R.id.call_the_firefighters:
//                phoneIntent.setData(Uri.parse("tel:101"));
//                startActivity(phoneIntent);
//                return true;
//            case R.id.call_the_police:
//                phoneIntent.setData(Uri.parse("tel:100"));
//                startActivity(phoneIntent);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------Side Menu----------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ///-----------------------------------Permissions---------------------------

    public void beginPermission() {
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)) {

            Log.i("1", "Permission is not granted");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) && (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE))) {
                Log.i("REQUEST", "Requesting permission....");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE },
                        MY_PERMISSIONS);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS);

            }
        } else {
            Toast.makeText(MainActivity.this, "THANKS", Toast.LENGTH_LONG);
           //Permission Granted




        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("1", "Permission is granted");

                } else {
                    Log.i("1", "Permission is again not granted");
                    Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Please ennable the permissions", Snackbar.LENGTH_SHORT);
                    mySnackbar.setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                        }
                    });
                    mySnackbar.show();

                }
                return;
            }
        }
    }

    //------------------------------------END PERMISSIONS---------------------------------------------------------------------------

    public void getData()
    {
        char unit = 'c';

        String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='" + unit + "'", "Delhi");
        WeatherApi.Factory.getInstance().getWeather(YQL, "json").enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                try {
                    Weather list = response.body();
                    //temperature.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp());
                    //mainData.setText(Html.fromHtml(response.body().getQuery().getResults().getChannel().getItem().getDescription()));
                    String formattedText = getString(R.string.html_string);
                    Spanned result = Html.fromHtml(formattedText);
                    mainData.setText(result);

                }
                catch (NullPointerException e){
                   // temperature.setText("0");
                }

            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
//                progress.dismiss();
            }
        });
    }


}
