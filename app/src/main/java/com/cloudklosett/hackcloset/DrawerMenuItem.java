package com.cloudklosett.hackcloset;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

    @Layout(R.layout.drawer_item)
    public class DrawerMenuItem extends AppCompatActivity{

        public static final int DRAWER_MENU_ITEM_PROFILE = 1;
        public static final int DRAWER_MENU_ITEM_REQUESTS = 2;
        public static final int DRAWER_MENU_ITEM_GROUPS = 3;
        public static final int DRAWER_MENU_ITEM_MESSAGE = 4;

        private int mMenuPosition;
        private Context mContext;
        private DrawerCallBack mCallBack;

        @View(R.id.itemNameTxt)
        private TextView itemNameTxt;

        @View(R.id.itemIcon)
        private ImageView itemIcon;

        public DrawerMenuItem(Context context, int menuPosition) {
            mContext = context;
            mMenuPosition = menuPosition;
        }

        @Resolve
        private void onResolved() {
            switch (mMenuPosition){
                case DRAWER_MENU_ITEM_PROFILE:
                    itemNameTxt.setText("My Outfits");
                    itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart));
                    break;
                case DRAWER_MENU_ITEM_REQUESTS:
                    itemNameTxt.setText("Schedule");
                    itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart));
                    break;
                case DRAWER_MENU_ITEM_GROUPS:
                    itemNameTxt.setText("Add Clothes");
                    itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart));
                    break;
                case DRAWER_MENU_ITEM_MESSAGE:
                    itemNameTxt.setText("Closet");
                    itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart));
                    break;
            }
        }

        @Click(R.id.mainView)
        private void onMenuItemClick(){
            switch (mMenuPosition){
                case DRAWER_MENU_ITEM_PROFILE: {
                    Intent i = new Intent(mContext, ClosetActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    Toast.makeText(mContext, "Make Outfit", Toast.LENGTH_SHORT).show();
                    if (mCallBack != null) mCallBack.onProfileMenuSelected();
                }
                    break;
                case DRAWER_MENU_ITEM_REQUESTS: {
                    Intent i = new Intent(mContext, SchedulerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    //mContext.getClass().goToSchedule();
                    Toast.makeText(mContext, "Schedule", Toast.LENGTH_SHORT).show();
                    if (mCallBack != null) mCallBack.onRequestMenuSelected();
                }
                    break;
                case DRAWER_MENU_ITEM_GROUPS: {
                    Intent i = new Intent(mContext, CameraActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    Toast.makeText(mContext, "Add Clothes", Toast.LENGTH_SHORT).show();
                    if (mCallBack != null) mCallBack.onGroupsMenuSelected();
                }
                    break;
                case DRAWER_MENU_ITEM_MESSAGE: {
                    Intent i = new Intent(mContext, ClosetActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    Toast.makeText(mContext, "Closet", Toast.LENGTH_SHORT).show();
                    if (mCallBack != null) mCallBack.onMessagesMenuSelected();
                }
                    break;
            }
        }

        public void setDrawerCallBack(DrawerCallBack callBack) {
            mCallBack = callBack;
        }

        public interface DrawerCallBack{
            void onProfileMenuSelected();
            void onRequestMenuSelected();
            void onGroupsMenuSelected();
            void onMessagesMenuSelected();

        }
    }



