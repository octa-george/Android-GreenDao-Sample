package ro.octa.greendaosample.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import ro.octa.greendaosample.dao.DBPhoneNumber;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDao;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.dao.DBUserDetailsDao;
import ro.octa.greendaosample.dao.DaoMaster;
import ro.octa.greendaosample.dao.DaoSession;

/**
 * @author Octa
 */
public class DatabaseManager implements IDatabaseManager, AsyncOperationListener {

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
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, "sample-database", null);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
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

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        asyncSession.waitForCompletion(1000);
        asyncSession.isCompleted();
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
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
    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            DaoMaster.dropAllTables(database, true); // drops all tables
            mHelper.onCreate(database);              // creates the tables
            asyncSession.deleteAll(DBUser.class);    // clear all elements from a table
            asyncSession.deleteAll(DBUserDetails.class);
            asyncSession.deleteAll(DBPhoneNumber.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized DBUser insertUser(DBUser user) {
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
        return user;
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
                daoSession.update(user);
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
    public synchronized boolean deleteUserById(Long userId) {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized DBUser getUserById(Long userId) {
        DBUser user = null;
        try {
            openReadableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            user = userDao.loadDeep(userId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
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
    public synchronized DBUserDetails insertOrUpdateUserDetails(DBUserDetails userDetails) {
        try {
            if (userDetails != null) {
                openWritableDb();
                daoSession.insertOrReplace(userDetails);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }

    @Override
    public synchronized void deleteUserByFirstNameAndGender(String firstName, String gender) {
        try {
            openWritableDb();
            DBUserDetailsDao dao = daoSession.getDBUserDetailsDao();
            WhereCondition condition = dao.queryBuilder().and(DBUserDetailsDao.Properties.FirstName.eq(firstName),
                    DBUserDetailsDao.Properties.Gender.eq(gender));
            QueryBuilder<DBUserDetails> queryBuilder = dao.queryBuilder().where(condition);
            dao.deleteInTx(queryBuilder.list());
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void insertOrUpdatePhoneNumber(DBPhoneNumber phoneNumber) {
        try {
            if (phoneNumber != null) {
                openWritableDb();
                daoSession.insertOrReplace(phoneNumber);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void bulkInsertPhoneNumbers(Set<DBPhoneNumber> phoneNumbers) {
        try {
            if (phoneNumbers != null && phoneNumbers.size() > 0) {
                openWritableDb();
                asyncSession.insertOrReplaceInTx(DBPhoneNumber.class, phoneNumbers);
                assertWaitForCompletion1Sec();
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
