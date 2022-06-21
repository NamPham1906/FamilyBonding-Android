package com.example.usmile.doctor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usmile.R;
import com.example.usmile.doctor.fragment.DoctorDetailWaitingHealthRecordFragment;
import com.example.usmile.doctor.fragment.ReceivedHealthRecordListFragment;
import com.example.usmile.doctor.fragment.WaitingHealthRecordListFragment;
import com.example.usmile.user.fragment.DetailWaitingHealthRecordFragment;
import com.example.usmile.user.fragment.HealthRecordFragment;
import com.example.usmile.user.models.HealthRecord;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class DoctorWaitingHealthRecordAdapter extends RecyclerView.Adapter<DoctorWaitingHealthRecordAdapter.DoctorWaitingHealthRecordViewHolder>{


    private List<HealthRecord> healthRecords;
    private Context context;
    PreferenceManager preferenceManager;

    public DoctorWaitingHealthRecordAdapter(List<HealthRecord> list) {
        this.healthRecords = list;
    }

    public void setData(List<HealthRecord> list) {
        this.healthRecords = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public DoctorWaitingHealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_waiting_health_record, parent, false);

        context = parent.getContext();

        return new DoctorWaitingHealthRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorWaitingHealthRecordViewHolder holder, int position) {
        HealthRecord item = healthRecords.get(position);
        preferenceManager = new PreferenceManager(this.context);

        if (item == null)
            return;


        holder.senderMessage.setText(item.getDescription());
        holder.sendDate.setText(item.getSentDate());
        userInfor(item);
        try
        {
            String ava = preferenceManager.getString(Constants.KEY_GET_USER_AVATAR);
            Bitmap bitmap = decodeImage(ava);
            holder.senderAvatar.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Log.e("ERR", e.getMessage());
        }

        holder.senderName.setText(preferenceManager.getString(Constants.KEY_GET_USER_NAME));

        // from account id -> load user avatar and name from firestore
//        holder.senderAvatar.setImageResource(R.drawable.example_avatar);
//        holder.senderName.setText("fake name");


    }

    private void userInfor(HealthRecord hr) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_ACCOUNT)
                .document(hr.getAccountId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        preferenceManager.putString(Constants.KEY_GET_USER_NAME,
                                doc.getString(Constants.KEY_ACCOUNT_FULL_NAME));
                        preferenceManager.putString(Constants.KEY_GET_USER_AVATAR,
                                doc.getString(Constants.KEY_ACCOUNT_AVATAR));
//                        Log.d("AVA", preferenceManager.getString(Constants.KEY_GET_USER_AVATAR));
//                        Log.d("NAME", preferenceManager.getString(Constants.KEY_GET_USER_NAME));


                    } else {
                        Log.d("DEN-ID", "No such document");
                    }
                } else {
                    Log.d("DEN-ID", "get failed with ", task.getException());
                }
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        if (healthRecords == null)
            return 0;
        return healthRecords.size();
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }




    public class DoctorWaitingHealthRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView senderAvatar;
        TextView senderName;
        TextView sendDate;
        TextView senderMessage;

        TextView checkHealthRecordButton;
        TextView skipButton;

        PreferenceManager preferenceManager;
        Context context;


        AlertDialog cancelDialog;
        AlertDialog.Builder dialogBuilder;


        public DoctorWaitingHealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            preferenceManager = new PreferenceManager(context);

            senderAvatar = (ImageView) itemView.findViewById(R.id.senderAvatar);
            sendDate = (TextView) itemView.findViewById(R.id.sendDate);
            senderName = (TextView) itemView.findViewById(R.id.senderName);
            senderMessage = (TextView) itemView.findViewById(R.id.senderMessage);

            checkHealthRecordButton = (TextView) itemView.findViewById(R.id.checkHealthRecordButton);
            skipButton = (TextView) itemView.findViewById(R.id.skipButton);

            skipButton.setOnClickListener(this);
            checkHealthRecordButton.setOnClickListener(this);
        }

        private void openNewFragment(View view, Fragment nextFragment) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentHolder, nextFragment).addToBackStack(null).commit();
        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            int position = getLayoutPosition();
            HealthRecord item = healthRecords.get(position);

            switch (id) {
                case R.id.checkHealthRecordButton:
                    preferenceManager.putString(Constants.KEY_HEALTH_RECORD_ID, item.getId());
                    preferenceManager.putString(Constants.KEY_GET_USER_ID, item.getAccountId());
                    preferenceManager.putListString(Constants.KEY_HEALTH_RECORD_DELETED, item.getDeleted());
                    Fragment fragment = new DoctorDetailWaitingHealthRecordFragment();
                    openNewFragment(view, fragment);


                    break;
                case R.id.skipButton:
                    preferenceManager.putString(Constants.KEY_HEALTH_RECORD_ID, item.getId());                    preferenceManager.putListString(Constants.KEY_HEALTH_RECORD_DELETED, item.getDeleted());
                    preferenceManager.putListString(Constants.KEY_HEALTH_RECORD_DELETED, item.getDeleted());

                    try{
                        createCancelDialog(view.getContext());
                    }
                    catch (Exception e)
                    {
                        Log.e("CANCEL DIALOG",e.getMessage());
                    }
                    break;
            }
        }

        private void cancelHealthRecord() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference
                    = db.collection(Constants.KEY_COLLECTION_HEALTH_RECORD)
                    .document(preferenceManager.getString(Constants.KEY_HEALTH_RECORD_ID));

            List<String> del = preferenceManager.getListString(Constants.KEY_HEALTH_RECORD_DELETED);
            del.add(preferenceManager.getString(Constants.KEY_ACCOUNT_ID));
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_HEALTH_RECORD_DELETED, del);

            documentReference.update(updates)
                    .addOnSuccessListener(unused -> {
                        showToast("Updated successfully");

                    })
                    .addOnFailureListener(e -> {
                        Log.e("update health record", e.getMessage());
                    });
        }

        public void createCancelDialog(Context context) {
            dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final View cancelPopup = inflater.inflate( R.layout.popup_doctor_cancel_health_record, null );

            Button quitBtn = (Button) cancelPopup.findViewById(R.id.btnQuit);
            Button cancelBtn = (Button) cancelPopup.findViewById(R.id.btnCancel);
            dialogBuilder.setView(cancelPopup);
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
                    //reload fragment
                    Fragment fragment = new WaitingHealthRecordListFragment();
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

    }
}
