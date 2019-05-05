package com.ieeevit.eventoadmin.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ieeevit.eventoadmin.R;

public class AddScannableActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String[] Scannables={"Select the Tab","Meal","Session","Swag","Event Registration","Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scannable);

        EditText sccanable_name = (EditText) findViewById(R.id.scannable_name);
        String scc_name = sccanable_name.getText().toString();

        EditText sccanable_des = (EditText) findViewById(R.id.scannable_desc);
        String scc_des = sccanable_name.getText().toString();

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.Spinner);
        spin.setOnItemSelectedListener(this);

//Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Scannables);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if (Scannables[position] != "Select the Tab") {
            Toast.makeText(getApplicationContext(), Scannables[position], Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }
}
