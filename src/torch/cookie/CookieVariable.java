package torch.cookie;

public class CookieVariable {
    private final String name;
    private String value;
    private boolean fresh = false;
    
    public CookieVariable(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public CookieVariable(String name, String value, boolean fresh) {
        this.name = name;
        this.value = value;
        this.fresh = fresh;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the fresh
     */
    public boolean isFresh() {
        return fresh;
    }
    
}
