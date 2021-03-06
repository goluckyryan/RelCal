package com.user.relcal;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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


public class TransferActivity extends AppCompatActivity {

    Nucleus ma, mb, m1, m2; //reaction a(b,1)2
    Double TLab, TLabMin, Ex;
    int thetaCM;
    double[] Pa = new double[3]; // (E, x, y);
    double[] Pb = new double[3];
    double[] P1 = new double[3];
    double[] P2 = new double[3];

    AssetManager assetManager;

    EditText PaName;
    EditText PbName;
    EditText P1Name;
    TextView P2Name;

    TextView infoView;

    TextView Pavec;
    TextView Pbvec;
    TextView P1vec;
    TextView P2vec;

    TextView QValueView;

    EditText TLabInput;
    SeekBar thetaCM_seekBar;
    TextView theta_reading_display;
    EditText ExInput;

    private XYPlot plot;
    private XYPlot plot2;

    double betaInc = 0;
    double k_cm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Transfer Reaction");

        //Reaction notate as a(b,1),2

        assetManager = getAssets();
        ma = new Nucleus(12, 6, assetManager);
        mb = new Nucleus(2, 1 ,assetManager);
        m1 = new Nucleus(1, 1, assetManager );
        m2 = new Nucleus( ma.A + mb.A - m1.A, ma.Z + mb.Z - m1.Z, assetManager );

