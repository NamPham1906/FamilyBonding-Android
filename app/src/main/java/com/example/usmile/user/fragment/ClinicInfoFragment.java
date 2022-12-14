package com.example.usmile.user.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.usmile.R;
import com.example.usmile.account.AccountFactory;
import com.example.usmile.account.models.Clinic;
import com.example.usmile.account.models.Doctor;
import com.example.usmile.account.models.User;
import com.example.usmile.doctor.DoctorMainActivity;
import com.example.usmile.user.fragment.SettingChangePasswordFragment;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ClinicInfoFragment extends Fragment implements View.OnClickListener {
    TextView clinicNameTextView;
    TextView addressTextView;
    TextView phoneNumberTextView;
    ImageView clinicImage;
    Button contactBtn;
    String encodedImage = "";

    User user;
    Clinic clinic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_clinic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        getBundle();

        bindingView(view);

        loadClinicDetails();

        setListeners();
    }

    private void setListeners() {
        clinicImage.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
    }

    private void getBundle() {
        Bundle bundle = getArguments();
       if (bundle != null)
           user = (User) bundle.getSerializable(AccountFactory.USERSTRING);

        if (bundle != null)
            clinic = (Clinic) bundle.getSerializable(AccountFactory.CLINICSTRING);

    }

    private void bindingView(@NonNull View view) {
        clinicNameTextView = (TextView) view.findViewById(R.id.clinicNameTextView);
        addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumberTextView);
         clinicImage = (ImageView) view.findViewById(R.id.clinicPicture);
         contactBtn = (Button) view.findViewById(R.id.callBtn);

    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    private void loadClinicDetails() {
        Bitmap bitmap = decodeImage(clinic.getAvatar());
        clinicImage.setImageBitmap(bitmap);
        clinicNameTextView.setText(clinic.getFullName());
        addressTextView.setText(clinic.getAddress());
        phoneNumberTextView.setText(clinic.getPhone());
    }



    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 600;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void openNewFragment(Fragment nextFragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), nextFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.clinicPicture:
                selectImage();
                break;
            case R.id.callBtn:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + clinic.getPhone()));
                getActivity().startActivity(intent);
                break;
        }
    }

    private void selectImage() {
        // open gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        // handle everything after picking
        pickImage.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {

                            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            clinicImage.setImageBitmap(bitmap);
                            clinic.setAvatar(encodeImage(bitmap));
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            database.collection(Constants.KEY_COLLECTION_CLINIC).add(clinic);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}