package io.torch.cookie;

/**
 * Store the data of a HTML cookie.
 */
public class CookieVariable {

    private final String name;
    private final String value;
    private final String path;

    /**
     * Create a new CookieVariable on the domain level.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     */
    public CookieVariable(String name, String value) {
        this(name, value, null);
    }

    /**
     * Create a new CookieVariable on the given path level.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the path where the cookie is set
     */
    public CookieVariable(String name, String value, String path) {
        this.name = name;
        this.value = value;
        this.path = path;
    }

    /**
     * Return the name of the cookie.
     *
     * @return the name of the cookie.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the value of the cookie.
     *
     * @return the value of the cookie.
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the path of the cookie. Return null if it's a domain wide cookie.
     *
     * @return the path of the cookie.
     */
    public String getPath() {
        return path;
    }

    /**
     * Return the value of the cookie.
     *
     * @return the value of the cookie.
     */
    @Override
    public String toString() {
        return value;
    }
}
