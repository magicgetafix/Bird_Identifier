package com.birdidentifier.birdidentifier_findthatbird.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.TestLooperManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.birdidentifier.birdidentifier_findthatbird.R;
import com.birdidentifier.birdidentifier_findthatbird.home.adapters.BirdAdapter;
import com.birdidentifier.birdidentifier_findthatbird.home.adapters.LinePagerIndicatorDecoration;
import com.birdidentifier.birdidentifier_findthatbird.model.Bird;
import com.birdidentifier.birdidentifier_findthatbird.model.viewmodel.MainModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BirdAdapter.BirdClickListener {

    private ImageView birdPhotoView;
    private Button identifyBirdButton;
    private ImageView deletePhoto;
    private ImageView deletePhotoBackground;
    private ImageView cameraShutterButton;
    private LocalModel birdIdentificationModel;
    private CustomImageLabelerOptions customImageLabelerOptions;
    private ImageLabeler imageLabeler;
    private TextView birdInstructions;
    private TextView possibleMatchTextView;
    private final static int REQUESTED_IMAGE_SIZE = 400;

    private RecyclerView recyclerView;

    private final static String IMAGE_FILE_NAME_KEY = "image_file_name_key";
    private final static String BIRD_LIST_KEY = "bird_list_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        birdPhotoView = findViewById(R.id.bird_image_view);
        identifyBirdButton = findViewById(R.id.button);
        deletePhoto = findViewById(R.id.delete_bird_image_button);
        deletePhotoBackground = findViewById(R.id.delete_bird_image_button_background);
        cameraShutterButton = findViewById(R.id.photo_shutter_button);
        birdInstructions = findViewById(R.id.bird_instructions);
        recyclerView = findViewById(R.id.bird_recycler_view);
        possibleMatchTextView = findViewById(R.id.bird_matches_text_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        birdIdentificationModel = new LocalModel.Builder()
                .setAssetFilePath("aiy_vision_classifier_birds_V1_1.tflite")
                .build();
        customImageLabelerOptions =
                new CustomImageLabelerOptions.Builder(birdIdentificationModel)
                        .setConfidenceThreshold(0.03f)
                        .setMaxResultCount(5)
                        .build();
        imageLabeler = ImageLabeling.getClient(customImageLabelerOptions);



        birdPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseImageClick();
            }
        });

        cameraShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseImageClick();
            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birdPhotoView.setImageDrawable(null);
                deletePhoto.setVisibility(View.INVISIBLE);
                deletePhotoBackground.setVisibility(View.INVISIBLE);
                birdInstructions.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainModel.birdList!=null && !MainModel.birdList.isEmpty()){
            processBirdImages(MainModel.birdList);
        }
        if (MainModel.imageFileName!=null && !MainModel.imageFileName.isEmpty()){
            Glide.with(this)
                    .load(MainModel.imageFileName)
                    .centerInside()
                    .into(birdPhotoView);
            deletePhoto.setVisibility(View.VISIBLE);
            deletePhotoBackground.setVisibility(View.VISIBLE);
            birdInstructions.setVisibility(View.INVISIBLE);
        }
    }

    private void processBirdImages(List<Bird> birdList){

        if (birdList!=null && !birdList.isEmpty()) {

            BirdAdapter birdAdapter = new BirdAdapter(this, birdList, this, this);
            recyclerView.setAdapter(birdAdapter);
            // pager indicator
            recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
            possibleMatchTextView.setVisibility(View.VISIBLE);
        }

    }


    public void onChooseImageClick() {

        MainModel.birdList = new ArrayList<>();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Choose a bird photo")
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(birdPhotoView.getWidth(), birdPhotoView.getHeight())
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(50)
                .start(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri imageUri = result.getUri();
                MainModel.imageFileName = imageUri.toString();
                Glide.with(this)
                        .load(imageUri)
                        .centerInside()
                        .into(birdPhotoView);

                deletePhoto.setVisibility(View.VISIBLE);
                processImage(imageUri);
            }
            else{
                showSnackbar(getString(R.string.failed_to_find_photo));
            }
        }
    }

    private void processImage(Uri imageUri){
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            showSnackbar(getString(R.string.failed_to_process_image));
        }

        if (image!=null && imageLabeler!=null){
            imageLabeler.process(image).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showSnackbar(getString(R.string.failed_to_process_image));
                }
            }).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                @Override
                public void onSuccess(List<ImageLabel> imageLabels) {
                    Iterator<ImageLabel> iterator = imageLabels.iterator();
                    String str = "";
                    while (iterator.hasNext()){
                        ImageLabel imageLabel = iterator.next();
                        if (!imageLabel.getText().contains("N/A")){
                            Bird bird = new Bird(imageLabel.getText(), imageLabel.getIndex(), imageLabel.getConfidence());
                            MainModel.birdList.add(bird);
                        }
                        else {
                            showSnackbar(getString(R.string.failed_to_process_image));
                        }
                    }
                    processBirdImages(MainModel.birdList);
                    birdInstructions.setVisibility(View.INVISIBLE);

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(IMAGE_FILE_NAME_KEY, MainModel.imageFileName);
        Bird[] birdArray = MainModel.birdList.toArray(new Bird[MainModel.birdList.size()]);
        outState.putParcelableArray(BIRD_LIST_KEY, birdArray);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            String imageFile = savedInstanceState.getString(IMAGE_FILE_NAME_KEY);
            if (imageFile!=null){
                Glide.with(this).load(imageFile).centerInside().into(birdPhotoView);
            }
            Parcelable[] parcelableArray = savedInstanceState.getParcelableArray(BIRD_LIST_KEY);
            MainModel.birdList = new ArrayList<>();
            if (parcelableArray!=null) {
                for (int i = 0; i < parcelableArray.length; i++) {
                    Bird parcelableBird = (Bird) parcelableArray[i];
                    if (parcelableBird != null) {
                        MainModel.birdList.add(parcelableBird);
                    }
                }
                processBirdImages(MainModel.birdList);
            }
        }
    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onClick(Bird bird) {

        if (bird!=null){
            Intent webActivityIntent = new Intent(this, WebActivity.class);
            webActivityIntent.putExtra(WebActivity.BIRD_NAME_KEY, bird.getEnglishName());
            webActivityIntent.putExtra(WebActivity.URL_KEY, bird.getWikipediaUrl());
            startActivity(webActivityIntent);
        }
    }




}
