package com.user.relcal;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class KnockoutActivity extends AppCompatActivity {

    //Reaction A(a,12)B, A = B + b

    Nucleus mA, ma, m1, m2, mB, mb;
    Double TLab, TLabMin, ExB;
    int thetaCM;
    double[] PA = new double[3]; // (E, x, y);
    double[] Pa = new double[3]; // (E, x, y);
    double[] Pb = new double[3];
    double[] P1 = new double[3];
    double[] P2 = new double[3];
    double[] PB = new double[3];

    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knockout);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Knockout Reaction");

    }

}
