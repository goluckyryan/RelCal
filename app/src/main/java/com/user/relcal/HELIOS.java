package com.user.relcal;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class HELIOS extends AppCompatActivity {

    Nucleus ma, mb, m1, m2; //reaction a(b,1)2
    Double TLab, Ex, TlabMin, ExMax, QValue;
    int thetaCM;
    double[] Pa = new double[3]; // (E, x, y);
    double[] Pb = new double[3];
    double[] P1 = new double[3];
    double[] P2 = new double[3];

    int Ex_Multipler;

    AssetManager assetManager;

    EditText PaName;
    EditText PbName;
    EditText P1Name;
    TextView P2Name;

    TextView QValueView;
    EditText TLabInput;
    TextView TMinView;

    SeekBar Ex_seekBar;
    TextView Ex_reading_display;

    SeekBar thetaCM_seekBar;
    TextView theta_reading_display;

    private XYPlot plot;
    private XYPlot plot2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helios);
        setTitle("HELIOS");

        assetManager = getAssets();

        Ex_Multipler = 10;

        ma = new Nucleus(12, 6, assetManager);
        mb = new Nucleus(2, 1 ,assetManager);
        m1 = new Nucleus(1, 1, assetManager );
        m2 = new Nucleus( ma.A + mb.A - m1.A, ma.Z + mb.Z - m1.Z, assetManager );

        QValueView = findViewById(R.id.textView_QValue);
        QValue = ma.mass + mb.mass - m1.mass - m2.mass;
        QValueView.setText(String.format("Q : %5.2f [MeV]",QValue));

        TMinView = findViewById(R.id.textView_Tmin);
        TlabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
        TMinView.setText(String.format("%5.3f", TlabMin));

        PaName = findViewById(R.id.editText_Pa);
        PbName = findViewById(R.id.editText_Pb);
        P1Name = findViewById(R.id.editText_P1);
        P2Name = findViewById(R.id.textView_P2);

        TLabInput = findViewById(R.id.editText_TLab);

        Ex_seekBar = findViewById(R.id.seekBar_Ex);
        Ex_seekBar.setMax(20 * Ex_Multipler);
        Ex_reading_display = findViewById(R.id.textView_ExShow);
        thetaCM_seekBar = findViewById(R.id.seekBar_thetaCM);
        theta_reading_display = findViewById(R.id.textView_thetaCM_Show);

        plot = findViewById(R.id.plot);
        plot2 = findViewById(R.id.plot2);

        plot.clear();
        plot.getLegend().position(100, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot.getDomainTitle().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot.setDomainBoundaries(-2,2, BoundaryMode.FIXED);
        //plot.setUserDomainOrigin(0);

        plot2.clear();
        plot2.getLegend().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot2.getDomainTitle().position(-50, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot2.setDomainBoundaries(-2,2, BoundaryMode.FIXED);
        //plot2.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

        PaName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = PaName.getText().toString();
                    ma = new Nucleus(nameA, assetManager);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);

                    QValue = ma.mass + mb.mass - m1.mass - m2.mass;
                    QValueView.setText(String.format("Q : %5.2f [MeV]",QValue));
                    TlabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
                    TMinView.setText(String.format("%5.3f", TlabMin));

                }
            }
        });

        PbName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = PbName.getText().toString();
                    mb = new Nucleus(nameA, assetManager);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);

                    QValue = ma.mass + mb.mass - m1.mass - m2.mass;
                    QValueView.setText(String.format("Q : %5.2f [MeV]",QValue));
                    TlabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
                    TMinView.setText(String.format("%5.3f", TlabMin));

                }
            }
        });

        P1Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = P1Name.getText().toString();
                    m1 = new Nucleus(nameA, assetManager);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);

                    QValue = ma.mass + mb.mass - m1.mass - m2.mass;
                    QValueView.setText(String.format("Q : %5.2f [MeV]",QValue));
                    TlabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
                    TMinView.setText(String.format("%5.3f", TlabMin));

                }
            }
        });

        thetaCM_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                thetaCM = i;
                theta_reading_display.setText("" + i);

                Ex = Ex_seekBar.getProgress() / Ex_Multipler *1.0;

                //TransferReaction( thetaCM, Ex );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // calculate plot 2
            }
        });

        Ex_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //Log.i("read", "Ex_seekbar value = " + Integer.toString(i));

                Ex = i * 1.0 / Ex_Multipler * 1.0;
                Ex_reading_display.setText("" + Ex);

                thetaCM = thetaCM_seekBar.getProgress();
                //TransferReaction( thetaCM, Ex );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // calculate plots

            }
        });

        TLabInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    ma = new Nucleus(PaName.getText().toString(), assetManager);
                    mb = new Nucleus(PbName.getText().toString(), assetManager);
                    m1 = new Nucleus(P1Name.getText().toString(), assetManager);
                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);

                    QValue = ma.mass + mb.mass - m1.mass - m2.mass;
                    QValueView.setText(String.format("Q : %5.2f [MeV]",QValue));
                    TlabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
                    TMinView.setText(String.format("%5.3f", TlabMin));

                    TLab = Double.parseDouble(TLabInput.getText().toString());

                    Ex_seekBar.setProgress(0);

                    // calculate 4-vector;
                    Pa[0] = ma.mass + TLab * ma.A;
                    Pa[1] = ma.CalMomentum( TLab * ma.A);
                    Pa[2] = 0;

                    Pb[0] = mb.mass;
                    Pb[1] = 0;
                    Pb[2] = 0;

                    TransferReaction(0, 0);
                    double Ecm = mb.mass * TLab * ma.A/ ( ma.mass + mb.mass);
                    ExMax = Math.floor(Ecm +QValue);
                    Ex_seekBar.setMax( ExMax.intValue() *10);

                    return true;
                }
                return false;
            }
        });
    }


    private void TransferReaction(double thetaCM, double Ex){
        // reaction notation a(b,1)2
        double[] Pcm = new double[3];
        Pcm[0] = (Pa[0]+Pb[0])/2;
        Pcm[1] = (Pa[1]+Pb[1])/2;
        Pcm[2] = 0;

        if( m1.mass > m2.mass) {
            m1.SetEx(Ex);
        }else{
            m2.SetEx(Ex);
        }

        double betaInc = Pcm[1]/Pcm[0];
        double gamma = 1/sqrt(1-betaInc*betaInc);

        double[] Pac = new double[3];
        double[] Pbc = new double[3];
        double[] P1c = new double[3];
        double[] P2c = new double[3];

        Pac[0] = gamma * Pa[0] - gamma * betaInc * Pa[1];
        Pac[1] = -gamma * betaInc * Pa[0] + gamma * Pa[1];
        Pac[2] = 0;

        Pbc[0] = gamma * Pb[0] - gamma * betaInc * Pb[1];
        Pbc[1] = -gamma * betaInc * Pb[0] + gamma * Pb[1];
        Pbc[2] = 0;

        double Ecm = Pac[0] + Pbc[0];
        double p = sqrt((Ecm - m1.mass - m2.mass)*(Ecm + m1.mass - m2.mass)*(Ecm - m1.mass + m2.mass)*(Ecm + m1.mass + m2.mass));
        double k_cm = p/2/Ecm;

        P1c[0] = sqrt(m1.mass*m1.mass + k_cm*k_cm);
        P1c[1] = - k_cm * cos(thetaCM * Math.PI/180.);
        P1c[2] = - k_cm * sin(thetaCM * Math.PI/180.);

        P2c[0] = sqrt(m2.mass*m2.mass + k_cm*k_cm);
        P2c[1] = - P1c[1];
        P2c[2] = - P1c[2];

        P1[0] = gamma * P1c[0] + gamma * betaInc * P1c[1];
        P1[1] = gamma * betaInc * P1c[0] + gamma * P1c[1];
        P1[2] = P1c[2];

        P2[0] = gamma * P2c[0] + gamma * betaInc * P2c[1];
        P2[1] = gamma * betaInc * P2c[0] + gamma * P2c[1];
        P2[2] = P2c[2];

    }
}
