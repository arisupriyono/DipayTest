package com.dipay.dipaygalery;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Boolean isFABOpen = false;
    FloatingActionButton fab, fabCam, fabPick;
    RecyclerView recyclerView;
    Uri imageUri;
    ImageView imageView;

    public static final int PICK_IMAGE = 1;

    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager LayoutManager;

    ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();


    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermissions();
        intializeBitmap();
        setupFab();
        listingImages();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgList.add(bitmap);
            listingImages();
        }
        if (requestCode == PICK_IMAGE) {
            final Uri uri = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgList.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            listingImages();
        }
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (result.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissions) {
                        if (result[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per;

                        }
                    }
                }
                return;
            }
        }
    }

    private void intializeBitmap(){
        imgList.addAll(Arrays.asList(
                getBitmap(getResources().getDrawable(R.drawable.img1)),
                getBitmap(getResources().getDrawable(R.drawable.img2)),
                getBitmap(getResources().getDrawable(R.drawable.img3)),
                getBitmap(getResources().getDrawable(R.drawable.img4)),
                getBitmap(getResources().getDrawable(R.drawable.img5)),
                getBitmap(getResources().getDrawable(R.drawable.img6)),
                getBitmap(getResources().getDrawable(R.drawable.img7))
        ));
    }

    public static Bitmap getBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void listingImages(){
        recyclerView = findViewById(R.id.recyclerView);
        LayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerAdapter = new RecyclerAdapter(imgList, this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        fabCam = findViewById(R.id.fabCam);
        fabPick = findViewById(R.id.fabPick);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
                closeFABMenu();
            }
        });

        fabPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
                closeFABMenu();
            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabCam.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabPick.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabCam.animate().translationY(0);
        fabPick.animate().translationY(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
