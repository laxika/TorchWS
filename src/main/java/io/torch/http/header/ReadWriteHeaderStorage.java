package io.torch.http.header;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadWriteHeaderStorage implements Iterable {

    public HashMap<String, String> headerStorage = new HashMap<>();

    public void setHeader(String name, String value) {
        headerStorage.put(name, value);
    }

    public String getHeader(String name) {
        return headerStorage.get(name);
    }
    
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headerStorage.entrySet().iterator();
    }
}
