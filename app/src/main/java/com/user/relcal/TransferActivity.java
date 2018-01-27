package com.user.relcal;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import static java.lang.Integer.parseInt;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


public class TransferActivity extends AppCompatActivity {

    Nucleus ma, mb, m1, m2;
    Double TLab, Ex;
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

    TextView Pavec;
    TextView Pbvec;
    TextView P1vec;
    TextView P2vec;

    EditText TLabInput;
    SeekBar thetaCM_seekBar;
    TextView theta_reading_display;
    EditText ExInput;

    private XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Transfer Reaction");

        assetManager = getAssets();
        ma = new Nucleus(12, 6, assetManager);
        mb = new Nucleus(2, 1 ,assetManager);
        m1 = new Nucleus(1, 1, assetManager );
        m2 = new Nucleus( ma.A + ma.A - mb.A, ma.Z + ma.Z - mb.Z, assetManager );

        PaName = findViewById(R.id.editText_Pa);
        PbName = findViewById(R.id.editText_Pb);
        P1Name = findViewById(R.id.editText_P1);
        P2Name = findViewById(R.id.textView_P2);

        Pavec = findViewById(R.id.textView_4vPa);
        Pbvec = findViewById(R.id.textView_4vPb);
        P1vec = findViewById(R.id.textView_4vP1);
        P2vec = findViewById(R.id.textView_4vP2);

        TLabInput = findViewById(R.id.editText_TLab);
        thetaCM_seekBar = findViewById(R.id.seekBar);
        theta_reading_display = findViewById(R.id.textView_theta_reading);
        ExInput = findViewById(R.id.editText_Ex);

        plot = findViewById(R.id.plot);

        XYSeries s1 = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"series1", 1,5,2,8,3,9);

        plot.addSeries(s1, new LineAndPointFormatter(Color.GREEN, Color.BLUE, null, null));

        PaName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = PaName.getText().toString();
                    ma = new Nucleus(nameA, assetManager);
                    Pavec.setText("" + ma.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - mb.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    return true;
                }
                return false;
            }
        });

        PbName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = PbName.getText().toString();
                    mb = new Nucleus(nameA, assetManager);
                    Pbvec.setText("" + mb.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - mb.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    return true;
                }
                return false;
            }
        });

        P1Name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    String nameA = P1Name.getText().toString();
                    m1 = new Nucleus(nameA, assetManager);
                    P1vec.setText("" + m1.mass);

                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - mb.Z, assetManager );
                    P2Name.setText(m2.Name);
                    P2vec.setText(""+m2.mass);

                    return true;
                }
                return false;
            }
        });

        TLabInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER){

                    ma = new Nucleus(PaName.getText().toString(), assetManager);
                    mb = new Nucleus(PbName.getText().toString(), assetManager);
                    m1 = new Nucleus(P1Name.getText().toString(), assetManager);
                    m2 = new Nucleus( mb.A + ma.A - m1.A, mb.Z + ma.Z - mb.Z, assetManager );
                    P2Name.setText(m2.Name);

                    TLab = Double.parseDouble(TLabInput.getText().toString());
                    Ex = Double.parseDouble(ExInput.getText().toString());
                    m2.SetEx(Ex);

                    // calculate 4-vector;

                    Pa[0] = ma.mass + TLab * ma.A;
                    Pa[1] = ma.CalMomentum( TLab * ma.A);
                    Pa[2] = 0;
                    Pavec.setText(displays4vec(Pa));

                    Pb[0] = mb.mass;
                    Pb[1] = 0;
                    Pb[2] = 0;
                    Pbvec.setText(displays4vec(Pb));

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

                TransferReaction();

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

    private String displays4vec(double[] vec){
        //DecimalFormat df = new DecimalFormat("#.###");
        //df.setRoundingMode(RoundingMode.HALF_UP);
        //return "(" + df.format(vec[0]) + ", " + df.format(vec[1]) + ", " + df.format(vec[2]) + ")";

        double angle = Math.atan2(vec[2], vec[1])*180./Math.PI;
        double p = sqrt(vec[1]*vec[1]+vec[2]*vec[2]);
        //double mass = sqrt(vec[0]*vec[0]- p*p);
        //double T = vec[0] - mass;
        double beta = p/vec[0];

        String ans = String.format("(%8.1f, %8.1f, %8.1f), (%4.3f, %5.1f)", vec[0], vec[1], vec[2], beta, angle);
        return ans;
    }

    private void TransferReaction(){
        double[] Pcm = new double[3];
        Pcm[0] = (Pa[0]+Pb[0])/2;
        Pcm[1] = (Pa[1]+Pb[1])/2;
        Pcm[2] = 0;

        double beta = Pcm[1]/Pcm[0];
        double gamma = 1/sqrt(1-beta*beta);

        double[] Pac = new double[3];
        double[] Pbc = new double[3];
        double[] P1c = new double[3];
        double[] P2c = new double[3];

        Pac[0] = gamma * Pa[0] - gamma * beta * Pa[1];
        Pac[1] = -gamma * beta * Pa[0] + gamma * Pa[1];
        Pac[2] = 0;

        Pbc[0] = gamma * Pb[0] - gamma * beta * Pb[1];
        Pbc[1] = -gamma * beta * Pb[0] + gamma * Pb[1];
        Pbc[2] = 0;

        double Etot = Pac[0] + Pbc[0];
        double p = sqrt((Etot - m1.mass - m2.mass)*(Etot + m1.mass - m2.mass)*(Etot - m1.mass + m2.mass)*(Etot + m1.mass + m2.mass));
        p = p/2/Etot;

        P1c[0] = sqrt(m1.mass*m1.mass + p*p);
        P1c[1] = p * cos(thetaCM * Math.PI/180.);
        P1c[2] = p * sin(thetaCM * Math.PI/180.);

        P2c[0] = sqrt(m2.mass*m2.mass + p*p);
        P2c[1] = - P1c[1];
        P2c[2] = - P1c[2];

        P1[0] = gamma * P1c[0] + gamma * beta * P1c[1];
        P1[1] = gamma * beta * P1c[0] + gamma * P1c[1];
        P1[2] = P1c[2];

        P2[0] = gamma * P2c[0] + gamma * beta * P2c[1];
        P2[1] = gamma * beta * P2c[0] + gamma * P2c[1];
        P2[2] = P2c[2];

    }

}
