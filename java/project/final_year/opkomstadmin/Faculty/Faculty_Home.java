package project.final_year.opkomstadmin.Faculty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import project.final_year.opkomstadmin.MainActivity;
import project.final_year.opkomstadmin.R;
import project.final_year.opkomstadmin.model.DaoFaculty;

public class Faculty_Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    TextView profileName, profileId, profileDept;
    EditText nameP, emailP, uniqueIdP, contact_noP, typeP;
    public static String uId,  profileImageUrl, isHOD;
    public static int flag;
    String name, email, department, contact, hod, password, type;
    Spinner dept;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Dialog facultyProfile;
    ImageView edit;
    DaoFaculty daoFaculty, daoFaculty1;
    RadioButton yHOD, nHOD;
    AlertDialog.Builder builder;
    public static final String PREFS_NAME = "LoginPrefsFaculty";
    CircleImageView profileImage, profileImage1;
    int PICK_IMAGE_REQUEST = 234, imageFlag=0, dialogFlag=0;
    private Uri filePath;
    byte[] bytes = null;
    ProgressDialog progressDialog1;
    NavigationView navigationView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty__home);

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Fetching Data...");
        progressDialog1.setCancelable(false);
        progressDialog1.show();

        facultyProfile = new Dialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        profileImage = (CircleImageView) header.findViewById(R.id.profileImage);
        profileName = (TextView) header.findViewById(R.id.profileName);
        profileId = (TextView) header.findViewById(R.id.profileuniqueId);
        profileDept = (TextView) header.findViewById(R.id.profileDept);
        edit = (ImageView) header.findViewById(R.id.edit);

         if(!profileImageUrl.equals("")){
            Glide.with(this).load(profileImageUrl).into(profileImage);
            progressDialog1.dismiss();
        }
        else {
            progressDialog1.dismiss();
        }

        Log.e("UID", uId);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chats, R.id.nav_notices, R.id.nav_timetable)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(isHOD.equals("NO"))
            hideItem();

        setFacultyDetails(uId);

        builder = new AlertDialog.Builder(this);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog1.show();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                facultyProfile.setContentView(R.layout.facultyprofile);

                dialogFlag = 1;

                TextView close = (TextView) facultyProfile.findViewById(R.id.close);
                profileImage1 = (CircleImageView) facultyProfile.findViewById(R.id.profileImage1);
                nameP = (EditText) facultyProfile.findViewById(R.id.nameP);
                emailP = (EditText) facultyProfile.findViewById(R.id.emailP);
                dept = (Spinner) facultyProfile.findViewById(R.id.deptP);
                uniqueIdP = (EditText) facultyProfile.findViewById(R.id.uniqueIdP);
                contact_noP = (EditText) facultyProfile.findViewById(R.id.contact_noP);
                typeP = (EditText) facultyProfile.findViewById(R.id.typeP);
                yHOD = (RadioButton) facultyProfile.findViewById(R.id.yHOD);
                nHOD = (RadioButton) facultyProfile.findViewById(R.id.nHOD);
                Button save = (Button) facultyProfile.findViewById(R.id.save);

                firebaseDatabase.child("Faculty").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!profileImageUrl.equals("")){
                            Glide.with(Faculty_Home.this).load(profileImageUrl).into(profileImage1);
                        }
                        DaoFaculty daoFaculty2 = dataSnapshot.getValue(DaoFaculty.class);
                        nameP.setText(daoFaculty2.getName());
                        emailP.setText(daoFaculty2.getEmail());
                        uniqueIdP.setText(daoFaculty2.getUniqueId());
                        contact_noP.setText(daoFaculty2.getContact());
                        typeP.setText(daoFaculty2.getType());
                        password = daoFaculty2.getPassword();
                        hod = daoFaculty2.getHod();
                        department = daoFaculty2.getDepartment();

                        String deptArray [] = {department};
                        ArrayAdapter<CharSequence> langAdapter3 = new ArrayAdapter<CharSequence>(getBaseContext(), R.layout.spinner_text, deptArray);
                        langAdapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                        dept.setAdapter(langAdapter3);

                        if(hod.equalsIgnoreCase("Yes")){
                            yHOD.setChecked(true);
                        }
                        else if(hod.equalsIgnoreCase("No")){
                            nHOD.setChecked(true);
                        }
                        progressDialog1.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        facultyProfile.dismiss();
                    }
                });

                profileImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(imageFlag==1){
                            uploadFile(bytes);
                        }
                        name = nameP.getText().toString();
                        email = emailP.getText().toString();
                        contact = contact_noP.getText().toString();
                        type = typeP.getText().toString();

                        Log.e("IMAGE URL", profileImageUrl);
                        daoFaculty1 = new DaoFaculty(contact, department, profileImageUrl, email, hod, name, password, type, uId);
                        final  DatabaseReference databaseReference = firebaseDatabase.child("Faculty").child(uId);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                databaseReference.setValue(daoFaculty1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setFacultyDetails(uId);
                                        facultyProfile.dismiss();
                                        Log.e("IMAGE FLAG", String.valueOf(imageFlag));
                                        if(imageFlag==0)
                                            showAlertDialog();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                facultyProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                facultyProfile.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage1.setImageBitmap(bitmap);
                imageFlag = 1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                bytes = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(byte[] bytes) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference riversRef = storageReference.child("profileImages/"+uId+".jpg");
            riversRef.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "Profile Updated ", Toast.LENGTH_LONG).show();
                            getDownloadUrl(riversRef);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    });
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showAlertDialog();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
        }
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileImageUrl = uri.toString();
                Glide.with(Faculty_Home.this).load(profileImageUrl).into(profileImage);
                imageFlag=0;
                SharedPreferences saved_data = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = saved_data.edit();
                editor.putString("DPURL", profileImageUrl);
                editor.commit();
                firebaseDatabase.child("Faculty").child(uId).child("dpurl").setValue(profileImageUrl);
            }
        });
    }

    private void setFacultyDetails(final String uId) {
        firebaseDatabase.child("Faculty").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                daoFaculty = dataSnapshot.getValue(DaoFaculty.class);

                profileName.setText(daoFaculty.getName());
                profileId.setText(uId);
                profileDept.setText(daoFaculty.getDepartment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog(){
        builder.setTitle("Update Successful!").setMessage("You need to Sign In Again.").setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences saved_data = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = saved_data.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(Faculty_Home.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(flag==1){
            this.finish();
        }
        if(flag==2){

        }
        else{
            imageFlag=0;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.faculty__home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                logOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        SharedPreferences saved_data = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = saved_data.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void hideItem()
    {
        navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView1.getMenu();
        nav_Menu.findItem(R.id.nav_timetable).setVisible(false);
    }
}
