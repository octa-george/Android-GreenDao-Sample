package ro.octa.greendaosample.manager;

import java.util.List;

import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;

/**
 * Interface that provides methods for managing the database inside the Application.
 *
 * @author Octa
 */
public interface IDatabaseManager {

    /**
     * Closing available connections
     */
    public void closeDbConnections();

    /**
     * Insert a user into the DB
     *
     * @param user to be inserted
     */
    public void insertUser(DBUser user);

    /**
     * List all the users from the DB
     *
     * @return list of users
     */
    public List<DBUser> listUsers();

    /**
     * Update a user from the DB
     *
     * @param user to be updated
     */
    void updateUser(DBUser user);

    /**
     * Delete all users with a certain email from the DB
     *
     * @param email of users to be deleted
     */
    public void deleteUserByEmail(String email);

    /**
     * Delete all the users from the DB
     */
    public void deleteUsers();

    /**
     * Insert or update a userDetails object into the DB
     *
     * @param userDetails to be inserted/updated
     */
    public void insertOrUpdateUserDetails(DBUserDetails userDetails);

}
