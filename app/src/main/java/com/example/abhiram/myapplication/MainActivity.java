package com.example.abhiram.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Handler h = new Handler();
    boolean permissionFlag=false;
    File rootDataDir = MainActivity.this.getDataDir();
    String rootDir=rootDataDir.toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check();

    }

    public void check() {
        TextView display = (TextView) findViewById(R.id.display);
        TextView details = (TextView) findViewById(R.id.details);
        h.postDelayed(r, 1000);
        FileReader reader,reader1;
        BufferedReader bufferedReader, bufferedReader1;
        try {
            if(!permissionFlag) {
                reader = new FileReader("/sys/class/power_supply/usb/type");  //Opening file
                bufferedReader = new BufferedReader(reader);                        //Buffer class to read and buffer data
                reader1 = new FileReader("/sys/class/power_supply/usb/uevent");
                bufferedReader1 = new BufferedReader(reader1);
            }
            else{
                String command="cp /sys/class/power_supply/usb/uevent /sys/class/power_supply/usb/type " + rootDir.trim();
                //Process SU = Runtime.getRuntime().exec(command);
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(command + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                String Fname=rootDir.trim() + "/type";
                reader = new FileReader(Fname);  //Opening file
                bufferedReader = new BufferedReader(reader);                        //Buffer class to read and buffer data
                Fname=rootDir.trim() + "/uevent";
                reader1 = new FileReader(Fname);
                bufferedReader1 = new BufferedReader(reader1);
            }
            String line, eqvalent="";
            while ((line = bufferedReader.readLine()) != null) {
                eqvalent = eqvalent + line;
            }
            if(eqvalent.trim().equals("USB")){
                eqvalent="PC connected";
            }
            else if(eqvalent.trim().equals("USB_DCP")){
                eqvalent="5V normal charger connected";
            }
            else if(eqvalent.trim().equals("USB_HVDCP")){
                eqvalent="Qualcomm Quick Charge 2 connected";
            }
            else if(eqvalent.trim().equals("USB_HVDCP_3")){
                eqvalent="Qualcomm Quick Charge 3 connected";
            }
            display.setText(eqvalent);
            String detailsS;
            details.setText("");
            while ((detailsS = bufferedReader1.readLine()) != null) {
                details.append(detailsS+"\n");
            }
            //details.setText(String.valueOf(bufferedReader1.read()));
            reader.close();
            reader1.close();
        } catch (IOException e) {
            display.setText(R.string.permissionError);
            details.setText(R.string.detailsError);
        }


    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            check();
        }
    };

}