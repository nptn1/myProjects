package au.com.timbersmart.logmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    LocalDB myLocalDB;
    RemoteDB myRemoteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setIcon(R.drawable.tslogo);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //myLocalDB = ((LogManagement)getApplicationContext()).getLocalDB();
        //getApplicationContext();

        // create/load the sqlLite BD
        myLocalDB = ((LogManagement)getApplicationContext()).getLocalDB();
        myLocalDB.localDBConnect(this);

        // connect to the Remote DB.
        myRemoteDB = ((LogManagement)getApplicationContext()).getRemoteDB();
        myRemoteDB.remoteDBConnect(this);

        // execute the Sync
        myRemoteDB.syncRemoteToDlocal(myLocalDB);


        // insert a user for testing.
        //myLocalDB.insertUser("nhan","nhan",1);

        Spinner dynamicSpinner = (Spinner) findViewById(R.id.UserName_spinner);

       // String[] items = new String[] { "Chai Latte", "Green Tea", "Black Tea" };
        List<String> userNames = myLocalDB.getUserNames();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, userNames);

        dynamicSpinner.setAdapter(adapter);
        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //Log.v("item", (String) parent.getItemAtPosition(position));
                Toast.makeText(parent.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.DBSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void butLogin(View view){

        //TextView UserName = (TextView)findViewById(R.id.Username);
        Spinner UserName = (Spinner)findViewById(R.id.UserName_spinner);
        TextView Password = (TextView)findViewById(R.id.Password);

        findViewById(R.id.UserName_spinner);

        if (myLocalDB.ValidateUser(UserName.toString(), Password.toString()) == true)
        {
            //go to next screen

        }
        else{
            Toast.makeText(this, "Authentication Error: Please check your UserName and Password", Toast.LENGTH_LONG).show();
        }


    }
}
