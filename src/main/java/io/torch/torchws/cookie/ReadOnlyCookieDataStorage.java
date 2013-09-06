package io.torch.torchws.cookie;

import io.netty.handler.codec.http.CookieDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ReadOnlyCookieDataStorage implements Iterable<CookieVariable> {

    private HashMap<String, CookieVariable> cookieStorage = new HashMap<>();

    public ReadOnlyCookieDataStorage(String cookieString) {
        if (cookieString != null) {
            Set<io.netty.handler.codec.http.Cookie> cookies = CookieDecoder.decode(cookieString);

            for (io.netty.handler.codec.http.Cookie cookie : cookies) {
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
