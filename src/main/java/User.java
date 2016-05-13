/**
 * Created by doug on 5/5/16.
 * Started by Taylor on 5/11/16.
 */
public class User {
    // todo: add a name property
    String name;

    // todo: add a password property
    String password;

    // todo: add a constructor that takes the name and password of the user and configures the object

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    // todo: create getters and setters for name and password

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
