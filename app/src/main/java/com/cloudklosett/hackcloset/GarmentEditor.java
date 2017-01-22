package com.cloudklosett.hackcloset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class GarmentEditor extends AppCompatActivity {
    ImageView imageView;
    EditText nameEditor;
    Garment garment;
    AppState state = AppState.getInstance();
    Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garment_editor);

        Intent intent = getIntent();
        String garmentId = intent.getStringExtra(CameraActivity.GARMENT_ID_MESSAGE);

        garment = state.getGarment(garmentId);

        nameEditor = (EditText)findViewById(R.id.editText);
        nameEditor.setText(garment.getName(), TextView.BufferType.EDITABLE);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(state.getImage(garment));

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<GarmentTypes>(this, android.R.layout.simple_spinner_item, GarmentTypes.values()));
        typeSpinner.setSelection(getIndex(typeSpinner, garment.getType().toString()));

    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void save(View view) {
        garment.setName(nameEditor.getText().toString());
        garment.setType( GarmentTypes.values()[typeSpinner.getSelectedItemPosition()]);
        state.saveImage(garment.getId(), this);
        state.saveAll(this);
        Intent intent = new Intent(this, ClosetActivity.class);
        startActivity(intent);
    }
}
