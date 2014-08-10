package ro.octa.greendaosample.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDao;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.dao.DBUserDetailsDao;
import ro.octa.greendaosample.dao.DaoMaster;
import ro.octa.greendaosample.dao.DaoSession;

/**
 * @author Octa
 */
public class DatabaseManager implements IDatabaseManager {

    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    /**
     * Instance of DatabaseManager
     */
    private static DatabaseManager instance;
    /**
     * The Android Activity reference for access to DatabaseManager.
     */
    private Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, "sample-database", null);
    }

    /**
     * @param context The Android {@link android.content.Context}.
     * @return this.instance
     */
    public static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    @Override
    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    @Override
    public synchronized void insertUser(DBUser user) {
        try {
            if (user != null) {
                openWritableDb();
                DBUserDao userDao = daoSession.getDBUserDao();
                userDao.insert(user);

                Log.d(TAG, "Inserted user: " + user.getEmail() + " to the schema.");

                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<DBUser> listUsers() {
        List<DBUser> users = null;
        try {
            openReadableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            users = userDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public synchronized void updateUser(DBUser user) {
        try {
            if (user != null) {
                openWritableDb();
                DBUserDao userDao = daoSession.getDBUserDao();
                userDao.update(user);

                Log.d(TAG, "Updated user: " + user.getEmail() + " from the schema.");

                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteUserByEmail(String email) {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            QueryBuilder<DBUser> queryBuilder = userDao.queryBuilder().where(DBUserDao.Properties.Email.eq(email));
            List<DBUser> userToDelete = queryBuilder.list();
            for (DBUser user : userToDelete) {
                userDao.delete(user);
            }
            daoSession.clear();
            Log.d(TAG, userToDelete.size() + " entry. " + "Deleted user: " + email + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteUsers() {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            userDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void insertOrUpdateUserDetails(DBUserDetails userDetails) {
        try {
            if (userDetails != null) {
                openWritableDb();
                DBUserDetailsDao userDetailsDao = daoSession.getDBUserDetailsDao();
                if (userDetailsDao.load(userDetails.getId()) == null) {
                    Log.d(TAG, "Inserted user details to the schema.");
                    userDetailsDao.insert(userDetails);
                } else {
                    Log.d(TAG, "Updated user details from the schema.");
                    userDetailsDao.update(userDetails);
                }
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
