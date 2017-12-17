package au.com.timbersmart.logmanagement;

import android.app.Application;

/**
 * Created by nptn1 on 6/12/2017.
 */

public class LogManagement extends Application
{
    private LocalDB localDB= new LocalDB();
    private RemoteDB remoteDB= new RemoteDB();

    public LocalDB getLocalDB(){
        return localDB;
    }

    public RemoteDB getRemoteDB(){
        return remoteDB;
    }
}
