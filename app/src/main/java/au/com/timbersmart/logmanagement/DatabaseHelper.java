package au.com.timbersmart.logmanagement;

/**
 * Created by nptn1 on 8/12/2017.
 * To be used later, to load from an existing DB file.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private final Context context;
    private final String assetPath;
    private final String dbPath;

    public DatabaseHelper(Context context, String dbName, String assetPath)
            throws IOException {
        super(context, dbName, null, 1);
        this.context = context;
        this.assetPath = assetPath;
        this.dbPath = "/data/data/"
                + context.getApplicationContext().getPackageName() + "/databases/"
                + dbName;
        checkExists();
    }

    /**
     * Checks if the database asset needs to be copied and if so copies it to the
     * default location.
     *
     * @throws IOException
     */
    private void checkExists() throws IOException {
        Log.i(TAG, "checkExists()");

        File dbFile = new File(dbPath);

        if (!dbFile.exists()) {

            Log.i(TAG, "creating database..");

            dbFile.getParentFile().mkdirs();
            copyStream(context.getAssets().open(assetPath), new FileOutputStream(
                    dbFile));

            Log.i(TAG, assetPath + " has been copied to " + dbFile.getAbsolutePath());
        }

    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte buf[] = new byte[1024];
        int c = 0;
        while (true) {
            c = is.read(buf);
            if (c == -1)
                break;
            os.write(buf, 0, c);
        }
        is.close();
        os.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
