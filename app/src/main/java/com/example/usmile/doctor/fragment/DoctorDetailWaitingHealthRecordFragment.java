package com.example.usmile.doctor.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usmile.R;
import com.example.usmile.user.UserMainActivity;
import com.example.usmile.user.fragment.HealthRecordFragment;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DoctorDetailWaitingHealthRecordFragment extends Fragment {
    UserMainActivity main;

    TextView senderGender;
    TextView sendRecordDate;
    TextView senderAge;
    TextView askForAdviceEditText;
    TextView senderName;

    Button acceptButton;
    Button cancelButton

    final int CAPTURE_FIRST_IMAGE = 1;
    final int CAPTURE_SECOND_IMAGE = 2;
    final int CAPTURE_THIRD_IMAGE = 3;
    final int CAPTURE_FOURTH_IMAGE = 4;

    final int LOAD_FIRST_IMAGE = 5;
    final int LOAD_SECOND_IMAGE = 6;
    final int LOAD_THIRD_IMAGE = 7;
    final int LOAD_FOURTH_IMAGE = 8;

    String encodeImage1 = "";
    String encodeImage2 = "";
    String encodeImage3 = "";
    String encodeImage4 = "";

    ImageView firstImageView;
    ImageView secondImageView;
    ImageView thirdImageView;
    ImageView fourthImageView;


    PreferenceManager preferenceManager;

    AlertDialog cancelDialog;
    AlertDialog.Builder dialogBuilder;

    Fragment fragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_detail_waiting_health_record, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(getContext());

        main = (UserMainActivity) getActivity();

        senderGender = (TextView) view.findViewById(R.id.senderGender);
        sendRecordDate = (TextView) view.findViewById(R.id.sendRecordDate);
        senderAge = (TextView) view.findViewById(R.id.senderAge);
        askForAdviceEditText = (TextView) view.findViewById(R.id.askForAdviceEditText);
        senderName = (TextView) view.findViewById(R.id.senderName);

        acceptButton = (Button) view.findViewById(R.id.acceptButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        firstImageView = (ImageView) view.findViewById(R.id.firstPicture);
        secondImageView = (ImageView) view.findViewById(R.id.secondPicture);
        thirdImageView = (ImageView) view.findViewById(R.id.thirdPicture);
        fourthImageView = (ImageView) view.findViewById(R.id.fourthPicture);

        loadWaitingHealthRecordDetails();

    }

    private void loadWaitingHealthRecordDetails()
    {
        String healthRecordId = preferenceManager.getString(Constants.KEY_HEALTH_RECORD_ID);
        Log.d("HR ID", healthRecordId );
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_HEALTH_RECORD)
                .whereEqualTo(Constants.KEY_HEALTH_RECORD_ID, healthRecordId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                        List<String> healthPictures = (ArrayList) doc.get("healthPictures");
                        String description = doc.getString("description");
                        String sendDate = doc.getString("sendDate");

                        encodeImage1 = healthPictures.get(0);
                        encodeImage2 = healthPictures.get(1);
                        encodeImage3 = healthPictures.get(2);
                        encodeImage4 = healthPictures.get(3);

                        firstImageView.setImageBitmap(decodeImage(encodeImage1));
                        secondImageView.setImageBitmap(decodeImage(encodeImage2));
                        thirdImageView.setImageBitmap(decodeImage(encodeImage3));
                        fourthImageView.setImageBitmap(decodeImage(encodeImage4));

                        timeDetailTextView.setText("Hồ sơ ngày " + sendDate);
                        askForAdviceEditText.setText(description);
                        editButton.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cancelHealthRecord() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_HEALTH_RECORD)
                .document(preferenceManager.getString(Constants.KEY_HEALTH_RECORD_ID))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DELETE HR", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error deleting document", e.getMessage());
                    }
                });
    }

    public void createCancelDialog(){
        dialogBuilder = new AlertDialog.Builder(main);
        final View quitPopup = getLayoutInflater().inflate(R.layout.popup_cancel_health_record, null);

        Button quitBtn = (Button) quitPopup.findViewById(R.id.btnQuit);
        Button cancelBtn = (Button) quitPopup.findViewById(R.id.btnCancel);
        dialogBuilder.setView(quitPopup);
        cancelDialog = dialogBuilder.create();
        cancelDialog.show();
        cancelDialog.setCanceledOnTouchOutside(false);
        cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dimiss dialog
                cancelHealthRecord();
                cancelDialog.dismiss();
                fragment = new HealthRecordFragment();
                openNewFragment(view, fragment);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dimiss dialog
                cancelDialog.dismiss();
            }
        });
    }

    private void openNewFragment(View view, Fragment nextFragment) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentHolder, nextFragment).addToBackStack(null).commit();
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bitmap;
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}