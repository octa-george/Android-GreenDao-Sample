package ro.octa.greendaosample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ro.octa.greendaosample.dao.DBPhoneNumber;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;

/**
 * @author Octa
 */
public class UserDetailsActivity extends Activity {

    private TextView detailsHeader;
    private TextView userEmail;
    private TextView userFirstName;
    private TextView userLastName;
    private TextView userNickName;
    private TextView userBirthDate;
    private TextView userGender;
    private TextView userCountry;
    private Spinner userPhoneNumbers;
    private Button deleteUser;
    private DBUser user;
    private IDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        init();
        setupDefaults();
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

    private void init() {
        // init database manager
        databaseManager = new DatabaseManager(this);

        long userId = getIntent().getLongExtra("userID", -1L);
        if (userId != -1) {
            user = databaseManager.getUserById(userId);
        }

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        detailsHeader = (TextView) findViewById(R.id.details_header);
        userEmail = (TextView) findViewById(R.id.email_label);
        userFirstName = (TextView) findViewById(R.id.first_name_label);
        userLastName = (TextView) findViewById(R.id.last_name_label);
        userNickName = (TextView) findViewById(R.id.nickname_label);
        userBirthDate = (TextView) findViewById(R.id.birthday_label);
        userGender = (TextView) findViewById(R.id.gender_label);
        userCountry = (TextView) findViewById(R.id.country_label);
        userPhoneNumbers = (Spinner) findViewById(R.id.phone_numbers_spinner);
        deleteUser = (Button) findViewById(R.id.delete_user);
    }

    private void setupDefaults() {
        if (user != null) {
            detailsHeader.setText(String.format(getResources().getString(R.string.details_for_user), user.getId()));
            userEmail.setText(user.getEmail());
            DBUserDetails userDetails = user.getDetails();
            if (userDetails != null) {
                userFirstName.setText(userDetails.getFirstName());
                userLastName.setText(userDetails.getLastName());
                userGender.setText(userDetails.getGender());
                userCountry.setText(userDetails.getCountry());
                userNickName.setText(userDetails.getNickName());
                userBirthDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(userDetails.getBirthDate()));
                List<DBPhoneNumber> phoneNumbers = userDetails.getPhoneNumbers();
                if (phoneNumbers != null) {
                    List<String> list = new ArrayList<String>();
                    for (DBPhoneNumber phone : phoneNumbers) {
                        list.add(phone.getPhoneNumber());
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userPhoneNumbers.setAdapter(dataAdapter);
                }
            }
            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long userId = user.getId();
                    boolean status = DatabaseManager.getInstance(UserDetailsActivity.this).deleteUserById(userId);
                    if (status) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        Log.i(UserDetailsActivity.class.getSimpleName(), "User " + userId + " was successfully deleted from the schema!");
                        finish();
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "ops... something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
