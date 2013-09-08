package io.torch.cookie;

/**
 * Hold the data of a html cookie.
 */
public class CookieVariable {

    private final String name;
    private final String value;
    private final String path;

    public CookieVariable(String name, String value) {
        this(name, value, null);
    }

    public CookieVariable(String name, String value, String path) {
        this.name = name;
        this.value = value;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return value;
    }
}
