package io.torch.cookie;

import java.util.HashMap;
import java.util.Iterator;

public class ReadWriteCookieStorage implements Iterable<CookieVariable> {

    public HashMap<String, CookieVariable> cookieStorage = new HashMap<>();

    public void addCookie(CookieVariable cookie) {
        cookieStorage.put(cookie.getName(), cookie);
    }

    public CookieVariable getCookie(String name) {
        return cookieStorage.get(name);
    }

    @Override
    public Iterator<CookieVariable> iterator() {
        return cookieStorage.values().iterator();
    }
}
