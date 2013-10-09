
package io.torch.http.header;

/**
 * A header variable came with the HTTP request.
 */
public class HeaderVariable {

    private final String name;
    private final String value;

    /**
     * Create a new HeaderVariable.
     * 
     * @param headerName the name of the header variable
     * @param headerValue the value of the header variable
     */
    public HeaderVariable(String headerName, String headerValue) {
        this.name = headerName;
        this.value = headerValue;
    }

    /**
     * Return the name of the header variable.
     * 
     * @return the name of the header variable.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the value of the header variable.
     * 
     * @return the value of the header variable.
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the name of the header variable.
     * 
     * @return the name of the header variable.
     */
    @Override
    public String toString() {
        return value;
    }
}
