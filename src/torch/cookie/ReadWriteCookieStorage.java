package torch.cookie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadWriteCookieStorage implements Iterable {

    public HashMap<String, CookieVariable> cookieStorage = new HashMap<>();

    public void addCookie(CookieVariable cookie) {
        cookieStorage.put(cookie.getName(), cookie);
    }

    public CookieVariable getCookie(String name) {
        return cookieStorage.get(name);
    }
    
    @Override
    public Iterator<Map.Entry<String, CookieVariable>> iterator() {
        return cookieStorage.entrySet().iterator();
    }
}
