package com.example.mytravel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MainApp extends AppCompatActivity implements OnMapReadyCallback,
        PlaceSelectionListener, GoogleMap.OnMapClickListener {

    final static int REQUEST_LOC_PERMISSIONS = 5, ADD_POST = 6, SHOW_POST = 7, SETTINGS = 8;
    static ArrayList<User> subUsers = new ArrayList<>();

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    User user;
    Post currPost;
    LatLng postLocation;
    Marker userMarker;
    ClusterManager<Post> mClusterManager;
    AlertDialog dialog;
    String currId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        Places.initialize(getApplicationContext(), "@string/API_KEY");

        /* User loading dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(MainApp.this);
        builder.setCancelable(false);
        builder.setView(R.layout.user_dialog);
        dialog = builder.create();
        dialog.show();

        initComponents();

        // Getting user from intent extras
        Intent intent = getIntent();
        this.user = intent.getParcelableExtra("user");
        assert user != null;
        Log.d("user", user.getEmail());

        Intent service = new Intent(this, pushService.class);
        service.putExtra("currUser", user);
        startService(service);

        // Requesting location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOC_PERMISSIONS);
            finish();
        }
        else { initMap(); }
    }

    protected void initComponents()
    {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_favorites) {
                    viewAccount();
                    return true;
                } else {
                    viewSettings();
                }
                return false;
            }
        });
    }


    protected void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        /* Setting up search places fragment and listener to place choice */
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOC_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initMap();
            } else {
                Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMapClickListener(this);

        // Checking permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        assert provider != null;
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) { zoomCamera(new LatLng(location.getLatitude(), location.getLongitude())); };

        /* Setting up marker clustering */
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setAnimation(true);
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<Post>() {
                    @Override public boolean onClusterItemClick(Post clusterItem) {
                        Log.d("help", "in");
                        Intent intent = new Intent(MainApp.this, showPost.class);
                        intent.putExtra("user", user);
                        intent.putExtra("post", (Post)clusterItem);
                        startActivityForResult(intent, 5);
                        return false;
                    }
                });

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        //googleMap.setOnInfoWindowClickListener(mClusterManager);

        getImages();
    }

    public void getImages()
    {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.getChildrenCount() > 0)
                    {
                        for (DataSnapshot postSnapshot : childDataSnapshot.getChildren()) {
                            currId = postSnapshot.getKey();
                            // Constructing all parameters of post
                            String name = postSnapshot.child("name").getValue(String.class);
                            String description = postSnapshot.child("description").getValue(String.class);
                            User owner = postSnapshot.child("owner").getValue(User.class);
                            String latitude = postSnapshot.child("location").child("latitude").getValue().toString();
                            String longitude = postSnapshot.child("location").child("longitude").getValue().toString();

                            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            Uri imageLink = Uri.parse(postSnapshot.child("imageLink").getValue(String.class));

                            assert imageLink != null;
                            currPost = new Post(currId, location, description, name, owner, imageLink.toString());
                            addMarker(currPost);
                        }
                    }
                }
                mClusterManager.cluster();
                dialog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError) {
                Toast.makeText(MainApp.this, "Can't load posts", Toast.LENGTH_LONG).show();
                Log.e("The read failed: ", firebaseError.getMessage());
                dialog.dismiss();
            }
        });

        mClusterManager.cluster();
        dialog.dismiss();
    }

    public void onMapClick(final LatLng clickCoords)
    {
        if (clickCoords != null)
        {
            if (this.userMarker != null)
                this.userMarker.remove();

            this.postLocation = clickCoords;
            // Construct marker and add it to map
            MarkerOptions marker = new MarkerOptions()
                    .position(clickCoords)
                    .draggable(true)
                    .snippet("Drag the marker to choose location")
                    .flat(true)
                    .icon(Utils.BitmapFromVector(getApplicationContext(), R.drawable.person_pin));

            this.userMarker = mMap.addMarker(marker);
        }
    }

    public void addMarker(Post post)
    {
        mClusterManager.addItem(post);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_POST && resultCode == RESULT_OK)
        {
            dialog.show();
            getImages();
            this.userMarker.remove();
        } else if (requestCode == SETTINGS && resultCode == SettingsActivity.LOG_OUT)
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPlaceSelected(@NotNull Place place) {
        LatLng placeSelected = place.getLatLng();
        zoomCamera(placeSelected);
    }

    @Override
    public void onError(@NotNull Status status) {
        Toast.makeText(MainApp.this, "Error while fetching location", Toast.LENGTH_LONG).show();
    }

    public void addPost(View view)
    {
        if (userMarker != null)
        {
            Intent intent = new Intent(this, AddImage.class);
            intent.putExtra("user", user);
            intent.putExtra("location", this.postLocation);
            startActivityForResult(intent, ADD_POST);
        }
        else
        {
            Toast.makeText(this, "Please select a location!", Toast.LENGTH_LONG).show();
        }

    }

    public void viewSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS);
    }

    public void viewAccount()
    {
        Intent intent = new Intent(this, ShowUser.class);
        intent.putExtra("currUser", user);
        intent.putExtra("inputUser", user);
        startActivity(intent);
    }

    protected void zoomCamera(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    private class RenderClusterInfoWindow extends DefaultClusterRenderer<Post> {

        RenderClusterInfoWindow(Context context, GoogleMap map, ClusterManager<Post> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterRendered(Cluster<Post> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(Post item, MarkerOptions markerOptions) {
            markerOptions.title(item.getName());

            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}

