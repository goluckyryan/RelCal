package com.user.relcal;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;
import static java.lang.Math.sqrt;

public class Nucleus extends AppCompatActivity {

    public int A, Z;
    public double mass, massError, BEA, Sp, Sn;
    public String Sym, Name;

    AssetManager assetManager;

    public Nucleus(AssetManager asset){
        this.assetManager = asset;
        this.A = 1;
        this.Z = 1;
        this.BEA = 0;
        this.Sp = 0;
        this.Sn = 0;
        this.Sym = "H";
        this.mass = 938.272046;
        this.massError = 0; // keV
        this.Name = "1H";
    }

    public Nucleus(int A, int Z, AssetManager asset){
        this.assetManager = asset;
        this.A = A;
        this.Z = Z;
        FindMassByBEA(A, Z); // also located mass, massError, and Sym;
        this.Sp = 0;
        this.Sn = 0;
        this.Name =  "" + A + this.Sym;
    }


    public Nucleus(String name, AssetManager asset){
        this.assetManager = asset;
        this.Sp = 0;
        this.Sn = 0;
        if( name.equals("p")) {
            mass = 938.272046;
            massError = 0;
            BEA = 0;
            A = 1;
            Z = 1;
            Sym = "H";
            Name = "1H";
        }else if (name.equals("n")) {
            mass = 939.5654;
            massError = 0;
            BEA = 0;
            A = 1;
            Z = 0;
            Sym = "n";
            Name = "1 n";
        }else if( name.equals("d")) {
            FindMassByBEA(2, 1);
            A = 2;
            Z = 1;
            this.Name = "" + A + this.Sym;
        }else if( name.equals("a")) {
            FindMassByBEA(4, 2);
            A = 4;
            Z = 2;
            this.Name = "" + A + this.Sym;
        }else if( name.equals("t")){
            FindMassByBEA( 3, 1);
            A = 3;
            Z = 1;
            this.Name = "" + A + this.Sym;
        }else {
            FindMassByName(name);
            this.Name = "" + A + this.Sym;
        }
    }

    public void CalSp(){
        this.Sp = FindSp(0, A, Z);
        this.Sn = FindSp(1, A, Z);
    }