        QValueView = findViewById(R.id.textView_QValue);
        QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));

        PaName = findViewById(R.id.editText_Pa);
        PbName = findViewById(R.id.editText_Pb);
        P1Name = findViewById(R.id.editText_P1);
        P2Name = findViewById(R.id.textView_P2);

        infoView = findViewById(R.id.textView_info);
        TLabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;
        SetInfo(TLabMin, betaInc, k_cm);

        Pavec = findViewById(R.id.textView_4vPa);
        Pbvec = findViewById(R.id.textView_4vPb);
        P1vec = findViewById(R.id.textView_4vP1);
        P2vec = findViewById(R.id.textView_4vP2);

        TLabInput = findViewById(R.id.editText_TLab);
        thetaCM_seekBar = findViewById(R.id.seekBar);
        theta_reading_display = findViewById(R.id.textView_theta_reading);
        ExInput = findViewById(R.id.editText_Tmin);

        plot = findViewById(R.id.plot);
        plot2 = findViewById(R.id.plot2);

        plot.clear();
        plot.getLegend().position(100, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot.getDomainTitle().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);

        plot2.clear();
        plot2.getLegend().position(0, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        plot2.getDomainTitle().position(-50, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                100, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot2.setUserRangeOrigin(0);
        plot2.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

        PaName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = PaName.getText().toString();
                    ma = new Nucleus(nameA, assetManager);
                    Pavec.setText("" + ma.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));

                    SetInfo(TLabMin,0,0);
                }
            }
        });

        PaName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = PaName.getText().toString();
                    ma = new Nucleus(nameA, assetManager);
                    Pavec.setText("" + ma.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));
                    SetInfo(TLabMin,0,0);

                    return true;
                }
                return false;
            }
        });

        PbName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = PbName.getText().toString();
                    mb = new Nucleus(nameA, assetManager);
                    Pbvec.setText("" + mb.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));
                    SetInfo(TLabMin,0,0);
                }
            }
        });

        PbName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = PbName.getText().toString();
                    mb = new Nucleus(nameA, assetManager);
                    Pbvec.setText("" + mb.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));
                    SetInfo(TLabMin,0,0);
                    return true;
                }
                return false;
            }
        });

        P1Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if( !b){
                    String nameA = P1Name.getText().toString();
                    m1 = new Nucleus(nameA, assetManager);
                    P1vec.setText("" + m1.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));
                    SetInfo(TLabMin,0,0);
                }
            }
        });

        P1Name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = P1Name.getText().toString();
                    m1 = new Nucleus(nameA, assetManager);
                    P1vec.setText("" + m1.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    QValueView.setText(String.format("Q : %5.2f [MeV]",ma.mass + mb.mass - m1.mass - m2.mass));
                    SetInfo(TLabMin,0,0);
                    return true;
                }
                return false;
            }
        });


        ExInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //ma = new Nucleus(PaName.getText().toString(), assetManager);
                    //mb = new Nucleus(PbName.getText().toString(), assetManager);
                    //m1 = new Nucleus(P1Name.getText().toString(), assetManager);
                    //m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - m1.Z, assetManager );
                    //P2Name.setText(m2.Name);

                    Ex = Double.parseDouble(ExInput.getText().toString());

                    QValueView.setText(String.format("Q : %5.2f [MeV]", ma.mass + mb.mass - m1.mass - m2.mass - Ex));

                    if( m1.mass > m2.mass) {
                        m1.SetEx(Ex);
                    }else{
                        m2.SetEx(Ex);
                    }

                    TLabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;

                    infoView.setText(String.format("%7s :  %5.2f [A MeV]",
                            Html.fromHtml("min(T<sub><small>Lab<small></sub>)"),
                            TLabMin));

                }
                return false;
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


                    TLab = Double.parseDouble(TLabInput.getText().toString());
                    Ex = Double.parseDouble(ExInput.getText().toString());

                    QValueView.setText(String.format("Q : %5.2f [MeV]", ma.mass + mb.mass - m1.mass - m2.mass - Ex));

                    if( m1.mass > m2.mass) {
                        m1.SetEx(Ex);
                    }else{
                        m2.SetEx(Ex);
                    }

                    TLabMin = (Math.pow(m1.mass+m2.mass,2) - Math.pow(ma.mass+mb.mass,2))/2./ma.A/mb.mass;

                    if( TLabMin > TLab ) {
                        infoView.setText(String.format("%7s :  %5.2f [A MeV]",
                                Html.fromHtml("min(T<sub><small>Lab<small></sub>)"),
                                TLabMin));
                        return false;
                    }

                    // calculate 4-vector;
                    Pa[0] = ma.mass + TLab * ma.A;
                    Pa[1] = ma.CalMomentum( TLab * ma.A);
                    Pa[2] = 0;
                    Pavec.setText(displays4vec(Pa));

                    Pb[0] = mb.mass;
                    Pb[1] = 0;
                    Pb[2] = 0;
                    Pbvec.setText(displays4vec(Pb));

                    //Plot graphic of Elab vs theta

                    //Calculate data
                    List<Double> xvals1 = new ArrayList<>();
                    List<Double> yvals1 = new ArrayList<>();

                    List<Double> xvals2 = new ArrayList<>();
                    List<Double> yvals2 = new ArrayList<>();

                    List<Double> xvals3 = new ArrayList<>();
                    List<Double> yvals3 = new ArrayList<>();

                    List<Double> xvals4 = new ArrayList<>();
                    List<Double> yvals4 = new ArrayList<>();

                    double maxX = 0 , maxY = 0, minX = 0, maxY2 = 0, minY2 = 100;

                    double ra = 1.25 * Math.pow(ma.A, 1./3.);
                    double rb = 1.25 * Math.pow(mb.A, 1./3.);
                    double hbar = 197.327 ;

                    TransferReaction(0);
                    double beta = GetMomentum(Pa) / Pa[0];
                    double gamma = 1/sqrt(1-beta*beta);
                    SetInfo(TLabMin, betaInc,k_cm);

                    for( int j = 0 ; j < 180; j++){
                        TransferReaction(j);

                        xvals1.add(GetKE(P1));
                        yvals1.add(Math.abs(GetThetaLab(P1)));

                        xvals2.add(GetKE(P2));
                        yvals2.add(Math.abs(GetThetaLab(P2)));

                        // momentum transfer to P1
                        xvals3.add(GetThetaLab(P1));
                        double Qx = P2[1] - Pa[1];
                        double Qy = P2[2] - Pa[2];
                        double q = sqrt(Qx * Qx + Qy * Qy);
                        double L1 = sqrt( Math.pow(q*rb/hbar, 2) + 0.25 ) - 0.5;
                        yvals3.add(L1);

                        //momentum transfer to Pb
                        xvals4.add(GetThetaLab(P2));
                        //---- cal the momentum in beam rest frame
                        double PbIx = - gamma * beta* Pb[0] + gamma * Pb[1];
                        double PbIy = Pb[2];

                        double P1Ix = - gamma * beta* P1[0] + gamma * P1[1];
                        double P1Iy = P1[2];

                        Qx = P1Ix - PbIx;
                        Qy = P1Iy - PbIy;
                        q = sqrt(Qx * Qx + Qy * Qy);
                        double L2 = sqrt( Math.pow(q*ra/hbar, 2) + 0.25 ) - 0.5;
                        yvals4.add(L2);


                        if( L1 > maxY2) maxY2 = L1;
                        if( L1 < minY2) minY2 = L1;

                        if( L2 > maxY2) maxY2 = L2;
                        if( L2 < minY2) minY2 = L2;


                        if(abs(yvals1.get(j)) > maxY) maxY = abs(yvals1.get(j));
                        if(abs(yvals2.get(j)) > maxY) maxY = abs(yvals2.get(j));
                        if(xvals1.get(j) > maxX) maxX = xvals1.get(j);
                        if(xvals2.get(j) > maxX) maxX = xvals2.get(j);

                        if(xvals1.get(j) < minX) minX = xvals1.get(j);
                        if(xvals2.get(j) < minX) minX = xvals2.get(j);

                        //Log.i("Read","i " + Integer.toString(j) + " , (x, y) = (" + xvals1.get(j)+", " + yvals1.get(j));
                    }

                    if( maxY > 90){
                        maxY = 180;
                    }else if( 90 >= maxY && maxY >30){
                        maxY = 60;
                    }else{
                        maxY = 30;
                    }

                    maxX = Math.round(Math.ceil(maxX+5)/10.0) * 10;
                    minX = Math.round(Math.floor(minX-5)/10.0) * 10;

                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, (maxX - minX)/10);

                    plot2.setDomainStep(StepMode.INCREMENT_BY_VAL, maxY/10);

                    XYSeries s1 = new SimpleXYSeries(xvals1, yvals1, "P1");
                    XYSeries s2 = new SimpleXYSeries(xvals2, yvals2, "P2");
                    XYSeries s3 = new SimpleXYSeries(xvals3, yvals3, "L(target)");
                    XYSeries s4 = new SimpleXYSeries(xvals4, yvals4, "L(beam)");

                    LineAndPointFormatter s1Format = new LineAndPointFormatter(Color.BLUE, null, null, null);
                    LineAndPointFormatter s2Format = new LineAndPointFormatter(Color.RED, null, null, null);

                    plot.clear();
                    plot.addSeries(s1, s1Format);
                    plot.addSeries(s2, s2Format);

                    plot2.clear();
                    plot2.addSeries(s3, s1Format);
                    plot2.addSeries(s4, s2Format);

                    //Find y range
                    plot.setDomainBoundaries(minX, maxX, BoundaryMode.FIXED);
                    plot.setRangeBoundaries(0, maxY, BoundaryMode.FIXED);
                    plot.redraw();

                    plot2.setDomainBoundaries(0, maxY, BoundaryMode.FIXED);
                    plot2.setRangeBoundaries(Math.floor(minY2), Math.ceil(maxY2), BoundaryMode.FIXED);
                    plot2.redraw();

                    TransferReaction(0);
                    P1vec.setText(displays4vec(P1));
                    P2vec.setText(displays4vec(P2));

                    return true;
                }
                return false;
            }
        });

        thetaCM_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                thetaCM = i;
                theta_reading_display.setText("" + i);

                TransferReaction( i );

                P1vec.setText(displays4vec(P1));
                P2vec.setText(displays4vec(P2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void SetInfo(double TLabMin, double beta, double k){
        infoView.setText(String.format("%7s :  %5.2f [A MeV], %3s : %6.4f,  k : %5.2f[MeV/c]",
                Html.fromHtml("min(T<sub><small>Lab<small></sub>)"),
                TLabMin,
                Html.fromHtml("\u03b2"),
                beta,
                k));
    }

    private String displays4vec(double[] vec){
        //DecimalFormat df = new DecimalFormat("#.###");
        //df.setRoundingMode(RoundingMode.HALF_UP);
        //return "(" + df.format(vec[0]) + ", " + df.format(vec[1]) + ", " + df.format(vec[2]) + ")";

        double angle = Math.atan2(vec[2], vec[1])*180./Math.PI;
        double p = sqrt(vec[1]*vec[1]+vec[2]*vec[2]);
        double mass = sqrt(vec[0]*vec[0]- p*p);
        double T = vec[0] - mass;
        //double beta = p/vec[0];

        String ans = String.format("(%8.1f, %8.1f, %8.1f), (%4.3f, %5.1f)", vec[0], vec[1], vec[2], T, angle);
        return ans;
    }

    private void TransferReaction(double thetaCM){
        // reaction notation a(b,1)2
        double[] Pcm = new double[3];
        Pcm[0] = (Pa[0]+Pb[0])/2;
        Pcm[1] = (Pa[1]+Pb[1])/2;
        Pcm[2] = 0;

        betaInc = Pcm[1]/Pcm[0];
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

        double Etot = Pac[0] + Pbc[0];
        double p = sqrt((Etot - m1.mass - m2.mass)*(Etot + m1.mass - m2.mass)*(Etot - m1.mass + m2.mass)*(Etot + m1.mass + m2.mass));
        k_cm = p/2/Etot;

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

    private double GetKE(double[] vec){
        if( vec.length != 3){
            return 0.0;
        }

        double mass = sqrt(vec[0] * vec[0] - vec[1] * vec[1] - vec[2]*vec[2]);

        return vec[0] - mass;
    }

    private double GetThetaLab(double[] vec){
        if( vec.length != 3){
            return 0.0;
        }

        return  Math.atan2(vec[2], vec[1])*180./Math.PI;
    }

    private double GetMomentum(double[] vec){
        if( vec.length != 3){
            return 0.0;
        }

        return  sqrt(vec[1] * vec[1] + vec[2]*vec[2]);
    }

}
