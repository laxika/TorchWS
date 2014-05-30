package io.torch.http.header;

import io.netty.handler.codec.http.HttpHeaders;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A read only data storage for HeaderVariables.
 */
public class ReadOnlyHeaderDataStorage implements Iterable<HeaderVariable> {

    private final HashMap<String, HeaderVariable> headerStorage = new HashMap<>();

    public ReadOnlyHeaderDataStorage(HttpHeaders headers) {
        for (Entry<String, String> header : headers) {
            headerStorage.put(header.getKey(), new HeaderVariable(header.getKey(), header.getValue()));
        }
    }

    public HeaderVariable getHeader(String name) {
        return headerStorage.get(name);
    }

    @Override
    public Iterator<HeaderVariable> iterator() {
        return headerStorage.values().iterator();
    }
}
