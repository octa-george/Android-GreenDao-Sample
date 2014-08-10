package ro.octa.greendaosample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ro.octa.greendaosample.adapters.UserListAdapter;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;

/**
 * @author Octa
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private ListView list;
    private UserListAdapter adapter;
    private List<DBUser> userList;
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

        refreshUserList();

        findViewById(R.id.createUserBtn).setOnClickListener(this);
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

                // create a random user object
                DBUser user = new DBUser();
                user.setEmail(UUID.randomUUID().toString() + "@email.com");
                user.setPassword("defaultPass");

                // insert that object to our DB
                databaseManager.insertUser(user);

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
        userList = databaseManager.listUsers();
        if (adapter == null) {
            adapter = new UserListAdapter(MainActivity.this, userList);
            list.setAdapter(adapter);
        } else {
            list.setAdapter(null);
            adapter.clear();
            adapter.addAll(userList);
            adapter.notifyDataSetChanged();
            list.setAdapter(adapter);
        }
    }
}
