import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Taylor on 5/17/16.
 */
public class CrudService {

    /*
    SELECT * FROM groceryItem
  INNER JOIN users
  ON groceryitem.user_id
  WHERE groceryitem.user_id

INSERT INTO groceryItem VALUES (NULL, ?, ?, ?)

INSERT INTO user VALUES (NULL, ?, ?)

CREATE TABLE IF NOT EXISTS user
(
  id IDENTITY,
  name VARCHAR,
  password VARCHAR
)
CREATE TABLE IF NOT EXISTS groceryItem
(
  id IDENTITY,
  itemName VARCHAR,
  itemQuantity VARCHAR
  userId INT
)
     */


    //create a connection that can't be changed
   private final Connection connection;

    //constructor that uses Connection as an argument so that we can use CrudServiceTest's connection here
    public CrudService(Connection connection) {
        this.connection = connection;
    }



    //todo: create initDatabase method
    public void initDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS user (id IDENTITY, name VARCHAR, password VARCHAR)");
        statement.execute("CREATE TABLE IF NOT EXISTS groceryItem (id IDENTITY, itemName VARCHAR, itemQuantity VARCHAR,  userId INT)");
        statement.execute("INSERT INTO user VALUES (NULL, 'andrew', 'password')");
        statement.execute("INSERT INTO groceryItem VALUES (NULL, 'apples', 'quite a few', 0)");
    }

    //todo: create insertUser method
    //this will create a new record in the users table
    public void insertUser(User user) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (NULL, ?, ?)");

        //set strings into statement by getting name and password
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPassword());

        preparedStatement.executeUpdate();

        //getGeneratedKeys() will return the generated id
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next(); //read the first line of results
        //set the generated id into user
        user.setId(resultSet.getInt(1));
    }


    //todo: create a selectUser method
    //this will return a User object for the given username
    public User selectUser(User user, String name) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name =?");

        preparedStatement.setString(1, name);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            if (user.name == name) {
                User selectedUser = new User(
                        resultSet.getString("name"),
                        resultSet.getString("password")
                );
                return selectedUser;
            }
        }
        return null;
    }

    //todo: create insertEntry method
    //this will create a new record for thing I'm tracking
    public void insertEntry(GroceryItem groceryItem, User user) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO groceryItem VALUES (NULL, ?, ?, ?)");

        //set strings into statement by getting itemName and itemQuantity
        preparedStatement.setString(1,groceryItem.getItemName());
        preparedStatement.setString(2,groceryItem.getItemQuantity());
        preparedStatement.setInt(3, user.getId());

        preparedStatement.executeUpdate();

    //getGeneratedKeys() will return the generated id
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next(); //read the first line of results
    //set the generated id into groceryItem
        groceryItem.setId(resultSet.getInt(1));
    }

/*
    //todo: create selectEntry method
    //this will return a new object for the given ID
    public GroceryItem selectEntry(GroceryItem groceryItem, int id) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM groceryItem INNER JOIN users ON user.id = groceryItem.user_id WHERE id =?");

        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            if (groceryItem.id == id) {
                GroceryItem selectedGroceryItem = new GroceryItem(
                        resultSet.getInt("id"),
                        resultSet.getString("itemName"),
                        resultSet.getString("password")
                );
                return selectedGroceryItem;
            }
        }
        return null;
    }
*/

    //todo: this query should use an INNER JOIN between users and entries table

    //todo: create test for insertEntry and selectEntry

    //todo: create a selectEntries method
    //this will return an Arraylist of all objects I am tracking
/*
    public ArrayList<GroceryItem> selectEntries(Connection connection) throws SQLException {
        ArrayList<GroceryItem> userGroceryList = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM groceryItem INNER JOIN users ON groceryitem.userId WHERE user.id");

        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            int itemId = resultSet.getInt("id");
            String itemName = resultSet.getString("itemName");
            String itemQuantity = resultSet.getString("itemQuantity");
            GroceryItem groceryItem = new GroceryItem(itemId, itemName, itemQuantity);
            usersGroceryList.add(groceryItem);

        } return usersGroceryList;
        //todo: this query should use an INNER JOIN between users and entries table

    }

*/

    //todo: create a test for selectEntries

    //todo: create updateEntry method
    //this will update all the values for a given ID

    //todo: create deleteEntry method
    //this will delete an entry with the given ID

    //todo: create a test for updateEntry and deleteEntry

}
