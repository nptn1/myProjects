package au.com.timbersmart.logmanagement;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;


/**
 * Created by nptn1 on 7/12/2017.
 */

public class RemoteDB extends Application {

    public static RemoteDB remoteDB=new RemoteDB() ;
    Connection conn;

    RemoteDB(){

    }

    public RemoteDB getRemoteDB(){
        return remoteDB;
    }

    public void remoteDBConnect (Context ctx)
    {
        // LOCAL PC IP from AVD, similar to 127.0.0.1
        String ip = "10.0.2.2";
        String classs = "net.sourceforge.jtds.jdbc.Driver";
        String db = "TimberSmartOne_TLB";
        String un = "sa";
        String password = "igit00";

        // TS SERVER
/*        String ip = "192.168.0.10";
        String classs = "net.sourceforge.jtds.jdbc.Driver";
        String db = "TimbersmartMvVantage";
        String un = "timbersmart";
        String password = "T1mberSm@rt"; */

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
            Toast.makeText(ctx, "DB Connection Failed!", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
            Toast.makeText(ctx, "DB Connection Failed!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
            Toast.makeText(ctx, "DB Connection Failed!", Toast.LENGTH_LONG).show();
        }
        finally {
            Toast.makeText(ctx, "DB Host:" + ip+",Instance:"+db+"!", Toast.LENGTH_LONG).show();
        }
        //return conn;
        return ;
    }

    public void syncRemoteToDlocal( LocalDB myLocalDB)
    {
        try {

            //*********************************USERS*******************************************
            // clear all users
            myLocalDB.deleteAllRecords("LiteUser");

            // re-populate
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(" SELECT UserName, Password, UserID FROM tuser ");

            while (rs.next())
            {
                myLocalDB.insertUser((String)rs.getString(1), (String)rs.getString(2), (int)rs.getInt(3));
            }

            // clear all users
            myLocalDB.deleteAllRecords("LiteLocation");

            //*********************************LOCATION*******************************************
            // re-populate Location.
            rs= stmt.executeQuery(" SELECT locationID, LocationName, LocationDescription FROM  tlocation; ");

            // list of fields, types, and values.
            ArrayList<String> fields = new ArrayList<String>(Arrays.asList("LocationID","LocationName","LocationDescription"));
            ArrayList types = new ArrayList(Arrays.asList(myLocalDB.TYPE_INT, myLocalDB.TYPE_CHAR, myLocalDB.TYPE_CHAR));

            while (rs.next())
            {

                Vector values = new Vector();
                values.add(0, (String)rs.getString(1));
                values.add(1, (String)rs.getString(2));
                values.add(2, (String)rs.getString(3));

                //myLocaDB.insertUser((String)rs.getString(1), (String)rs.getString(2), (int)rs.getInt(3));
                myLocalDB.insertRecord("LiteLocation",fields,types, values);
            }


            //*********************************PROCESS*******************************************
            // re-populate Location.
            rs= stmt.executeQuery(" SELECT ProcessID, ProcessName, ProcessDescription, IsCreation, IsDisposal, IsTransfer, IsAdjustment, IsReturn, Active, LocationID, StatusID, cost FROM  tProcess; ");

            //fields. destroy ?
            //types. destroy ?
            // list of fields, types, and values.
            fields = new ArrayList<String>(Arrays.asList("ProcessID","ProcessName","ProcessDescription","IsCreation","IsDisposal","IsTransfer","IsAdjustment","IsReturn","Active","LocationID","StatusID","cost"));
            types= new ArrayList(Arrays.asList(myLocalDB.TYPE_INT, myLocalDB.TYPE_CHAR, myLocalDB.TYPE_CHAR,myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_INT, myLocalDB.TYPE_DEC));

            while (rs.next())
            {

                Vector values = new Vector();
                values.add(0, (int)rs.getInt(1)); // ProcessID
                values.add(1, (String)rs.getString(2)); //ProcessName
                values.add(2, (String)rs.getString(3)); //ProcessDesc
                values.add(3, (int)rs.getInt(4)); //isCre
                values.add(4, (int)rs.getInt(5)); //isDis
                values.add(5, (int)rs.getInt(6)); //isTran
                values.add(6, (int)rs.getInt(7)); //isAdjustment
                values.add(7, (int)rs.getInt(8)); //isReturn
                values.add(8, (int)rs.getInt(9)); //Active
                values.add(9, (int)rs.getInt(10)); //LocationID
                values.add(10, (int)rs.getInt(11)); //Staus
                values.add(11, (int)rs.getFloat(12)); //Cost


                //myLocaDB.insertUser((String)rs.getString(1), (String)rs.getString(2), (int)rs.getInt(3));
                myLocalDB.insertRecord("LiteProcess",fields,types, values);
            }


           // EditText num = (EditText) findViewById(R.id.displaymessage);
            //num.setText(reset.getString(1));
        }
        catch (SQLException se) {
            Log.e("ERROR", se.getMessage());
        } //catch (ClassNotFoundException e) {
            //Log.e("ERRO", e.getMessage());
         //catch (Exception e) {
            //Log.e("ERRO", e.getMessage());

    }
}
