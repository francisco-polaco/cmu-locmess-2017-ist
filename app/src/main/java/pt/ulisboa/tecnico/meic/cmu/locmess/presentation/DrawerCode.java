package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.PersistenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LogoutWebService;

public class DrawerCode {

    public static void selectDrawerItem(MenuItem menuItem, Activity activity, ActivityCallback logoutCallback, DrawerLayout drawerLayout, Context context) {
        switch (menuItem.getItemId()) {
            case R.id.Message:
                if (activity.getClass().equals(MessageScreen.class)) break;
                Intent message = new Intent(context, MessageScreen.class);
                message.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(message);
                break;
            case R.id.Locations:
                if (activity.getClass().equals(LocationScreen.class)) break;
                Intent location = new Intent(context, LocationScreen.class);
                location.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(location);
                break;
            case R.id.EditProfile:
                if (activity.getClass().equals(EditProfile.class)) break;
                Intent editprofile = new Intent(context, EditProfile.class);
                editprofile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(editprofile);
                break;
            case R.id.Logout:
                PersistenceManager.getInstance().stopLocationUpdates(activity.getApplicationContext());
                new LogoutWebService(context, logoutCallback).execute();
                break;
        }
        menuItem.setCheckable(false);
        drawerLayout.closeDrawers();

    }
}
