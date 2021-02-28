package com.example.puzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


public class FragmentHomeScreen extends Fragment {

    private FirebaseAuth fAuth;
    private TextView homeLBLVerifyMessage;
    private TextView homeLBLWelcome;
    private Button homeBTNLogout;
    public static ImageView homeImageViewProfilePicture;
    private Button homeBTNOpenPuzzles;
    private String fName, lName, score;
    private StorageReference mStorage;
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public DatabaseReference db;
    private DatabaseReference reference;
    public Uri imguri;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        findViews(view);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        loadLayout();
        return view;
    }

    private void findViews(View view) {
        homeBTNLogout = view.findViewById(R.id.homeBTNLogout);
        homeLBLWelcome = view.findViewById(R.id.homeLBLWelcome);
        homeBTNOpenPuzzles = view.findViewById(R.id.homeBTNOpenPuzzles);
        homeImageViewProfilePicture = view.findViewById(R.id.homeImageViewProfilePicture);
        homeLBLVerifyMessage = view.findViewById(R.id.homeLBLVerifyMessage);
    }

    public void loadLayout() {

        mStorage = FirebaseStorage.getInstance().getReference(getString(R.string.imageStorageDataBase));
        homeImageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });
        homeBTNOpenPuzzles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPuzzles();
            }
        });
        fAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members)).child(fAuth.getUid());
        FirebaseUser user = fAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fName = snapshot.child(getString(R.string.firstNameKey)).getValue().toString();
                    lName = snapshot.child(getString(R.string.lastNamekey)).getValue().toString();
                    score = snapshot.child(getString(R.string.scoreKey)).getValue().toString();
                    homeLBLWelcome.setText("Welcome " + fName + " " + lName + " your current score is " + Integer.valueOf(score));
                    loadImageFromStorage();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        if (!user.isEmailVerified()) {
            homeLBLVerifyMessage.setVisibility(View.VISIBLE);
        }
        homeBTNLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        LoginManager.getInstance().logOut();
        startActivity(new Intent(getContext().getApplicationContext(), ActivityLogin.class));
        getActivity().finish();
    }


    public void FileUploader() {
        final StorageReference imagesRef = mStorage.child(fName + " " + lName + getString(R.string.dotJpg));

        imagesRef.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        StorageMetadata snapshotMetaData = taskSnapshot.getMetadata();
                        Task<Uri> downloadUrl = imagesRef.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {
                                String imageRef = uri.toString();
                                reference.child(fAuth.getUid()).child(getString(R.string.imageUrlKey)).setValue(imageRef);

                            }
                        });
                        Toast.makeText(getContext(), getString(R.string.imageUploaded), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getContext(), getString(R.string.somethingFailed), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void FileChooser() {
        Intent intent = new Intent();
        intent.setType(getString(R.string.imageGalleryPermission));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1 && data != null && data.getData() != null) {
            imguri = data.getData();
            homeImageViewProfilePicture.setImageURI(imguri);
            homeImageViewProfilePicture.setBackgroundColor(0);
            FileUploader();
        }
    }

    public void loadImageFromStorage() {
        try {
            StorageReference imageLoad = firebaseStorage.getReferenceFromUrl(getString(R.string.imageStorageUrl)).child(fName + " " + lName + getString(R.string.dotJpg));
            final File file = File.createTempFile(getString(R.string.image), getString(R.string.jpg));
            imageLoad.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    homeImageViewProfilePicture.setImageBitmap(bitmap);
                    homeImageViewProfilePicture.setBackgroundColor(0);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "No image ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openPuzzles() {
        startActivity(new Intent(getContext(), PuzzleScreenTabbed.class));
    }


}