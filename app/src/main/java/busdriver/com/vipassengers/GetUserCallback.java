package busdriver.com.vipassengers;

/**
 * Created by richardalexander on 16/02/16.
 */
public interface GetUserCallback {

    /**
     * Invoked when background task is completed
     */

    public abstract void done(User returnedUser);
}