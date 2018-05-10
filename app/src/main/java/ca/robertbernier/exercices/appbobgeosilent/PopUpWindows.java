package ca.robertbernier.exercices.appbobgeosilent;
import android.content.Context;
import android.widget.PopupWindow;

/**
 * Created by bob on 1/15/2018.
 */

public class PopUpWindows {
    PopupWindow  mPopUpWindow;

    public void PopupWindows(Context context) {
        Context   mContext = context;
         mPopUpWindow = new PopupWindow(context);
    }
}


