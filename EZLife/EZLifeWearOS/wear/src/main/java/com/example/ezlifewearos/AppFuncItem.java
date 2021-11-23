package com.example.ezlifewearos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AppFuncItem {
    private final String iName;
    private final int iImageId;
    private final Class iClass;

    public AppFuncItem(String input_name, int input_imageId, Class<? extends Activity> input_class){
        iName = input_name;
        iImageId = input_imageId;
        iClass = input_class;
    }

    public AppFuncItem(String input_name, int input_imageId){
        iName = input_name;
        iImageId = input_imageId;
        iClass = null;
    }

    public String getName(){
        return iName;
    }
    public int getImageId(){
        return iImageId;
    }
    public void launchActivity(Context context){
        Intent intent = new Intent(context, iClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
