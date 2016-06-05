import org.h2.tools.Server;
import org.omg.CORBA.ShortSeqHelper;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;

/**
 * Created by doug on 5/8/16.
 * Started by Taylor on 5/113/16.
 */
public class Main {


    //TODO: REMOVE GLOBAL ARRAYLIST AND HASHMAP THAT STORE USERS AND ENTRIES
    //TODO: THE NEW METHODS IN CrudService WILL TAKE OVER THESE FUNCTIONS
    // I'm providing this as a pseudo-database of users.
    // See addUsers below for the set of users who can login
    //static HashMap<String, User> users = new HashMap<>();

    // create a HashMap that maps usernames to ArrayLists containing GroceryItem objects. (HashMap<String, ArrayList<GroceryItem>>)
    //static HashMap<String, ArrayList<GroceryItem>> groceryMap = new HashMap<>();

    //TRM: added this because comment on line 136 in /add-grocery-item route threw me for a loop when trying to get getGroceryItems() to work
    //HSN: removed this because all users end up sharing the same grocery list
    //OLD CODE static ArrayList<GroceryItem> groceryList = new ArrayList<>();


    //   create a "sequence" variable. This is an integer that represents the next id for a grocery item.
    //static int sequence = 0;

    //   Each time you read from it be sure to increment it. Set its initial value to 0.

