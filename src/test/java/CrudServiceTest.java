
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.RealSystem;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Taylor on 5/17/16.
 */
public class CrudServiceTest {


    //create a connection to be shared across the entire class
    Connection connection;

    //Create service variable, instantiated before testing
    CrudService crudService;

    @Before
    public void before() throws SQLException{
        //create a TCP server
        //make sure H2 dependency is in pom.xml
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        //establish connection
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/test", "", null);
        //instantiate new service
        crudService = new CrudService(connection);
    }

    /**
     * Given: a new database
     * When: database is initialized
     * Then: we get two tables: User and GroceryItem
     */

    @Test
    public void testInitialization() throws SQLException {
        //arrange
        CrudService service = new CrudService(connection);

        //act
        service.initDatabase();

        //assert
        ResultSet tables = connection.createStatement().executeQuery(
                "SELECT * \n" +
                        "FROM INFORMATION_SCHEMA.TABLES \n" +
                        "WHERE TABLE_SCHEMA = 'PUBLIC'"); //  AND TABLE_NAME in ('User', 'GroceryItem')

        ArrayList<String> tableNames = new ArrayList<>();

        while(tables.next()){
            //get TABLE_NAME strings and add them to tableNames array list
            tableNames.add(tables.getString("TABLE_NAME"));
        }

        assertThat(tableNames, hasItems("USER", "GROCERYITEM"));
    }

    //todo: create a test for insertUser and selectUser

    /**
     * Given: user database
     * When: a new user is inserted
     * Then: then user id is set and user exists in database
     */

    @Test
    public void whenUserInsertedThenUserExists() throws SQLException, InterruptedException {

        //arrange
        CrudService service = new CrudService(connection);
        service.insertUser(connection, "test", "pw");

        //act
        User user = service.selectUser(connection, "test");
        connection.close();
        //assert
        assertThat(user.getName(), is("test"));
        int id = user.getId();

        while (true) {
            Thread.sleep(100);
        }
    }

    /**
     * Given: user database containing users
     * When: a user is selected by entering user's name
     * Then: the user object associated with that user name is returned
     */
/*
    @Test
    public void whenUserSelectedThenUserReturned() throws SQLException {

        //arrange
        CrudService service = new CrudService(connection);
        service.insertUser(connection, "test", "password");

        //make sure that I'm actually testing the returned user and not the instance of
        //user that i created above in this test
        //act
       User newUser = service.selectUser(user, user.name);

        //assert
        assertThat(newUser.getName(), is("testSelect"));

    }
*/
    /**
     * Given: an initialized grocery item database
     * When: a grocery item is inserted
     * Then: grocery item id is set and grocery item exists in database
     */

    @Test
    public void testGroceryItemDatabase() throws SQLException, InterruptedException {

        //arrange
        CrudService service = new CrudService(connection);
        service.insertUser(connection, "test", "pw");
        User testUser = service.selectUser(connection, "test");
        GroceryItem testItem = new GroceryItem(0, "apples", "a lot", testUser.getId());
        //act
        service.insertEntry(connection, testItem.getId(), testItem.getItemName(), testItem.getItemQuantity(), testItem.getUserId());

        GroceryItem returnedItem = service.selectEntry(connection, testItem.getId());

        connection.close();
        //assert
        assertThat(returnedItem.getItemName(), is("apples"));
/*
        while(true) {
            Thread.sleep(100);
        }
        */
    }

    /**
     * Given: a grocery item database with grocery item entries
     * When: a grocery item is selected by entering its name
     * Then: the grocery item associated with that name is returned
     *//*
    @Test
    public void whenGroceryItemSelectedThenGroceryItemReturned() throws SQLException{
        //arrange
        CrudService service = new CrudService(connection);
        GroceryItem groceryItem = new GroceryItem(1, "testItem", "quite a few");
        service.insertEntry(groceryItem);

        //act
        service.selectEntry(groceryItem, 1);

        //assert
        assertThat(groceryItem.getItemName(), is("testItem"));
    }



    //todo: create a test for selectEntries
    *//**
     * Given: two databases with users and grocery items
     * When: selectEntries is run
     * Then: return an array list with all tracked objects
     *//*
*//*
    @Test
    public void whenUsersSelectedThenUsersAndGroceryItemArrayListReturned() throws SQLException{
        //arrange
        CrudService service = new CrudService(connection);
        service.insertEntry(groceryItem);

        GroceryItem groceryItem = new GroceryItem(1, "testItem", "quite a few");

        //act
        ArrayList<GroceryItem> selectList = service.selectEntries(connection);

        //assert

        assertThat(3, is(selectList.size()));

    }

    @After
    public void after() throws SQLException {
        //close the connection
        connection.close();
        /*
        //deletes any database files in ~/Projects/ForumWeb/data
        File dataFolder = new File("data");
        if(dataFolder.exists()) {
            for(File file : dataFolder.listFiles()){
                if(file.getName().startsWith("test.h2")) {
                    file.delete();
                }
            }
        }*/
    }



    //todo: create a test for updateEntry and deleteEntry

