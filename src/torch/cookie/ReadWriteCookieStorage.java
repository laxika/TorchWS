package torch.cookie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadWriteCookieStorage implements Iterable {

    public HashMap<String, Cookie> cookieStorage = new HashMap<>();

    public void addCookie(Cookie cookie) {
        cookieStorage.put(cookie.getName(), cookie);
    }

    public Cookie getCookie(String name) {
        return cookieStorage.get(name);
    }
    
    @Override
    public Iterator<Map.Entry<String, Cookie>> iterator() {
        return cookieStorage.entrySet().iterator();
    }
}
