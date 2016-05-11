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
    // I'm providing this as a pseudo-database of users.
    // See addUsers below for the set of users who can login
    static HashMap<String, User> users = new HashMap<>();

    // todo: create a HashMap that maps usernames to ArrayLists containing GroceryItem objects. (HashMap<String, ArrayList<GroceryItem>>)

    // todo: create a "sequence" variable. This is an integer that represents the next id for a grocery item.
    // todo: Each time you read from it be sure to increment it. Set its initial value to 0.

    public static void main(String[] args){
        // this adds test users to the application.
        addTestUsers();

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
                    if(request.uri().startsWith("/images/")) return;

                    // allow anything with /css/ in the root of the uri
                    if(request.uri().startsWith("/css/")) return;

                    // allow the login page no matter what
                    if(request.uri().startsWith("/login")) return;

                    // allow anything if the user is logged in
                    if(request.session().attributes().contains("user")) return;

                    // todo: redirect to the login page

                    // todo: halt the request
                }
        );

        Spark.get(
                "/",
                (request, response) -> {
                    // todo: create a HashMap to hold our model


                    // todo: Get the user from the session

                    // todo: user the getGroceryItems() method you created below to get a list of the user's own grocery items


                    // todo: Put the user's grocery list into the model


                    // todo: return a ModelAndView for the groceryList.mustache template

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/add-grocery-item",
                (request, response) -> {
                    // todo: create a new GroceryItem
                    GroceryItem item = new GroceryItem();

                    // todo: Determine the next value in the sequence sequence. If it's currently 1, return 2. Then,
                    // todo: set the item's id using the next value in the sequence
                    // todo: update the sequence to *be* the next value. That way when you come here again you'll get 2 instead of 1.

                    // todo: Set the item's name

                    // todo: Set the item's quantity

                    // todo: Get the user from the session

                    // todo: Get the user's grocery list using the getGroceryItems() method you created below

                    // todo: add the item to the grocery list (the static property defined above)


                    // todo: redirect to the webroot

                    // todo: halt this request

                    // todo: return null
                }
        );

        // note that this is a get method. There is also a past method with the same endpoint
        Spark.get(
                "/edit-grocery-item",
                (request, response) -> {
                    // todo: create a map to hold your model values

                    // todo: get the id of the item being deleted from the query params and convert it to an integer

                    // todo: Get the user from the session

                    // todo: Get the user's grocery list using the method below


                    // todo: use the getItem() method you create below to get the correct item from the grocery list


                    // todo: add the item into your m hashmap. Be sure to name the key "item".


                    // todo: return a new model and view object for the grocery item edit form, groceryItemForm.mustache
                },
                new MustacheTemplateEngine()
        );

        // note that this is a post method. There is also a get method with the same endpoint
        Spark.post(
                "/edit-grocery-item",
                (request, response) -> {
                    // todo: get the id of the item being deleted from the query params and convert it to an integer

                    // todo: Get the user from the session

                    // todo: Get the user's grocery list

                    // todo: use getItem() to get the item being edited from the user's grocery list

                    // todo: update the item's name

                    // todo: update the item's quantity

                    // note: (no code to write here) we don't need to add this item into the grocery list because it's already there.

                    // todo: redirect to the webroot

                    // todo: halt this request

                    // todo: return null
                }
        );

        Spark.get(
                "/delete-grocery-item",
                (request, response) -> {
                    // todo: get the id of the item being deleted from the query params and convert it to an integer

                    // todo: Get the user from the session

                    // todo: Get the user's grocery list

                    // todo: use getItem() to get the item being edited from the user's grocery list

                    // todo: delete this item from the array list


                    // todo: redirect to the webroot

                    // todo: halt this request


                    // todo: return null
                }
        );

        Spark.get(
                "/login",
                (request, response) -> {
                    // todo: return a ModelAndView for the loginForm.mustache

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    // todo: get the username that was posted

                    // todo: get the password that was posted

                    // todo: get the matching user (if any) from the ArrayList of users (defined at the top of this file)


                    // todo: if the user is not null and the user's password equals the submitted password

                    // todo: add the user to the session


                    // todo: redirect to the webroot

                    // todo: halt this request


                    // todo: return null

                }
        );

        Spark.get(
                "/logout",
                (request, response) -> {
                    // todo: invalidate the user's session

                    // todo: redirect to the webroot

                    // todo: halt this request


                    // todo: return null
                }
        );
    }

    private static ArrayList<GroceryItem> getGroceryItems(User user) {
        // todo: check if we have an array list in the groceryLists "global" variable already.

            // todo: if not, create an empty array list and assign it into the hashmap. Use the user's name as the key.

        // return the arraylist of groceries for this user. Use their name as the key in the hashmap

    }

    static GroceryItem getItem(ArrayList<GroceryItem> groceryList, int id){
        // todo: loop over the list of grocery items

        // todo: check if this item's id match the id of the item being deleted.

        // todo: if so, return this item


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
