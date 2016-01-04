package ro.octa.greendaosample.manager;

import java.util.ArrayList;
import java.util.Set;

import ro.octa.greendaosample.dao.DBPhoneNumber;
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
    void closeDbConnections();

    /**
     * Delete all tables and content from our database
     */
    void dropDatabase();

    /**
     * Insert a user into the DB
     *
     * @param user to be inserted
     */
    DBUser insertUser(DBUser user);

    /**
     * List all the users from the DB
     *
     * @return list of users
     */
    ArrayList<DBUser> listUsers();

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
    void deleteUserByEmail(String email);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param userId of users to be deleted
     */
    boolean deleteUserById(Long userId);

    /**
     * @param userId - of the user we want to fetch
     * @return Return a user by its id
     */
    DBUser getUserById(Long userId);

    /**
     * Delete all the users from the DB
     */
    void deleteUsers();

    /**
     * Insert or update a userDetails object into the DB
     *
     * @param userDetails to be inserted/updated
     */
    DBUserDetails insertOrUpdateUserDetails(DBUserDetails userDetails);

    /**
     * Delete a user by name and gender
     */
    void deleteUserByFirstNameAndGender(String firstName, String gender);

    /**
     * Insert or update a phoneNumber object into the DB
     *
     * @param phoneNumber to be inserted/updated
     */
    void insertOrUpdatePhoneNumber(DBPhoneNumber phoneNumber);

    /**
     * Insert or update a list of phoneNumbers into the DB
     *
     * @param phoneNumbers - list of objects
     */
    void bulkInsertPhoneNumbers(Set<DBPhoneNumber> phoneNumbers);


}
