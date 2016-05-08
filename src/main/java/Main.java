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
    static ArrayList<GroceryItem> groceryList = new ArrayList<>();

    public static void main(String[] args){
        // this adds test users to the application.
        addTestUsers();

        Spark.get(
                "/",
                (request, response) -> {
                    // create a HashMap to hold our model
                    HashMap<String, Object> m = new HashMap<>();

                    // Check if the user exists in the current session.
                    if(request.session().attributes().contains("user")){
                        // If so, add the groceryList into the model
                        m.put("groceryList", groceryList);

                    // if not....
                    } else {
                        // if not, then redirect to the login page
                        response.redirect("/login");
                        halt();
                    }

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
                    // Set the item's name
                    item.name = request.queryParams("name");
                    // Set the item's quantity
                    item.quantity = request.queryParams("quantity");

                    // add the item to the grocery list (the static property defined above)
                    groceryList.add(item);

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

    }

    // this method adds a set of test users to log in with.
    static void addTestUsers() {
        users.put("Alice", new User("Alice", "cats"));
        users.put("Bob", new User("Bob", "bob"));
        users.put("Charlie", new User("Charlie", "password"));
    }
}
