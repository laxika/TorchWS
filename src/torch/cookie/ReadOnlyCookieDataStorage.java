package torch.cookie;

import io.netty.handler.codec.http.CookieDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ReadOnlyCookieDataStorage implements Iterable {

    private HashMap<String, Cookie> cookieStorage = new HashMap<>();

    public ReadOnlyCookieDataStorage(String cookieString) {
        if (cookieString != null) {
            Set<io.netty.handler.codec.http.Cookie> cookies = CookieDecoder.decode(cookieString);

            for (io.netty.handler.codec.http.Cookie cookie : cookies) {
                cookieStorage.put(cookie.getName(), new Cookie(cookie.getName(), cookie.getValue()));
            }
        }
    }

    public Cookie getCookie(String name) {
        return cookieStorage.get(name);
    }

    @Override
    public Iterator<Entry<String, Cookie>> iterator() {
        return cookieStorage.entrySet().iterator();
    }
}