    public static void main(String[] args) throws SQLException {

        //create a connection to be shared across the entire class
        Connection connection;

        //Create service variable, instantiated before testing
        CrudService crudService;


        //create a TCP server
        //make sure H2 dependency is in pom.xml
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        //establish connection
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/main", "", null);
        //instantiate new service
        crudService = new CrudService(connection);

        //initialize database
        crudService.initDatabase();

        // this adds test users to the application.
        //addTestUsers();

        // this sets the path to the static images and css files
        Spark.staticFileLocation("static");

        // Spark has more than just .get and .post methods. It also has .before. The .before
        // method runs before all requests that match the endpoint pattern specified. The one
        // below matches all paths and therefore runs before all requests. This is a good place
        // to put global login/security checks.

        Spark.before(
                "/*",
                (request, response) -> {


                    // allow anything with /images/ in the root of the uri
                    if (request.uri().startsWith("/images/")) return;

                    // allow anything with /css/ in the root of the uri
                    if (request.uri().startsWith("/css/")) return;

                    // allow the login page no matter what
                    if (request.uri().startsWith("/login")) return;

                    // allow anything if the user is logged in
                    if (request.session().attributes().contains("user")) return;

                    //   redirect to the login page
                    response.redirect("/login");
                    //   halt the request
                    halt();



                }
        );

        Spark.get(
                "/",
                (request, response) -> {

                    //   create a HashMap to hold our model
                    HashMap hashMapModel = new HashMap();


                    //   Get the user from the session
                    //request.session().attributes().contains("user");
                    Session session = request.session();
                    User user = session.attribute("user");
                    int id = user.getId();


                        //   user the getGroceryItems() method you created below to get a list of the user's own grocery items
                    ArrayList<GroceryItem> userArrayList = crudService.selectEntries(connection, id);

                    //   Put the user's grocery list into the model
                    hashMapModel.put("groceryList", userArrayList);

                        //   return a ModelAndView for the groceryList.mustache template
                    return new ModelAndView(hashMapModel, "groceryList.mustache");

                    },
                    new MustacheTemplateEngine()
        );



        Spark.get(
                "/login",
                (request, response) -> {
                    //   return a ModelAndView for the loginForm.mustache
                    return new ModelAndView(null, "loginForm.mustache");

                },
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/login",
                (request, response) -> {

                    //   get the username that was posted
                    String submittedName = request.queryParams("username");

                    //   get the password that was posted
                    String submittedPassword = request.queryParams("password");

                    //  create a new User instance with the submitted data
                    //User enteredUser = new User(submittedName, submittedPassword);

                    //  check the entered user's data against the current database of users
                    //create an H2 database for User
                    //create a selectUser method
                    User checkUser = crudService.selectUser(connection, submittedName);



                    //return any users with the same username as the entered user
                    //if the entered user does not match any users in the database, then redirect back to /login with an error message
                    /*if(checkUser == null) {
                        response.redirect("/");
                    } else if(checkUser.equals(enteredUser) && !submittedPassword.equals(checkUser.password)) {
                        response.redirect("/");
                    } else {
                        request.session().attribute("user", enteredUser);
                        response.redirect("/");
                        halt();
                    }*/


                    if(checkUser != null && (submittedPassword.equals(checkUser.password))) {
                        request.session().attribute("user", checkUser);
                    }

                    response.redirect("/");

                    halt();


                    //if the entered user does match a user that exists in the database, then check the entered password against the saved password
                    //if the entered password does not match the saved password, then redirect back to /login with an error message
                    //if the entered password does match the saved password, then enter the user into the session and redirect to /groceryList

                    /*
                    //   get the matching user (if any) from the >ArrayList< HashMap of users (defined at the top of this file)
                    User user = users.get(submittedName);

                    //   if the user is not null and the user's password equals the submitted password
                    if(user != null && (submittedPassword.equals(user.password))) {

                        //   add the user to the session
                        request.session().attribute("user", user);

                        //   redirect to the webroot
                        response.redirect("/");

                        //   halt this request
                        halt();

                    }
                    */
                    //   return null


                    return null;


                }
        );


        Spark.get(
                "/logout",
                (request, response) -> {
                    //   invalidate the user's session
                    request.session().invalidate();
                    //   redirect to the webroot
                    response.redirect("/");
                    //   halt this request
                    halt();

                    //   return null
                    return null;
                }
        );


        Spark.post(
                "/add-grocery-item",
                (request, response) -> {

                    User currentUser = request.session().attribute("user");

                    GroceryItem newGroceryItem = new GroceryItem();

                    newGroceryItem.setItemName(request.queryParams("itemName"));
                    newGroceryItem.setItemQuantity(request.queryParams("itemQuantity"));
                    newGroceryItem.setUserId(currentUser.getId());

                    crudService.insertEntry(connection, newGroceryItem);

                    response.redirect("/");

                    halt();

                    return null;
                }

        );
        // note that this is a get method. There is also a past method with the same endpoint

        Spark.get(
                "/edit-grocery-item",
                (request, response) -> {
                    //   create a map to hold your model values
                    HashMap m = new HashMap();

                    //   get the id of the item being deleted from the query params and convert it to an integer
                    String id = request.queryParams("id");
                    int editId = Integer.valueOf(id);

                    //   Get the user from the session
                    User user = request.session().attribute("user");

                    //   Get the user's grocery list using the method below
                    GroceryItem editGroceryItem = crudService.selectEntry(connection, editId);

                    //   use the getItem() method you create below to get the correct item from the grocery list
                    //   add the item into your m hashmap. Be sure to name the key "item".
                    //m.put("item", getItem(getGroceryItems(user), editId));
                    m.put("item", editGroceryItem);

                    //   return a new model and view object for the grocery item edit form, groceryItemForm.mustache
                    return new ModelAndView(m, "groceryItemForm.mustache");
                },
                new MustacheTemplateEngine()
        );



                    /* OLD CODE
                    int id = 0;
                    String name = new String();
                    String quantity = new String();

                    //GroceryItem groceryItem = new GroceryItem(id, name, quantity);
                    *

                    //   create a new GroceryItem
                    GroceryItem groceryItem = new GroceryItem();
                    //OLD CODE GroceryItem groceryItem = new GroceryItem(id, name, quantity);

                    //   Determine the next value in the sequence sequence. If it's currently 1, return 2. Then,
                    sequence = sequence+1;

                    //   set the item's id using the next value in the sequence
                    groceryItem.setId(sequence);
                    //   update the sequence to *be* the next value. That way when you come here again you'll get 2 instead of 1.


                    //   Set the item's name
                    groceryItem.setItemName(request.queryParams("name"));

                    //   Set the item's quantity
                    groceryItem.setItemQuantity(request.queryParams("quantity"));

                    //   Get the user from the session
                    Session session = request.session();
                    User user = session.attribute("user");
                    //set the user into groceryItem
                    groceryItem.setCreatedByUser(user);

                    //   Get the user's grocery list using the getGroceryItems() method you created below
                    //   add the item to the grocery list (the static property defined above)
                    //TRM have no idea what you meant by 'static property defined above'.  Was I supposed to create a static Array List? HSN: nope
                    crudService.insertEntry(groceryItem);

                    //   redirect to the webroot
                    response.redirect("/");

                    //   halt this request
                    halt();

                    //   return null
                    return null;

                }
        );


*/





        // note that this is a post method. There is also a get method with the same endpoint
        Spark.post(
                "/edit-grocery-item",
                (request, response) -> {
                    //   get the id of the item being deleted from the query params and convert it to an integer
                    String id = request.queryParams("id");
                    int editId = Integer.valueOf(id);

                    //   Get the user from the session
                    User user = request.session().attribute("user");

                    //   Get the selected grocery item using the connection and the id of item
                    GroceryItem editGroceryItem = crudService.selectEntry(connection, editId);

                    //   use getItem() to get the item being edited from the user's grocery list
                    //GroceryItem groceryItem = new GroceryItem(sequence, request.queryParams("name"), request.queryParams("quantity"));
                    //groceryItem = getItem(getGroceryItems(user), editId);


                    editGroceryItem.setItemName(request.queryParams("itemName"));
                    editGroceryItem.setItemQuantity(request.queryParams("itemQuantity"));
                    editGroceryItem.setUserId(user.getId());

                    crudService.updateEntry(connection, editId, editGroceryItem);

                    //   update the item's name
                    //groceryItem.setItemName(request.queryParams("name"));

                    //   update the item's quantity
                    //groceryItem.setItemQuantity(request.queryParams("quantity"));

                    // note: (no code to write here) we don't need to add this item into the grocery list because it's already there.

                    //   redirect to the webroot
                    response.redirect("/");

                    //   halt this request
                    halt();

                    //   return null
                    return null;
                }
        );
        /*

        Spark.get(
                "/delete-grocery-item",
                (request, response) -> {
                    //   get the id of the item being deleted from the query params and convert it to an integer
                    String id = request.queryParams("id");
                    int deleteId = Integer.valueOf(id);

                    //   Get the user from the session
                    User user = request.session().attribute("user");

                    //   Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    //   use getItem() to get the item being edited from the user's grocery list
                    GroceryItem deleteGroceryItem = new GroceryItem();
                    deleteGroceryItem = getItem(groceryList, deleteId);

                    //   delete this item from the array list
                    //groceryList.remove(deleteGroceryItem);
                    crudService.

                    //   redirect to the webroot
                    response.redirect("/");

                    // halt this request
                    halt();


                    // return null
                    return null;
                }
        );





    private static ArrayList<GroceryItem> getGroceryItems(User user) {
        //   check if we have an array list in the groceryLists "global" variable already.
        //not sure what you meant by global variable.
        //heck, i'm not sure what getGroceryItems() is supposed to be.  A method?  An arraylist?

        //OLD CODE boolean arrayListExists = groceryMap.containsValue(groceryList);
        //   if not, create an empty array list and assign it into the hashmap. Use the user's name as the key.
        //OLD CODE if (!arrayListExists) {
        //OLD CODE    groceryMap.put(user.name, groceryList);
        if(!groceryMap.containsKey(user.getName())) {  //key: me ; value: my grocery arraylist
            ArrayList<GroceryItem> groceryList = new ArrayList<>();
            groceryMap.put(user.getName(), groceryList);
        }

        //   return the arraylist of groceries for this user. Use their name as the key in the hashmap
        //TRM: tried to figure out a way to get a new array list in above if loop to be returned but had no luck
        //went ahead and created a static array list
        //HSN: turns out creating a static array list results in all users sharing that one array list to store grocery items
        //figured out how to return an empty grocery list specific to each user by first adding it into the hashmap and then
        //returning the value (the arraylist) of the hashmap associated with the current user

        return groceryMap.get(user.getName());
    }


    static GroceryItem getItem(ArrayList<GroceryItem> groceryList, int id) {
        //   loop over the list of grocery items
        for (GroceryItem groceryItem : groceryList) {

            //groceryList.forEach(GroceryItem -> GroceryItem.id == new GroceryItem());
            //   check if this item's id match the id of the item being deleted.
            if (id == groceryItem.id) {
                //   if so, return this item
                return groceryItem;
            }
            // it's possible that the list of grocery items is empty or the id provided isn't actually in the list.
            // If that happens we won't reach the return statement in the loop above. Because of this we must add
            // a default return statement that returns null.
        }
        return null;
    }



    // this method adds a set of test users to log in with.
    static void addTestUsers() {
        users.put("Alice", new User("Alice", "cats"));
        users.put("Bob", new User("Bob", "bob"));
        users.put("Charlie", new User("Charlie", "password"));
        users.put("Doug", new User("Doug", "password"));
    }

    */
    }
}
