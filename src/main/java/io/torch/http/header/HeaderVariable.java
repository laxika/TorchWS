
package io.torch.http.header;

public class HeaderVariable {

    private final String name;
    private final String value;

    public HeaderVariable(String headerName, String headerValue) {
        this.name = headerName;
        this.value = headerValue;
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