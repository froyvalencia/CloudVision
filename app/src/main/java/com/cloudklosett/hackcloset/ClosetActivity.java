package com.cloudklosett.hackcloset;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

public class ClosetActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private final AppState state = AppState.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f));
        //.setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
        //.setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        state.listFiles(this);
        state.loadAll(this);


        for(Garment garment: state.getAllGarments() ){
            mSwipeView.addView(new ClothesCard(mContext, garment, mSwipeView));
        }
    }

    public void enterCameraMode(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void viewScheduler(View view) {
        Intent intent = new Intent(this, SchedulerActivity.class);
        startActivity(intent);
    }
}
