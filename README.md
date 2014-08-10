greenDao Sample
============================

Recently I evaluated an Android [Object Relational Mapping] (ORM) called [greenDAO] which simplifies your Android development by generating your [Data Access Object] (DAO) layer and your [Domain Objects] based on a simple Java schema you define.

Use of an ORM also supports the [Domain Model Pattern] which:

* Improves software quality and maintainability.
* Is fundamental to the [Domain Driven Design] approach.

###The Tutorial###
Lets walk through getting the [greenDAO] tutorial up and running and I shall explain as we go:

1. Checkout the project in Android studio.
2. Inside you will notice two modules: 'app' and 'dao'.
3. If you browse to MyDaoGenerator within the IDE you will observe this is the example file which defines the schema (Generated into your DAO and Domain Object Java code when run).
4. The main method constructs a Schema object and defines where the generated source will be deployed.

```sh
public static void main(String args[]) throws Exception {
        // Defines the Schema object and the package it will be exported to
        Schema schema = new Schema(1, "ro.octa.greendaosample.dao");
        // Call the addTables method which appends our Objects definition to the schema
        addTables(schema);
        // Generate the DAO and domain objects based on the schema to the target location
        new DaoGenerator().generateAll(schema, OUT_DIR);
    }
```
  

The *addTables* method further refine the schema by defining some domain objects and their relationships. The definition of the schema is where your domain modelling is realized.


**NB:** Ideally you will have completed a domain design and modelling process using UML which can now be realised via greenDAO schema definition.

```sh
    /**
     * Create tables and the relationships between them
     */
    private static void addTables(Schema schema) {
        /* entities */
        Entity user = addUser(schema);
        Entity userDetails = addUserDetails(schema);

        /* properties */
        Property userIdForUserDetails = userDetails.addLongProperty("userId")
                .notNull().getProperty();
        Property userDetailsIdForUser = user.addLongProperty("detailsId")
                .notNull().getProperty();

        /* relationships between entities */
        userDetails.addToOne(user, userIdForUserDetails, "user");
        user.addToOne(userDetails, userDetailsIdForUser, "details");

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
    
    ...
```

Review the code in the *MyDaoGenerator* until you feel you understand the role of this class and the schema definition.

When you wish to generate your DAO and Domain Objects Run *MyDaoGenerator* as a Java Application via your IDE.

Now that you have generated your DAO and Domain Objects, browse to *app\src\main\java\ro\octa\greendaosample\dao* to review the generated source.

Browse to *app\src\main\java\ro\octa\greendaosample\MainActivity.java*. This is the Android Activity which will be shown when your application is running. Take some time to look at the code in this class and you will identify the wiring for the UI elements which leverage the Database via greenDAO to persist the users.

If your IDE is correctly configured you will be able to right click on your *app* project and select Run as > Android Application. I recommend playing with the application either on a connected device or using the emulator. You should add a few users and verify they are persisted and loaded correctly.

As you can see the greenDAO tutorial gives you a pretty good understanding of the steps required to implement the schema and generate your DAO and Domain Objects. The team from greenDAO deserve recognition for how easy to use they have made this ORM and how well documented it is.



###How do I use the DAO for CRUD operations?###

The first step to accessing the DAO and the associated CreateReadUpdateDelete([CRUD]) operations is to construct an SQLiteOpenHelper. Typically you will use the generated DevOpenHelper for development and your own custom written OpenHelper for production. Your OpenHelper will handle applying schema updates. I am currently in the process of writing a blog post on performing a schema update and data-migration with greenDAO, so stay tuned.
@see DatabaseManager.java

```sh
    /**
     * Constructs a new DatabaseManager with the specified arguments.
     */
    public DatabaseManager() {
        mHelper = new DaoMaster.DevOpenHelper(this.context, "sample-database", null);
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
```

Now that we have the DAO reference we can perform CRUD operations for our domain objects.

```sh
    // [C]reate
    User user = new User(); 
    userDao.insert(user); 
    System.out.println("Inserted: " + user.getEmail());
    
    // [R]ead
    userDao.load(1); 
    userDao.loadAll();
    
    // [U]pdate 
    user.setEmail("newEmail@me.com"); 
    userDao.update(user);
    
    // [D]elete 
    userDao.delete(user); 
    userDao.deleteAll();
```


###What are the transaction options available and why do I need them?###

It is important once you start using the ORM commercially that you understand the implications of transactions. Sometimes you wish to perform a chunk of work as a single atomic block, grouping a number of actions together so that they either succeed (and are committed) or fail (and are rolled back) together. Transactions allow the Database to ensure referential integrity is maintained given a sometimes chaotic operating environment.

greenDAO uses transactions under the hood for every call (e.g. insert or delete) on a DAO, however it also exposes abstractions to allow you to manage the transactions. Without getting into too much detail, here are some situations in which you would take control of the transactions: 
* If you need to commit a change which occurs across multiple tables then a transaction can be used to ensures they occur according to the ACID principles
* If you have a large number of operations and you wish to perform them efficiently, then you should explicitly group them into a single transaction. This will free greenDAO from the transaction overhead of a transaction per call (making it significantly faster to execute them).

```sh
    daoSession.runInTx(new Runnable() {
        public void run() {
            // Everything in run will be executed in a single transaction.
            ....
        }
    });
```

###QueryBuilder and querying your data###

The QueryBuilder lets you create powerful queries without having to write raw SQL. The QueryBuilder is a fluent interface, which makes your code very easy to read and maintain. QueryBuilder also ensures compile time checking (as opposed to SQL which will fail at runtime as it is an String).

```sh
// The following code will query the DAO for all users which have the same email
List<DBUser> userToDelete = userDao.queryBuilder().where(DBUserDao.Properties.Email.eq(email)).list();
```

###Where can I find help?###
greenDAO uses [Stack Overflow] for support, if you have any questions which are not answered by the documentation then SO is your best bet

License
----



**Free Software, Hell Yeah!**

[Domain Driven Design]:http://en.wikipedia.org/wiki/Domain-driven_design
[Domain Model Pattern]:http://martinfowler.com/eaaCatalog/domainModel.html
[Domain Objects]:http://en.wikipedia.org/wiki/Domain_object
[Data Access Object]:http://en.wikipedia.org/wiki/Data_access_object
[Object Relational Mapping]:http://en.wikipedia.org/wiki/Object-relational_mapping
[greenDao]:http://greendao-orm.com/
[CRUD]:http://en.wikipedia.org/wiki/Create,_read,_update_and_delete
[Stack Overflow]:http://stackoverflow.com/

