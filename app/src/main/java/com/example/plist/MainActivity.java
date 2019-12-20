package com.example.plist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plist.barcode.BarcodeCaptureFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int RC_BARCODE_CAPTURE_PRIMERO = 9000;
    public static final int RC_BARCODE_CAPTURE_SEGUNDO = 9001;

    TextView txtOne,txtDos;
    ImageButton imbtnOne,imbtnTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imbtnOne = (ImageButton) findViewById(R.id.imbtnOne);
        imbtnTwo = (ImageButton) findViewById(R.id.imbtnTwo);
        txtOne = (TextView) findViewById(R.id.txtOne);
        txtDos = (TextView) findViewById(R.id.txtDos);


        imbtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readBarCode(1);
            }
        });
        imbtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readBarCode(2);
            }
        });
    }




    public void readBarCode(int cual){

        Intent intent = new Intent(this, BarcodeCaptureFragment.class);
        switch (cual){
            case 1:
                startActivityForResult(intent, RC_BARCODE_CAPTURE_PRIMERO);
                break;
            case 2:
                startActivityForResult(intent, RC_BARCODE_CAPTURE_SEGUNDO);
                break;
            default:
                    startActivityForResult(intent, RC_BARCODE_CAPTURE_PRIMERO);
                break;
        }



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == CommonStatusCodes.SUCCESS) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra(BarcodeCaptureFragment.BarcodeObject);
                int type = barcode.valueFormat;
                if (type == Barcode.TEXT) {

                    switch (requestCode){
                        case RC_BARCODE_CAPTURE_PRIMERO:
                            txtOne.setText(barcode.displayValue);
                            break;

                        case RC_BARCODE_CAPTURE_SEGUNDO:
                            txtDos.setText(barcode.displayValue);
                            break;
                    }
                }
            }
        }  else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
