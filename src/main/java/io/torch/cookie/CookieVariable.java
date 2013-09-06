package io.torch.cookie;

/**
 * Hold the data of a html cookie.
 */
public class CookieVariable {

    private final String name;
    private final String value;

    public CookieVariable(String cookieName, String cookieValue) {
        this.name = cookieName;
        this.value = cookieValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
