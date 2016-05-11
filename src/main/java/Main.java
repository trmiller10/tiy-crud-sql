import org.omg.PortableInterceptor.USER_EXCEPTION;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.halt;

/**
 * Created by doug on 5/8/16.
 */
public class Main {
    // I'm providing this as a pseudo-database of users. See addUsers below for the set of users who can login
    static HashMap<String, User> users = new HashMap<>();

    // create an ArrayList of GroceryItem objects to hold the items we add to our grocery list
    static HashMap<String, ArrayList<GroceryItem>> groceryLists = new HashMap<>();

    // create a "sequence" variable. This is an integer that represents the next id for a grocery item.
    // Each time you read from it be sure to increment it. Set its initial value to 0.
    static int sequence = 0;

    public static void main(String[] args){
        // this adds test users to the application.
        addTestUsers();

        Spark.staticFileLocation("static");

        // Spark has more than just .get and .post methods. It also has .before. The .before
        // method runs before all requests that match the endpoint pattern specified. The one
        // below matches all paths and therefore runs before all requests. This is a good place
        // to put global login/security checks.
        Spark.before(
                "/*",
                (request, response) -> {

                    // allow anything with /images/ in the root of the uri
                    if(request.uri().startsWith("/images/")) return;

                    // allow anything with /css/ in the root of the uri
                    if(request.uri().startsWith("/css/")) return;

                    // allow the login page no matter what
                    if(request.uri().startsWith("/login")) return;

                    // allow anything if the user is logged in
                    if(request.session().attributes().contains("user")) return;

                    // otherwise, redirect to the login page
                    response.redirect("/login");

                    halt();
                }
        );

        Spark.get(
                "/",
                (request, response) -> {
                    // create a HashMap to hold our model
                    HashMap<String, Object> m = new HashMap<>();

                    // Get the user from the session
                    User user = request.session().attribute("user");

                    // Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    m.put("groceryList", groceryList);

                    // return a ModelAndView for the groceryList.mustache template
                    return new ModelAndView(m, "groceryList.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/add-grocery-item",
                (request, response) -> {
                    // create a new GroceryItem
                    GroceryItem item = new GroceryItem();
                    // Increment the sequence property you created above by one.
                    // set the item's id using the sequence.
                    item.id = ++sequence;

                    // Set the item's name
                    item.name = request.queryParams("name");
                    // Set the item's quantity
                    item.quantity = request.queryParams("quantity");

                    // set the owner of this grocery item
                    item.user = request.session().attribute("user");

                    // Get the user from the session
                    User user = request.session().attribute("user");

                    // Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    // add the item to the grocery list
                    groceryList.add(item);

                    // redirect to the webroot
                    response.redirect("/");
                    // halt this request
                    halt();

                    // return null
                    return null;
                }
        );

        // note that this is a get method. There is also a past method with the same endpoint
        Spark.get(
                "/edit-grocery-item",
                (request, response) -> {
                    HashMap<String, Object> m = new HashMap<>();

                    // get the id of the item being deleted from the query params and convert it to an integer
                    int id = Integer.parseInt(request.queryParams("id"));

                    // Get the user from the session
                    User user = request.session().attribute("user");

                    // Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    // use the getItem() method you create below to get the correct item from the grocery list
                    GroceryItem item = getItem(groceryList, id);

                    // add the item into your m hashmap. Be sure to name the key "item".
                    m.put("item", item);

                    return new ModelAndView(m, "groceryItemForm.mustache");
                },
                new MustacheTemplateEngine()
        );

        // note that this is a post method. There is also a get method with the same endpoint
        Spark.post(
                "/edit-grocery-item",
                (request, response) -> {
                    // get the id of the item being deleted from the query params and convert it to an integer
                    int id = Integer.parseInt(request.queryParams("id"));

                    // Get the user from the session
                    User user = request.session().attribute("user");

                    // Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    // get the grocery item from the array list using the getItem() method created below
                    GroceryItem item = getItem(groceryList, id);

                    // update the item's name
                    item.name = request.queryParams("name");

                    // update the item's quantity
                    item.quantity = request.queryParams("quantity");

                    // note: (no code to write here) we don't need to add this item into the grocery list because it's already there.

                    // redirect to the webroot
                    response.redirect("/");

                    // halt this request
                    halt();

                    // return null
                    return null;
                }
        );

        Spark.get(
                "/delete-grocery-item",
                (request, response) -> {
                    // get the id of the item being deleted from the query params and convert it to an integer
                    int id = Integer.parseInt(request.queryParams("id"));

                    // Get the user from the session
                    User user = request.session().attribute("user");

                    // Get the user's grocery list
                    ArrayList<GroceryItem> groceryList = getGroceryItems(user);

                    // get the grocery item from the array list using the getItem() method created below
                    GroceryItem item = getItem(groceryList, id);

                    // delete this item from the array list
                    groceryList.remove(item);

                    // redirect to the webroot
                    response.redirect("/");
                    // halt this request
                    halt();

                    // return null
                    return null;
                }
        );

        Spark.get(
                "/login",
                (request, response) -> {
                    // return a ModelAndView for the loginForm.mustache
                    return new ModelAndView(null, "loginForm.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    // get the username that was posted
                    String username = request.queryParams("username");
                    // get the password that was posted
                    String password = request.queryParams("password");
                    // get the matching user (if any) from the ArrayList of users (defined at the top of this file)
                    User user = users.get(username);

                    // if the user is not null and the user's password equals the submitted password
                    if(user != null && user.getPassword().equals(password)){
                        // then add the user to the session
                        request.session().attribute("user", user);
                    }

                    // redirect to the webroot
                    response.redirect("/");
                    // halt this request
                    halt();

                    // return null
                    return null;
                }
        );

        Spark.get(
                "/logout",
                (request, response) -> {
                    request.session().invalidate();
                    response.redirect("/");
                    halt();
                    return null;
                }
        );

    }

    private static ArrayList<GroceryItem> getGroceryItems(User user) {
        if(!groceryLists.containsKey(user.name)){
            groceryLists.put(user.name, new ArrayList<GroceryItem>());
        }

        return groceryLists.get(user.name);
    }

    static GroceryItem getItem(ArrayList<GroceryItem> groceryList, int id){
        // loop over the list of grocery items
        for(GroceryItem item : groceryList){
            // does this item's id match the id of the item being deleted?
            if(item.id == id){
                // return this item
                return item;
            }
        }

        // it's possible that the list of grocery items is empty or the id provided isn't actually in the list.
        // If that happens we won't reach the return statement in the loop above. Because of this we must add
        // a default return statement that returns null.
        return null;
    }

    // this method adds a set of test users to log in with.
    static void addTestUsers() {
        users.put("Alice", new User("Alice", "cats"));
        users.put("Bob", new User("Bob", "bob"));
        users.put("Charlie", new User("Charlie", "password"));
        users.put("Doug", new User("Doug", "password"));
    }
}
