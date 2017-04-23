package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LogoutWebService;

public class DrawerCode {

    public static void selectDrawerItem(MenuItem menuItem, Activity activity, DrawerLayout drawerLayout, Context context) {
        switch (menuItem.getItemId()) {
            case R.id.Message:
                Intent message = new Intent(context, MainScreen.class);
                activity.startActivity(message);
                break;
            case R.id.Locations:
                Intent location = new Intent(context, LocationScreen.class);
                activity.startActivity(location);
                break;
            case R.id.EditProfile:
                Intent editprofile = new Intent(context, EditProfile.class);
                activity.startActivity(editprofile);
                break;
            case R.id.Logout:
                new LogoutWebService(context, (ActivityCallback) activity).execute();
                break;
        }
        menuItem.setCheckable(false);
        drawerLayout.closeDrawers();

    }
}
