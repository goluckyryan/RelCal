package com.user.relcal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    double c = 299.792458; // speed of light mm/ns

    Nucleus isotope;

    double mass = 938.272046;
    double KE = 0;
    double momt = 0;
    double Gamma = 1;
    double beta = 0;
    double length = 1000;
    double tof = 1000000;
    double Brho = 0;
    int Z = 1; // number of unit charge of particle
    int A = 1; // number of unit charge of particle

    boolean editedName = false;

    AssetManager assetManager;

    EditText mass_dis;
    EditText Sym_dis;
    EditText BEA_dis;
    EditText Sp_dis;
    EditText Sn_dis;
    EditText Z_dis;
    EditText A_dis;
    EditText KE_dis;
    EditText momt_dis;
    EditText Gamma_dis;
    EditText beta_dis;
    EditText length_dis;
    EditText tof_dis;
    EditText Brho_dis;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assetManager = getAssets();

        Button mass_Cal  = findViewById(R.id.mass_view);
        Button ke_Cal    = findViewById(R.id.ke_but);
        Button momt_Cal  = findViewById(R.id.momt_but);
        Button gamma_Cal = findViewById(R.id.gamma_but);
        Button beta_Cal  = findViewById(R.id.beta_but);
        Button tof_Cal   = findViewById(R.id.tof_but);
        Button Brho_Cal  = findViewById(R.id.Brho_but);

        mass_dis   = findViewById(R.id.mass_text);
        Sp_dis     = findViewById(R.id.Sp_text);
        Sn_dis     = findViewById(R.id.Sn_text);
        Sym_dis    = findViewById(R.id.Sym_text);
        BEA_dis    = findViewById(R.id.BEA_text);
        Z_dis      = findViewById(R.id.Z_text);
        A_dis      = findViewById(R.id.A_text);
        KE_dis     = findViewById(R.id.ke_text);
        momt_dis   = findViewById(R.id.momt_text);
        Gamma_dis  = findViewById(R.id.gamma_text);
        beta_dis   = findViewById(R.id.beta_text);
        length_dis = findViewById(R.id.length_text);
        tof_dis    = findViewById(R.id.tof_text);
        Brho_dis   = findViewById(R.id.Brho_text);

        isotope = new Nucleus(assetManager);

        mass_dis.setText(massExp(isotope.mass, isotope.massError));
        Z_dis.setText("" + isotope.Z);
        A_dis.setText("" + isotope.A);
        Sym_dis.setText(isotope.Name);
        BEA_dis.setText(display(isotope.BEA));
        Sp_dis.setText("0.0" );
        Sn_dis.setText("0.0" );
        KE_dis.setText("" + KE);
        momt_dis.setText("" + momt);
        Gamma_dis.setText("" + Gamma);
        beta_dis.setText("" + beta);
        tof_dis.setText("" + tof);
        length_dis.setText("" + length);
        Brho_dis.setText("" + Brho);

        Sym_dis.setOnTouchListener(new EditText.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                editedName = true;
                return false;
            }
        });

        Z_dis.setOnTouchListener(new EditText.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                editedName = false;
                return false;
            }
        });

        A_dis.setOnTouchListener(new EditText.OnTouchListener(){
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                editedName = false;
                return false;
            }
        });

        A_dis.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    Z = parseInt(Z_dis.getText().toString());
                    A = parseInt(A_dis.getText().toString());
                    Log.i("Read","A = "+Integer.toString(A)+", Z = "+Integer.toString(Z)) ;

                    isotope = new Nucleus(A, Z, assetManager);

                    mass_dis.setText(massExp(isotope.mass, isotope.massError));
                    Sym_dis.setText(isotope.Name);
                    BEA_dis.setText(display(isotope.BEA));

                    isotope.CalSp();
                    Sp_dis.setText(display(isotope.Sp));
                    Sn_dis.setText(display(isotope.Sn));

                    return true;
                }
                return false;
            }
        });

        Sym_dis.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && i == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String name = Sym_dis.getText().toString();

                    isotope = new Nucleus(name, assetManager);

                    mass_dis.setText(massExp(isotope.mass, isotope.massError));
                    Z_dis.setText("" + isotope.Z);
                    A_dis.setText("" + isotope.A);
                    Sym_dis.setText(isotope.Name);
                    BEA_dis.setText(display(isotope.BEA));

                    isotope.CalSp();
                    Sp_dis.setText(display(isotope.Sp));
                    Sn_dis.setText(display(isotope.Sn));

                    return true;
                }
                return false;
            }
        });

        mass_Cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if( editedName == false) {
                    Z = parseInt(Z_dis.getText().toString());
                    A = parseInt(A_dis.getText().toString());
                    Log.i("Read","A = "+Integer.toString(A)+", Z = "+Integer.toString(Z)) ;

                    isotope = new Nucleus(A, Z, assetManager);

                    mass_dis.setText(massExp(isotope.mass, isotope.massError));
                    Sym_dis.setText(isotope.Name);
                    BEA_dis.setText(display(isotope.BEA));

                    isotope.CalSp();
                    Sp_dis.setText(display(isotope.Sp));
                    Sn_dis.setText(display(isotope.Sn));

                }else{
                    String name = Sym_dis.getText().toString();

                    isotope = new Nucleus(name, assetManager);

                    mass_dis.setText(massExp(isotope.mass, isotope.massError));
                    Z_dis.setText("" + isotope.Z);
                    A_dis.setText("" + isotope.A);
                    Sym_dis.setText(isotope.Name);
                    BEA_dis.setText(display(isotope.BEA));

                    isotope.CalSp();
                    Sp_dis.setText(display(isotope.Sp));
                    Sn_dis.setText(display(isotope.Sn));
                }

                editedName = false;

            }
        });

        ke_Cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                KE = Double.parseDouble(KE_dis.getText().toString()) * isotope.A;

                Gamma = (KE + mass) / mass;
                beta = Math.pow((1 - 1 / Math.pow(Gamma, 2)), 0.5);
                momt = mass * Gamma * beta;
                tof = length / (beta * c);
                Brho = mass * Gamma * beta / c / Z;

                momt_dis.setText("" + momt);
                Gamma_dis.setText("" + Gamma);
                beta_dis.setText("" + beta);
                tof_dis.setText("" + tof);
                Brho_dis.setText("" + Brho);

            }
        });

        momt_Cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                momt = Double.parseDouble(momt_dis.getText().toString());

                KE = (Math.sqrt(Math.pow(mass,2)+Math.pow(momt,2)) - mass)/A;
                Gamma = (KE + mass) / mass;
                beta = Math.pow((1 - 1 / Math.pow(Gamma, 2)), 0.5);
                momt = mass * Gamma * beta;
                tof = length / (beta * c);
                Brho = mass * Gamma * beta / c / Z;

                KE_dis.setText("" + KE);
                Gamma_dis.setText("" + Gamma);
                beta_dis.setText("" + beta);
                tof_dis.setText("" + tof);
                Brho_dis.setText("" + Brho);

            }
        });


        gamma_Cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                Gamma = Double.parseDouble(Gamma_dis.getText().toString());

                KE = (Gamma - 1) * mass / A;
                beta = Math.pow((1 - 1 / Math.pow(Gamma, 2)), 0.5);
                momt = mass * Gamma * beta;
                tof = length / (beta * c);
                Brho = mass * Gamma * beta / c / Z;

                KE_dis.setText("" + KE);
                momt_dis.setText("" + momt);
                beta_dis.setText("" + beta);
                tof_dis.setText("" + tof);
                Brho_dis.setText("" + Brho);

            }
        });

        beta_Cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                beta = Double.parseDouble(beta_dis.getText().toString());

                Gamma = 1 / Math.pow(1 - Math.pow(beta, 2), 0.5);
                KE = (Gamma - 1) * mass / A;
                momt = mass * Gamma * beta;
                tof = length / (beta * c);
                Brho = mass * Gamma * beta / c / Z;

                KE_dis.setText("" + KE);
                momt_dis.setText("" + momt);
                Gamma_dis.setText("" + Gamma);
                tof_dis.setText("" + tof);
                Brho_dis.setText("" + Brho);

            }
        });

        tof_Cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                tof = Double.parseDouble(tof_dis.getText().toString());

                beta = length / (tof * c);
                Gamma = 1 / Math.pow(1 - Math.pow(beta, 2), 0.5);
                momt = mass * Gamma * beta;
                KE = (Gamma - 1) * mass / A;
                Brho = mass * Gamma * beta / c  / Z;

                KE_dis.setText("" + KE);
                momt_dis.setText("" + momt);
                Gamma_dis.setText("" + Gamma);
                beta_dis.setText("" + beta);
                Brho_dis.setText("" + Brho);

            }
        });

        Brho_Cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Z = parseInt(Z_dis.getText().toString());
                A = parseInt(A_dis.getText().toString());
                length = Double.parseDouble(length_dis.getText().toString());

                isotope = new Nucleus(A,Z, assetManager);
                mass_dis.setText(massExp(isotope.mass, isotope.massError));
                Sym_dis.setText(isotope.Name);
                BEA_dis.setText(display(isotope.BEA));
                isotope.CalSp();
                Sp_dis.setText(display(isotope.Sp));
                Sn_dis.setText(display(isotope.Sn));

                mass = isotope.mass;

                Brho = Double.parseDouble(Brho_dis.getText().toString());

                Gamma = Math.pow(Math.pow(Brho * Z  * c / mass, 2) + 1,0.5);
                KE = (Gamma - 1) * mass / A;
                beta = Math.pow((1 - 1 / Math.pow(Gamma, 2)), 0.5);
                momt = mass * Gamma * beta;
                tof = length / (beta * c);

                //	ShowResult(mass, KE, momt, Gamma, beta, tof, Brho);

                KE_dis.setText("" + KE);
                momt_dis.setText("" + momt);
                Gamma_dis.setText("" + Gamma);
                beta_dis.setText("" + beta);
                tof_dis.setText("" + tof);

            }
        });

    }

    private String massExp(double mass, double error){
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return "" + df.format(mass) + " (" + error + " keV)";
    }

    private String display(double value){
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return "" + df.format(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_transfer) {
            Intent intent = new Intent (this, TransferActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_Helios){
            Intent intent = new Intent (this, HELIOS.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}