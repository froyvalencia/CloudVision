package com.cloudklosett.hackcloset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;

public class OutfitEditor extends AppCompatActivity {

    AppState state = AppState.getInstance();
    EditText nameEditor;
    private Outfit outfit;
    private LinearLayout garmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_editor);

        state.loadAll(this);

        Intent intent = getIntent();

        String id = intent.hasExtra(AppState.OUTFIT_EDITOR_MESSAGE) ?
                intent.getStringExtra(AppState.OUTFIT_EDITOR_MESSAGE) : "";

        outfit = state.getOutfit(id);

        nameEditor = (EditText) findViewById(R.id.nameEdit);
        nameEditor.setText(outfit.getName());

        garmentList = (LinearLayout) findViewById(R.id.garmentList);
    }

    private void makeElement(Garment garment) {
        LinearLayout row = new LinearLayout(this);

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<Garment>(this, android.R.layout.simple_spinner_item, state.getAllGarments()));

        Button delete = new Button(this);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                garmentList.removeView((View) v.getParent());
            }
        });

        garmentList.addView(row);
    }

    private Garment defaultGarment() {
        ArrayList<Garment> allGarms = this.state.getAllGarments();
        if (allGarms.size() > 0)
            return allGarms.get(0);
        return new Garment(new ArrayList<EntityAnnotation>());
    }

    public void addGarment(View view) {
        makeElement(defaultGarment());
    }
}
