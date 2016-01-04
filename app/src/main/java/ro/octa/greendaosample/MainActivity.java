package ro.octa.greendaosample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import ro.octa.greendaosample.adapters.UserListAdapter;
import ro.octa.greendaosample.commons.model.Gender;
import ro.octa.greendaosample.dao.DBPhoneNumber;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;
import ro.octa.greendaosample.utils.MathUtils;

/**
 * @author Octa
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private ListView list;
    private UserListAdapter adapter;
    private ArrayList<DBUser> userList;
    /**
     * Manages the database for this application..
     */
    private IDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init database manager
        databaseManager = new DatabaseManager(this);

        userList = new ArrayList<DBUser>();
        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DBUser user = (DBUser) list.getItemAtPosition(position);
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                    intent.putExtra("userID", user.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        refreshUserList();

        findViewById(R.id.createUserBtn).setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                refreshUserList();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_users) {
            handleDeleteAllUsers();
            return true;
        } else if (id == R.id.truncate_all_tables) {
            handleTruncateAllTables();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleTruncateAllTables() {
        databaseManager.dropDatabase();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    private void handleDeleteAllUsers() {
        databaseManager.deleteUsers();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    private DBUser createRandomUser() {
        DBUser user = new DBUser();
        user.setEmail(UUID.randomUUID().toString() + "@email.com");
        user.setPassword("defaultPass");
        return user;
    }

    private DBUserDetails createRandomUserDetails() {
        DBUserDetails userDetails = new DBUserDetails();
        userDetails.setBirthDate(new Date());
        userDetails.setRegistrationDate(new Date());
        userDetails.setCountry("World");
        userDetails.setFirstName(UUID.randomUUID().toString());
        userDetails.setLastName(UUID.randomUUID().toString());
        userDetails.setGender("MALE");
        userDetails.setNickName(UUID.randomUUID().toString());

        Random r = new Random();
        int low = 0;
        int high = 1;
        int result = r.nextInt(high - low) + low;
        if (result == 0) {
            userDetails.setGender(Gender.MALE.getGender());
        } else {
            userDetails.setGender(Gender.FEMALE.getGender());
        }


        return userDetails;
    }

    private void onCreateUserClick() {
        // create a random user object
        DBUser user = createRandomUser();

        // insert that user object to our DB
        user = databaseManager.insertUser(user);

        // Create a random userDetails object
        DBUserDetails userDetails = createRandomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUser(user);

        // insert or update this userDetails object to our DB
        userDetails = databaseManager.insertOrUpdateUserDetails(userDetails);

        // link userDetails Key to user
        user.setDetailsId(userDetails.getId());
        user.setDetails(userDetails);
        databaseManager.updateUser(user);

        // create a dynamic list of phone numbers for the above object
        for (int i = 0; i < MathUtils.randInt(1, 7); i++) {
            DBPhoneNumber phoneNumber = new DBPhoneNumber();
            phoneNumber.setPhoneNumber(UUID.randomUUID().toString());
            phoneNumber.setDetailsId(userDetails.getId());

            // insert or update an existing phone number into the database
            databaseManager.insertOrUpdatePhoneNumber(phoneNumber);
        }

        try {
            // add the user object to the list
            userList.add(user);
            adapter.notifyDataSetChanged();
            list.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    list.setSelection(adapter.getCount() - 1);
                }
            });
        } catch (UnsupportedOperationException e) {

        }
    }

    /**
     * Called after your activity has been stopped, prior to it being started again.
     * Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        if (databaseManager == null)
            databaseManager = new DatabaseManager(this);

        super.onRestart();
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity
     * to start interacting with the user.
     */
    @Override
    protected void onResume() {
        // init database manager
        databaseManager = DatabaseManager.getInstance(this);

        super.onResume();
    }

    /**
     * Called when you are no longer visible to the user.
     */
    @Override
    protected void onStop() {
        if (databaseManager != null)
            databaseManager.closeDbConnections();

        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createUserBtn: {
                onCreateUserClick();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Display all the users from the DB into the listView
     */
    private void refreshUserList() {
        userList = DatabaseManager.getInstance(this).listUsers();
        if (userList != null) {
            if (adapter == null) {
                adapter = new UserListAdapter(MainActivity.this, userList);
                list.setAdapter(adapter);
                if (userList.isEmpty())
                    onCreateUserClick();
            } else {
                list.setAdapter(null);
                adapter.clear();
                adapter.addAll(userList);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);
            }
        }
    }
}