    private double FindMassByName(String name){

        int A_tmp, Z_tmp, numLine = 1;

        double mp = 938.272046;
        double mn = 939.565378;

        //AssetManager assetManager = getAssets();
        BufferedReader reader = null;

        int numLineStart = 40;
        int numLineEnd = 3392;
        int flag = 0;

        //extract the mass number, if any
        String A_string = name.replaceAll("[^\\d]", "");
        int A_num;
        if( A_string.length() == 0){
            A_num = 0;
        }else{
            A_num = Integer.parseInt(A_string);

            if ( A_num >= 50 && A_num <  100){
                numLineStart = 454;
            }else if (A_num >= 100 && A_num < 150){
                numLineStart = 1100;
            }else if (A_num >= 150 && A_num < 200){
                numLineStart = 1899;
            }else if (A_num >= 200){
                numLineStart = 2622;
            }
        }
        String symbol = name.replaceAll("[0-9]","");

        Log.i("Read", "Mass number = " + Integer.toString(A_num) + ", Symbol = " + symbol);

        try {
            InputStream massData = assetManager.open("mass16.txt");
            reader = new BufferedReader(new InputStreamReader(massData));
            String text;
            //     Log.i("Read","flag = " + Integer.toString(flag)+", numLine = " + Integer.toString(numLine));
            // repeat until all lines is read
            while ((text = reader.readLine()) != null && flag == 0) {
                if(numLine >= numLineStart && numLine <=numLineEnd){
                    //	Log.i("Read","numLine ="+Integer.toString(numLine)) ;
                    //	Log.i("Read",text.toString()) ;
                    //	Log.i("Read","A_tmp string = " + text.substring(15, 19)) ;
                    //	Log.i("Read","Z_tmp string = " + text.substring(10, 14)) ;
                    String Name_tmp = text.substring(20,22);
                    // need to delete space
                    Name_tmp = Name_tmp.replace(" ", "");
                    A_tmp = parseInt(text.substring(15, 19).replaceAll("\\s",""));
                    Z_tmp = parseInt(text.substring(10, 14).replaceAll("\\s",""));

                    //Log.i("Read", name + " | Name = " + Name_tmp + ", A_tmp ="+Integer.toString(A_tmp)+", Z_tmp = "+Integer.toString(Z_tmp)) ;

                    if( A_num == 0) {
                        if (Name_tmp.equals(symbol)) {
                            //Log.i("Read","mDefect string = " + text.substring(54, 65)) ;
                            BEA = Double.parseDouble(text.substring(54, 65).replaceAll("#", "").replaceAll("\\s", "")) / 1000.;
                            massError = Double.parseDouble(text.substring(66, 73).replaceAll("#", "").replaceAll("\\s", ""));
                            Log.i("Read", "mDefect per A =" + Double.toString(BEA) + " , error = " + Double.toString(massError));
                            A = A_tmp;
                            Z = Z_tmp;
                            mass = Z * mp + (A - Z) * mn - A * BEA;
                            Sym = Name_tmp;
                            //Log.i("Read","mass ="+Double.toString(mass)) ;
                            flag = 1;
                            break;
                        } else {
                            Log.i("Read", symbol + " != " + Name_tmp);
                        }
                    }else{
                        if (Name_tmp.equals(symbol) && A_tmp == A_num) {
                            //Log.i("Read","mDefect string = " + text.substring(54, 65)) ;
                            BEA = Double.parseDouble(text.substring(54, 65).replaceAll("#", "").replaceAll("\\s", "")) / 1000.;
                            massError = Double.parseDouble(text.substring(66, 73).replaceAll("#", "").replaceAll("\\s", ""));
                            Log.i("Read", "mDefect per A =" + Double.toString(BEA) + " , error = " + Double.toString(massError));
                            A = A_tmp;
                            Z = Z_tmp;
                            mass = Z * mp + (A - Z) * mn - A * BEA;
                            Sym = Name_tmp;
                            //Log.i("Read","mass ="+Double.toString(mass)) ;
                            flag = 1;
                            break;
                        } else {
                            Log.i("Read", symbol + " != " + Name_tmp + " and " + A_num + " != " + A_tmp);
                        }
                    }

                }
                numLine ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Error",e.toString()) ;
            mass = -3;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (flag ==0){
            mass = -404;
            massError = -404;
            BEA = -404;
            Sym = "non";
            A = -1;
            Z = -1;
        }

        return mass;

    }

    public double CalMomentum(double KE){
        return sqrt(2*mass*KE + KE*KE);
    }

    public void SetEx(double Ex){
        mass = mass + Ex;
    }

    private double FindMassByBEA(int a,int z){
        //	Log.i("Read","a ="+Integer.toString(a)+", z = "+Integer.toString(z)) ;
        int A_tmp, Z_tmp, numLine=1;

        double mp = 938.272046;
        double mn = 939.565378;

        int flag = 0;
        // problem is this class is not within content, so getAssets will return null.

        //AssetManager assetManager = getAssets();
        BufferedReader reader = null;

        int numLineStart = 40;
        int numLineEnd = 3392;

        if ( a >= 50 && a <  100){
            numLineStart = 454;
        }else if (a >= 100 && a < 150){
            numLineStart = 1100;
        }else if (a >= 150 && a < 200){
            numLineStart = 1899;
        }else if (a >= 200){
            numLineStart = 2622;
        }
        Log.i("Read","numLineStart = "+Integer.toString(numLineStart)) ;

        try {
            InputStream massData = assetManager.open("mass16.txt");
            reader = new BufferedReader(new InputStreamReader(massData));

            String text = null;
            //     Log.i("Read","flag = " + Integer.toString(flag)+", numLine = " + Integer.toString(numLine));
            // repeat until all lines is read
            while ((text = reader.readLine()) != null && flag == 0) {
                if(numLine >= numLineStart && numLine <=numLineEnd){
                    //	Log.i("Read","numLine ="+Integer.toString(numLine)) ;
                    //	Log.i("Read",text.toString()) ;
                    //	Log.i("Read","A_tmp string = " + text.substring(15, 19)) ;
                    //	Log.i("Read","Z_tmp string = " + text.substring(10, 14)) ;
                    A_tmp = parseInt(text.substring(15, 19).replaceAll("\\s",""));
                    Z_tmp = parseInt(text.substring(10, 14).replaceAll("\\s",""));
                    //	Log.i("Read","A_tmp ="+Integer.toString(A_tmp)+", Z_tmp = "+Integer.toString(Z_tmp)) ;
                    if(A_tmp == a && Z_tmp == z){
                        //Log.i("Read","mDefect string = " + text.substring(54, 65)) ;
                        BEA = Double.parseDouble(text.substring(54, 65).replaceAll("#","").replaceAll("\\s",""))/1000.;
                        massError = Double.parseDouble(text.substring(66,73).replaceAll("#","").replaceAll("\\s",""));
                        Log.i("Read","mDefect per A ="+Double.toString(BEA) +  " , error = " + Double.toString(massError)) ;
                        mass = z*mp + (a-z)*mn - a*BEA;
                        Sym = text.substring(20,22);
                        //Log.i("Read","mass ="+Double.toString(mass)) ;
                        flag = 1;
                    }else if (A_tmp > a){
                        Log.i("Read","A_temp > a = "+Integer.toString(A_tmp)+". Stop search.") ;
                        break;
                    }

                }
                numLine ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Error",e.toString()) ;
            mass = -3;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (flag ==0){
            mass = -404;
            BEA = -404;
            Sym = "non";
            massError = -404;
        }

        return mass;
    }

    private double FindSp(int id, int a,int z){

        if (a == 1 ){
            return 0;
        }

        double mass3 = 0;
        double massRes = 0;

        if ( id == 0){
            mass3 = 938.272046; // proton
            massRes = FindMassByBEA(a-1, z-1);
        }else if (id == 1){
            mass3 = 939.565378; // neutron
            massRes = FindMassByBEA(a-1, z);
        }

        double mass1 = FindMassByBEA(a, z);
        Log.i("Read","id = "+id+", massRes = "+Double.toString(massRes)) ;

        if (mass1 != -404 && massRes != -404){
            return massRes + mass3 - mass1;
        }else{
            return -404;
        }

    }


}
