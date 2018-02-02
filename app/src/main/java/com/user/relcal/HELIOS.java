package com.user.relcal;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class HELIOS extends AppCompatActivity {

    Nucleus ma, mb, m1, m2; //reaction a(b,1)2
    Double TLab, Ex, TlabMin, ExMax, QValue;
    Double Bfield;
    int thetaCM;
    double[] Pa = new double[3]; // (E, x, y);
    double[] Pb = new double[3];
    double[] P1 = new double[3];
    double[] P2 = new double[3];

    int Ex_Multipler;

    Double betaCM, k_cm;

    AssetManager assetManager;

    EditText PaName;
    EditText PbName;
    EditText P1Name;
    TextView P2Name;

    TextView QValueView;
    EditText TLabInput;
    TextView TMinView;

    EditText BfieldInput;
    TextView slopeShow;

    SeekBar Ex_seekBar;
    TextView Ex_reading_display;

    SeekBar thetaCM_seekBar;
    TextView theta_reading_display;

    private XYPlot plot1;
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

        BfieldInput = findViewById(R.id.editTex_Bfield);
        slopeShow = findViewById(R.id.textView_slope);

        Ex_seekBar = findViewById(R.id.seekBar_Ex);
        Ex_seekBar.setMax(20 * Ex_Multipler);
        Ex_reading_display = findViewById(R.id.textView_ExShow);
        thetaCM_seekBar = findViewById(R.id.seekBar_thetaCM);
        theta_reading_display = findViewById(R.id.textView_thetaCM_Show);

        plot1 = findViewById(R.id.plot);
        plot2 = findViewById(R.id.plot2);

        plot1.clear();
        plot1.getLegend().position(100, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot1.getDomainTitle().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot1.setDomainBoundaries(-2,2, BoundaryMode.FIXED);
        plot1.setDomainStep(StepMode.INCREMENT_BY_VAL, 0.5);
        //plot.setUserDomainOrigin(0);

        plot2.clear();
        plot2.getLegend().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot2.getDomainTitle().position(-50, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot2.setDomainBoundaries(-2,2, BoundaryMode.FIXED);
        plot2.setDomainStep(StepMode.INCREMENT_BY_VAL, 0.5);
        plot2.setRangeBoundaries(0,0.42, BoundaryMode.FIXED);
        plot2.setUserRangeOrigin(0);
        plot2.setRangeStep(StepMode.INCREMENT_BY_VAL, 0.1);

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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // calculate plot
                thetaCM = thetaCM_seekBar.getProgress();
                Ex = Ex_seekBar.getProgress() * 1.0 / Ex_Multipler;
                TransferReaction(thetaCM, Ex);
                //DrawPlot1();
                DrawPlot2();

            }
        });

        Ex_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //Log.i("read", "Ex_seekbar value = " + Integer.toString(i));
                Ex = i * 1.0 / Ex_Multipler * 1.0;
                Ex_reading_display.setText("" + Ex);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // calculate plots
                thetaCM = thetaCM_seekBar.getProgress();
                Ex = Ex_seekBar.getProgress() * 1.0 / Ex_Multipler;
                TransferReaction(thetaCM, Ex);
                DrawPlot1();
                DrawPlot2();

            }
        });

        TLabInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    SetUpReaction();
                    DrawPlot1();

                    return true;
                }
                return false;
            }
        });

        BfieldInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    SetUpReaction();
                    DrawPlot1();

                    return true;
                }
                return false;
            }
        });
    }

    private void SetUpReaction(){
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
        Bfield = Double.parseDouble(BfieldInput.getText().toString());

        Ex_seekBar.setProgress(0);
        Ex = 0.0;

        // calculate 4-vector;
        Pa[0] = ma.mass + TLab * ma.A;
        Pa[1] = ma.CalMomentum( TLab * ma.A);
        Pa[2] = 0;

        Pb[0] = mb.mass;
        Pb[1] = 0;
        Pb[2] = 0;

        TransferReaction(0, Ex);
        double Ecm = mb.mass * TLab * ma.A/ ( ma.mass + mb.mass);
        ExMax = Math.floor(Ecm +QValue);
        Ex_seekBar.setMax( ExMax.intValue() *10);
    }

    private void DrawPlot1(){

        final double c = 299.792458;
        double gamma = 1/sqrt(1-betaCM * betaCM);

        double mlight;
        int Zlight;
        String title;
        if( m1.mass > m2.mass ){
            mlight  = m2.mass;
            Zlight = m2.Z;
            title = "P2";
        }else{
            mlight = m1.mass;
            Zlight = m1.Z;
            title = "P1";
        }

        double alpha = Bfield * c * Zlight / 2 / Math.PI;
        double slope = alpha * betaCM;
        double intercept = sqrt(mlight*mlight + k_cm * k_cm) / gamma - mlight;

        slopeShow.setText(String.format("slope : %6.3f", slope));

        //calculate sereis
        List<Double> xVal = new ArrayList<>();
        List<Double> yVal_locus = new ArrayList<>();
        List<Double> yVal_bound = new ArrayList<>();

        for( double pos = -2.0 ; pos <= 2.0; pos+=4./100. ){
            xVal.add(pos);
            yVal_locus.add(intercept + slope * pos);
            yVal_bound.add(sqrt(mlight* mlight + pow(slope/betaCM * pos,2))-mlight);
        }

        XYSeries locus = new SimpleXYSeries(xVal, yVal_locus, title);
        XYSeries bound = new SimpleXYSeries(xVal, yVal_bound, "Max/Min");

        LineAndPointFormatter blueFormat = new LineAndPointFormatter(Color.BLUE, null, null, null);
        LineAndPointFormatter redFormat = new LineAndPointFormatter(Color.RED, null, null, null);

        plot1.clear();
        plot1.addSeries(locus, redFormat);
        plot1.addSeries(bound, blueFormat);

        if( Ex == 0.0) {
            double maxTLab = pow(gamma,2) *(intercept + mlight * pow(betaCM,2) + betaCM * sqrt( pow(intercept,2)+ 2 * intercept * mlight + pow(mlight * betaCM,2)));
            plot1.setRangeBoundaries(0, maxTLab * 1.2, BoundaryMode.FIXED);
        }
        plot1.redraw();

    }

    private void DrawPlot2(){
        final double c = 299.792458;

        double velocityZ_P1 = P1[1]/P1[0] ; // beta
        double velocityR_P1 = P1[2]/P1[0] ;
        double velocityZ_P2 = P2[1]/P2[0] ;
        double velocityR_P2 = P2[2]/P2[0] ;

        double rhoP1 = P1[2]/m1.Z/c/Bfield ; // in meter
        double rhoP2 = P2[2]/m2.Z/c/Bfield ;

        List<Double> xVal_P1 = new ArrayList<>();
        List<Double> yVal_P1 = new ArrayList<>();
        List<Double> xVal_P2 = new ArrayList<>();
        List<Double> yVal_P2 = new ArrayList<>();

        double sign_P1, sign_P2;
        if( P1[1] > 0){
            sign_P1 = +1;
        }else{
            sign_P1 = -1;
        }
        if( P2[1] > 0){
            sign_P2 = +1;
        }else{
            sign_P2 = -1;
        }

        for( double pos = 0.0 ; pos <= 2.0; pos+=  1./200. ) {

            if ( Math.abs(velocityR_P1 / velocityZ_P1 * pos / rhoP1) <= 2 * Math.PI) {
                xVal_P1.add(sign_P1 * pos);
                yVal_P1.add( sqrt( pow(rhoP1 * Math.cos(velocityR_P1 / velocityZ_P1 * pos / rhoP1) - rhoP1,2) + pow(rhoP1 * Math.sin(velocityR_P1 / velocityZ_P1 * pos / rhoP1),2)));
            }

            if ( Math.abs(velocityR_P2 / velocityZ_P2 * pos / rhoP2) <= 2 * Math.PI) {
                xVal_P2.add(sign_P2 * pos);
                yVal_P2.add(sqrt( pow(rhoP2 * Math.cos(velocityR_P2 / velocityZ_P2 * pos / rhoP2) - rhoP2,2) + pow(rhoP2 * Math.sin(velocityR_P2 / velocityZ_P2 * pos / rhoP2),2)));
            }
        }

            XYSeries rzP1 = new SimpleXYSeries(xVal_P1, yVal_P1, "P1");
            XYSeries rzP2 = new SimpleXYSeries(xVal_P2, yVal_P2, "P2");

            LineAndPointFormatter blueFormat = new LineAndPointFormatter(Color.BLUE, null, null, null);
            LineAndPointFormatter redFormat = new LineAndPointFormatter(Color.RED, null, null, null);

        plot2.clear();
        plot2.addSeries(rzP1, redFormat);
        plot2.addSeries(rzP2, blueFormat);
        plot2.redraw();

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

        betaCM = Pcm[1]/Pcm[0];
        double gamma = 1/sqrt(1- betaCM * betaCM);

        double[] Pac = new double[3];
        double[] Pbc = new double[3];
        double[] P1c = new double[3];
        double[] P2c = new double[3];

        Pac[0] = gamma * Pa[0] - gamma * betaCM * Pa[1];
        Pac[1] = -gamma * betaCM * Pa[0] + gamma * Pa[1];
        Pac[2] = 0;

        Pbc[0] = gamma * Pb[0] - gamma * betaCM * Pb[1];
        Pbc[1] = -gamma * betaCM * Pb[0] + gamma * Pb[1];
        Pbc[2] = 0;

        double Ecm = Pac[0] + Pbc[0];
        double p = sqrt((Ecm - m1.mass - m2.mass)*(Ecm + m1.mass - m2.mass)*(Ecm - m1.mass + m2.mass)*(Ecm + m1.mass + m2.mass));
        k_cm = p/2/Ecm;

        P1c[0] = sqrt(m1.mass*m1.mass + k_cm*k_cm);
        P1c[1] = - k_cm * cos(thetaCM * Math.PI/180.);
        P1c[2] = - k_cm * sin(thetaCM * Math.PI/180.);

        P2c[0] = sqrt(m2.mass*m2.mass + k_cm*k_cm);
        P2c[1] = - P1c[1];
        P2c[2] = - P1c[2];

        P1[0] = gamma * P1c[0] + gamma * betaCM * P1c[1];
        P1[1] = gamma * betaCM * P1c[0] + gamma * P1c[1];
        P1[2] = P1c[2];

        P2[0] = gamma * P2c[0] + gamma * betaCM * P2c[1];
        P2[1] = gamma * betaCM * P2c[0] + gamma * P2c[1];
        P2[2] = P2c[2];

    }
}
