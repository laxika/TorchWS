package torch.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadWriteTemplateStorage implements Iterable {

    public HashMap<String, Object> templateStorage = new HashMap<>();

    public void setVariable(String name, Object data) {
        templateStorage.put(name, data);
    }

    public Object getVariable(String name) {
        return templateStorage.get(name);
    }
    
    public HashMap<String, Object> getRoot() {
        return templateStorage;
    }
    
    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return templateStorage.entrySet().iterator();
    }
}
