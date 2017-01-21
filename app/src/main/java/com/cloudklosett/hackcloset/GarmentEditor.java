package com.cloudklosett.hackcloset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class GarmentEditor extends AppCompatActivity {
    ImageView imageView;
    EditText nameEditor;
    Garment garment;
    AppState state = AppState.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garment_editor);

        Intent intent = getIntent();
        String garmentId = intent.getStringExtra(MainActivity.GARMENT_ID_MESSAGE);

        garment = state.getGarment(garmentId);

        nameEditor = (EditText)findViewById(R.id.editText);
        nameEditor.setText(garment.getName(), TextView.BufferType.EDITABLE);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(state.getImage(garment));
    }

    public void save(View view) {
         try {
             state.saveAll(this);
         } catch (Exception e) {
             Log.e("Save Error", Log.getStackTraceString(e), e);
         }
    }
}
