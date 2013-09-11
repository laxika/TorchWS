package io.torch.cookie;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A read only data storage for CookieVariables.
 */
public class ReadOnlyCookieDataStorage implements Iterable<CookieVariable> {

    private final HashMap<String, CookieVariable> cookieStorage = new HashMap<>();

    public ReadOnlyCookieDataStorage(String cookieString) {
        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);

            for (Cookie cookie : cookies) {
                cookieStorage.put(cookie.getName(), new CookieVariable(cookie.getName(), cookie.getValue()));
            }
        }
    }

    public CookieVariable getCookie(String name) {
        return cookieStorage.get(name);
    }

    @Override
    public Iterator<CookieVariable> iterator() {
        return cookieStorage.values().iterator();
    }
}
