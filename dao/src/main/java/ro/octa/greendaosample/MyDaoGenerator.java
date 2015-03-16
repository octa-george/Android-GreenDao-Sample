package ro.octa.greendaosample;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * @author Octa
 */
public class MyDaoGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir").replace("\\", "/");
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ro.octa.greendaosample.dao");

        addTables(schema);

        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    /**
     * Create tables and the relationships between them
     */
    private static void addTables(Schema schema) {
        /* entities */
        Entity user = addUser(schema);
        Entity userDetails = addUserDetails(schema);
        Entity phoneNumber = addPhoneNumber(schema);

        /* properties */
        Property userIdForUserDetails = userDetails.addLongProperty("userId").notNull().getProperty();
        Property userDetailsIdForUser = user.addLongProperty("detailsId").notNull().getProperty();
        Property userDetailsIdForPhoneNumber = phoneNumber.addLongProperty("detailsId").notNull().getProperty();

        /* relationships between entities */
        userDetails.addToOne(user, userIdForUserDetails, "user");    // one-to-one (user.getDetails)
        user.addToOne(userDetails, userDetailsIdForUser, "details"); // one-to-one (user.getUser)

        ToMany userDetailsToPhoneNumbers = userDetails.addToMany(phoneNumber, userDetailsIdForPhoneNumber);
        userDetailsToPhoneNumbers.setName("phoneNumbers"); // one-to-many (userDetails.getListOfPhoneNumbers)

    }

    /**
     * Create user's Properties
     *
     * @return DBUser entity
     */
    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("DBUser");
        user.addIdProperty().primaryKey().autoincrement();
        user.addStringProperty("email").notNull().unique();
        user.addStringProperty("password").notNull();
        return user;
    }

    /**
     * Create user details Properties
     *
     * @return DBUserDetails entity
     */
    private static Entity addUserDetails(Schema schema) {
        Entity userDetails = schema.addEntity("DBUserDetails");
        userDetails.addIdProperty().primaryKey().autoincrement();
        //  userDetails.addLongProperty("id").notNull().unique().primaryKey().autoincrement();
        userDetails.addStringProperty("nickName").notNull();
        userDetails.addStringProperty("firstName").notNull();
        userDetails.addStringProperty("lastName").notNull();
        userDetails.addDateProperty("birthDate");
        userDetails.addStringProperty("gender");
        userDetails.addStringProperty("country");
        userDetails.addDateProperty("registrationDate").notNull();
        return userDetails;
    }

    /**
     * Create phone numbers Properties
     *
     * @return DBPhoneNumber entity
     */
    private static Entity addPhoneNumber(Schema schema) {
        Entity phone = schema.addEntity("DBPhoneNumber");
        phone.addIdProperty().primaryKey().autoincrement();
        phone.addStringProperty("phoneNumber").notNull().unique();
        return phone;
    }

}
