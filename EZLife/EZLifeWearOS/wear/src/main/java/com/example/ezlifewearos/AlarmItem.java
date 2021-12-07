package com.example.ezlifewearos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AlarmItem {
    private final String itemName;
    private final int itemImageId;
    private final Class itemClass;
    private final boolean itemAddFlag;

    public AlarmItem(String input_name, int input_imageId, Class<? extends Activity> input_class, boolean input_addFlag){
        itemName = input_name;
        itemImageId = input_imageId;
        itemClass = input_class;
        itemAddFlag = input_addFlag;
    }

//    public AppFuncItem(String input_name, int input_imageId, Class<? extends Activity> input_class){
//        iName = input_name;
//        iImageId = input_imageId;
//        iClass = null;
//    }

    public String getName(){
        return itemName;
    }
    public int getImageId(){
        return itemImageId;
    }
    public boolean getAddFlag() { return itemAddFlag; }
    public void launchActivity(Context context){
        Intent intent = new Intent(context, itemClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("AddFlag", itemAddFlag);

        context.startActivity(intent);
    }
}
