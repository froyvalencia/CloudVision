package com.cloudklosett.hackcloset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class OutfitEditor extends AppCompatActivity {

    AppState state = AppState.getInstance();
    EditText nameEditor;
    private Outfit outfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfit_editor);

        Intent intent = getIntent();

        String id = intent.hasExtra(AppState.OUTFIT_EDITOR_MESSAGE) ?
                intent.getStringExtra(AppState.OUTFIT_EDITOR_MESSAGE) : "";

        outfit = state.getOutfit(id);

        nameEditor.setText(outfit.getName());
    }
}
