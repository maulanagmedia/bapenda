package com.example.bappeda.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.DialogChooserAdapter;
import com.example.bappeda.R;

import com.example.bappeda.R;

import com.example.bappeda.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DialogFactory {
    private static final DialogFactory ourInstance = new DialogFactory();

    public static DialogFactory getInstance() {
        return ourInstance;
    }

    private DialogFactory() {

    }

    public Dialog createDialog(Activity activity, int res_id, int widthpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100, WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

    public Dialog createDialogTrans(Activity activity, int res_id, int widthpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100, WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }
        return dialog;
    }

    public Dialog createDialog(Activity activity, int res_id, int widthpercentage, int heightpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

    public Dialog createDialog(Activity activity, int res_id, int anim_id, int widthpercentage, int heightpercentage){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            //lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = anim_id;
            dialog.getWindow().setAttributes(lp);
        }

        return dialog;
    }

    public Dialog createDialog(Activity activity, int res_id, int widthpercentage,
                               int heightpercentage, final DialogActionListener listener){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    listener.onBackPressed(dialog);
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    private Dialog createChooserDialog(Activity activity, int res_id, int widthpercentage,
                                       int heightpercentage, final DialogActionListener listener){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalWidth = size.x;
        int device_TotalHeight = size.y;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 ,
                    device_TotalHeight * heightpercentage / 100);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

    public interface DialogActionListener{
        void onBackPressed(Dialog dialog);
    }

    public interface ChooserDialogListener{
        void onSelect(String id, String value);
    }
}
