package au.com.timbersmart.logmanagement;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;




public class LocalDB extends Application
{
    final int REQUEST_WRITE_EXTERNAL_STORAGE=1;

    public static final int TYPE_CHAR = 0;
    public static final int TYPE_INT = 1;
    public static final int TYPE_DEC = 2;

    public static LocalDB localDB=new LocalDB() ;
    SQLiteDatabase myDB;

    //@Override
    //public void onCreate() {
    //    localDB = (LocalDB)getApplicationContext();
    //}
    //public LocalDB getLocalDB()
    //{
    //    return localDB;
    //}

    LocalDB(){

    }

    public LocalDB getLocalDB(){
        return localDB;
    }

    public void localDBConnect (Context ctx)
    {
        String DBPath;
        InputStream x;
        //Toast.makeText(this.getBaseContext(), "my button clicked", Toast.LENGTH_SHORT).show();



        //Context context = this.getBaseContext();
        //DBPath = context.getDatabasePath("StocktakeSQLLite.db").getPath();

        //x = context.getAssets().open("StocktakeSQLLite.db");
        //DBPath = "/data/data/au.com.timbersmart.exercise/assets/StocktakeSQLLite.db";

        //SQLiteDatabase db = SQLiteDatabase.openDatabase(DBPath, null, 0);

        myDB = ctx.openOrCreateDatabase("TSLogManagemt",MODE_PRIVATE,null);

        myDB.execSQL("CREATE TABLE IF NOT EXISTS LiteUser (UserName VARCHAR(25),UserID INT(5), Password VARCHAR(10) )");

        myDB.execSQL("CREATE TABLE IF NOT EXISTS LiteLocation (LocationID INT(5), LocationName VARCHAR(20), LocationDescription VARCHAR(50) ) ");

        myDB.execSQL("CREATE TABLE IF NOT EXISTS LiteProcess (ProcessID INT(9), ProcessName VARCHAR(20), ProcessDescription VARCHAR(50), IsCreation INT(1), IsDisposal INT(1), IsTransfer INT(1), IsAdjustment INT(1), IsReturn INT(1), Active INT(1), LocationID INT(5), StatusID INT(2), cost DECIMAL(9,2) ) ");

//        myDB.execSQL("CREATE TABLE IF NOT EXISTS StocktakeLite (StocktakeID INT(5),Description VARCHAR(250), BeginDate DATE, Criteria VARCHAR(500) )");

//        myDB.execSQL("CREATE TABLE IF NOT EXISTS StocktakeRowLite (StocktakeRowID INT(5),LocationID INT(5),LocationName VARCHAR(50),RowName VARCHAR(15), Count INT(5), Note VARCHAR(500), StocktakeID INT(5), PreCount INT(5))");

//        myDB.execSQL("CREATE TABLE IF NOT EXISTS StocktakePackLite (PackNo VARCHAR(50), StocktakeID INT(5), LocationID INT(5), StocktakeRowID INT(5), Match INT(1), PackGUID VARCHAR(16))");

        return ;
    }


    // Validate the User against LocalDB.
    public boolean ValidateUser(String UserName, String Password)
    {
        try {

            Cursor c = myDB.rawQuery("SELECT UserName, UserID, Password FROM LiteUser WHERE UserName = '" + UserName + "'", null);
            int UserNameIndex = c.getColumnIndex("UserName");
            int UserIDIndex = c.getColumnIndex("UserID");
            int PasswordIndex = c.getColumnIndex("Password");

            c.moveToFirst();

            while (c != null) {

                if (c.getString(PasswordIndex) == Password) {
                    return true;
                }

                c.moveToNext();
            }
        }
        catch( Exception ex)
        {
            //Log.i("Error -" + ex.st);
            ex.printStackTrace();
        }
        return false;
    }

    void requestPermission( Activity ctx)
    {
        try {
            File download_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //File download_folder = Environment.getDataDirectory();

            //File data = Environment.getDataDirectory();

            int hasWriteStoragePermission = ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ctx, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);


                return;
            }


            //if (download_folder.canWrite()) {
            String currentDBPath = myDB.getPath();//context.getDatabasePath("Stocktake.db").toString();
            //String backupDBPath = Environment.get;
            File currentDB = new File("", currentDBPath);
            //File backupDB = new File(download_folder, backupDBPath);

            File backupDB = new File("",download_folder.getPath()+"/StocktakeWork.db");

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            //}



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getUserNames(){
        String SQLcmd = "SELECT UserName FROM LiteUser;" ;
        List<String> UserNames = new ArrayList<>();

        try {
            Cursor c = myDB.rawQuery(SQLcmd, null);
            int UserIndex= c.getColumnIndex("UserName");

            c.moveToFirst();

            while ( c != null){
                UserNames.add(c.getString(UserIndex)) ;
                c.moveToNext();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return UserNames;
    }

    //************************
    // SYncing PURPOSES ONLY
    public void insertUser(String UserName, String Password, int UserID)
    {
        String SQLcmd = "INSERT INTO LiteUser (UserName,Password,UserID) values ('" + UserName + "','" + Password + "'," + UserID + ");" ;
        try {
            myDB.execSQL(SQLcmd);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*public void deleteAllUsers()
    {
        String SQLcmd = "DELETE LiteUser;";
        try{
            myDB.execSQL(SQLcmd);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }*/

    //************************
    // SYncing PURPOSES ONLY
    //public void insertLocation(int locationID, String LocationName, String LocationDescription)
    public int insertRecord(String tableName, ArrayList<String> fields, ArrayList types,  Vector values)
    {

        // build the sql statement.
        String SQLcmd = "INSERT INTO " + tableName + " ";
        // build fieldList to ( field1, field2, field3 ...)
        String fieldList = "";
        // build valueList to ' values (value1, value2, "value3" ...'
        String valueList = " values (";
        try {
            int i=0;
            while (fields.get(i) != null){

                fieldList += fields.get(i);
                if ((int)types.get(i) == TYPE_CHAR) // add quotes for char, varchars etc.
                    valueList += "'" + values.get(i) + "'";
                else
                    valueList += values.get(i) ;

                // add the comma at the end of strings or terminate with ).
                if (fields.get(i+1) != null){
                    fieldList += ",";
                    valueList += ",";
                }
                else{
                    fieldList += ")";
                    valueList += ")";
                }
                i++;
            }

            SQLcmd += fieldList;
            SQLcmd += valueList;


            myDB.execSQL(SQLcmd); // execute the statement.
        }
        catch(Exception e){
            e.printStackTrace();
            return 1; // failure
        }
        return 0; // success.
    }

    public void deleteAllRecords(String tableName)
    {
        String SQLcmd = "DELETE " + tableName + ";";
        try{
            myDB.execSQL(SQLcmd);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}



