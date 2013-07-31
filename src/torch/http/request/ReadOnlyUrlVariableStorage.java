package torch.http.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadOnlyUrlVariableStorage implements Iterable {

    private HashMap<String, String> urlVariableStorage = new HashMap<>();

    public ReadOnlyUrlVariableStorage(HashMap<String,String> urlData) {
        this.urlVariableStorage = urlData;
    }

    public String getVariableValue(String name) {
        return urlVariableStorage.get(name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return urlVariableStorage.entrySet().iterator();
    }
}
