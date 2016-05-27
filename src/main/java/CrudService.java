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
        statement.execute("INSERT INTO user VALUES (NULL, 'a', 'p')");
        //statement.execute("INSERT INTO groceryItem VALUES (NULL, 'apples', 'quite a few', 1)");
    }

    //todo: create insertUser method
    //this will create a new record in the users table
    public void insertUser(Connection connection, String name, String password) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (NULL, ?, ?)");

        //set strings into statement by getting name and password
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);

        preparedStatement.execute();

        //getGeneratedKeys() will return the generated id
        //ResultSet resultSet = preparedStatement.getGeneratedKeys();
        //resultSet.next(); //read the first line of results
        //set the generated id into user
        //user.setId(resultSet.getInt(1));
    }


    //todo: create a selectUser method
    //this will return a User object for the given username
    public User selectUser(Connection connection, String name) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name =?");

        preparedStatement.setString(1, name);

        ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                return new User(id, name, password);
            }
        return null;
    }


    //todo: create insertEntry method
    //this will create a new record for thing I'm tracking
    public void insertEntry(Connection connection, int itemId, String itemName, String itemQuantity, int userId) throws SQLException {
        //create prepared statement
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO groceryItem VALUES (NULL, ?, ?, ?)");

        //set strings into statement by getting itemName and itemQuantity
        preparedStatement.setString(1, itemName);
        preparedStatement.setString(2, itemQuantity);
        preparedStatement.setInt(3, userId);

        preparedStatement.executeUpdate();


    //getGeneratedKeys() will return the generated id
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next(); //read the first line of results
    //set the generated id into groceryItem
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setId(resultSet.getInt(1));
        groceryItem.setItemName(itemName);
        groceryItem.setItemQuantity(itemQuantity);
    }


    //todo: create selectEntry method
    //this will return a new object for the given ID
    public GroceryItem selectEntry(Connection connection, int selectId) throws SQLException {
        //create prepared statement
        //it turns out i don't need an inner join for this query because i don't have a User propery in my GroceryItem class
        //inner join would be necessary if i needed to pull out the User object that is intrinsically part of the GroceryItem class
        //but since I have GroceryItem with just a foreign key pointing to the Id of the User (userId), I just need to pull out the GroceryItem related to the Id of the GroceryItem that is queried
        //However, it is preferred when working with Java to make the User object an inherent property of the GroceryItem class because that is the point of object-oriented programming
        //to have the objects relate directly to each other instead of pointing to each other indirectly
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM groceryItem WHERE groceryItem.id = ?");

        preparedStatement.setInt(1, selectId);

        //resultSet is returned null because User is not even being
        ResultSet resultSet = preparedStatement.executeQuery();

        GroceryItem groceryItem = null;

        if (resultSet.next()) {
            int itemId = resultSet.getInt("id");
            String itemName = resultSet.getString("itemName");
            String itemQuantity = resultSet.getString("itemQuantity");
            int userId = resultSet.getInt("userId");
            return new GroceryItem(itemId, itemName, itemQuantity, userId);
        }
        return null;
    }



    //todo: this query should use an INNER JOIN between users and entries table

    //todo: create test for insertEntry and selectEntry

    //todo: create a selectEntries method
    //this will return an Arraylist of all objects I am tracking
    public ArrayList<GroceryItem> selectEntries(Connection connection, int id) throws SQLException {
        ArrayList<GroceryItem> userGroceryList = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM groceryItem INNER JOIN user ON groceryItem.userId = user.id WHERE user.id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int itemId = resultSet.getInt("id");
            String itemName = resultSet.getString("itemName");
            String itemQuantity = resultSet.getString("itemQuantity");
            int userId = resultSet.getInt("userId");
            GroceryItem groceryItem = new GroceryItem(itemId, itemName, itemQuantity, userId);
            userGroceryList.add(groceryItem);
        }
    return userGroceryList;
        //todo: this query should use an INNER JOIN between users and entries table

    }



    //todo: create a test for selectEntries

    //todo: create updateEntry method
    //this will update all the values for a given ID

    //todo: create deleteEntry method
    //this will delete an entry with the given ID

    //todo: create a test for updateEntry and deleteEntry

}
