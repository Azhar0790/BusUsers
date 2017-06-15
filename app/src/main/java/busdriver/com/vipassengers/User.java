package busdriver.com.vipassengers;

/**
 * Created by richardalexander on 16/02/16.
 */
public class User {

    String name, username, password, image, numero, token;
    int edad, tipoveh;

    public User(String name, int edad, int tipoveh, String username, String password, String image, String numero, String token) {
        this.name = name;
        this.edad = edad;
        this.tipoveh = tipoveh;
        this.username = username;
        this.password = password;
        this.image = image;
        this.numero = numero;
        this.token = token;
    }

    public User(String username , String password, String token) {
        this("", 0, 0, username, password, "", "",token);
    }
}