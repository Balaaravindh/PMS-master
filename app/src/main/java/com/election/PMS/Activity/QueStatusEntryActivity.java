package com.election.PMS.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.election.PMS.Common.Constant;
import com.election.PMS.Common.NukeSSLCerts;
import com.election.PMS.Common.Session;
import com.election.PMS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class QueStatusEntryActivity extends AppCompatActivity {

    ActionBar actionBar;
    TextView tvCurrentTime, boothName;
    EditText edtQueCount;
    Button btn_submit, btn_back, btn_takePhoto, btn_retakePhoto;
    ImageView previewImage;
    LinearLayout capturedImagePreview;
    HashMap<String, String> user;
    Session session;
    String currentTime = "";
    String encodedString = "";
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_que_status_entry);
        actionBar = getSupportActionBar();
        actionBar.hide();

        inti();
        session = new Session(this);
        new NukeSSLCerts().nuke();
        user = session.getProfileManagerDetails();
        boothName.setText(user.get("boothName"));

        btn_back.setOnClickListener(v -> finish());

        currentTime = new SimpleDateFormat(Constant.FixedTimeFormat, Locale.getDefault()).format(new Date());
        tvCurrentTime.setText(currentTime);

        btn_submit.setOnClickListener(v -> {
            if(!edtQueCount.getText().toString().isEmpty() && encodedString != "") {
                //API Call
                submitQueStatusEntry();
            }else{
                if(edtQueCount.getText().toString().isEmpty()){
                    Toast.makeText(this, "Please Enter the Que Count", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "You must upload the Que Photo.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_takePhoto.setOnClickListener(v-> {
            if (checkPermission()) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, 2);
            }else{
                requestPermission();
            }
        });

        btn_retakePhoto.setOnClickListener(v -> {
            encodedString = "";
            btn_takePhoto.setVisibility(View.VISIBLE);
            capturedImagePreview.setVisibility(View.GONE);
        });
    }
    private void submitQueStatusEntry() {
        String URL = "SavePollQue";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        session = new Session(this);
        user = session.getProfileManagerDetails();
        String constituencyID = user.get("parliamentConstituencyID");
        if(constituencyID.equals("0")){
            constituencyID = user.get("assemblyConstituencyId");
        }
        JSONObject params = new JSONObject();
        try {
            params.put("presidingOfficerID", user.get("presidingOfficierId"));
            params.put("constituencyID", constituencyID);
            params.put("boothID", user.get("boothId"));
            params.put("queCount", edtQueCount.getText().toString());
            params.put("quePhoto", encodedString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, params, response -> {
            try {
                String result = response.getString("isSuccess");
                pDialog.dismiss();
                if (result.equals("false")) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    edtQueCount.setText("");
                    encodedString = "";
                    btn_takePhoto.setVisibility(View.VISIBLE);
                    capturedImagePreview.setVisibility(View.GONE);
                    Toast.makeText(this, "Successfully Updated your data.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {

            }
        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            btn_takePhoto.setVisibility(View.GONE);
            capturedImagePreview.setVisibility(View.VISIBLE);
            previewImage.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 85, baos);
            byte[] b = baos.toByteArray();
            encodedString = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
    public void inti() {
        btn_back = findViewById(R.id.btn_back);
        boothName = findViewById(R.id.boothName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        edtQueCount = findViewById(R.id.edtQueCount);
        btn_submit = findViewById(R.id.btn_submit);
        btn_takePhoto = findViewById(R.id.btn_takePhoto);
        capturedImagePreview = findViewById(R.id.capturedImagePreview);
        previewImage = findViewById(R.id.previewImage);
        btn_retakePhoto = findViewById(R.id.btn_retakePhoto);
    }
}