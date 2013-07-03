package torch.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Set;
import torch.session.Session;

/**
 *
 * @author laxika
 */
public class TorchHttpRequest extends HttpBase {

    private final HttpRequest request;
    private final HashMap<String, Cookie> cookies = new HashMap<>();

    public TorchHttpRequest(HttpRequest request) {
        this.request = request;

        String cookieString = request.headers().get(COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);

            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.getName(), cookie);
            }
        }
    }

    //Return the session for the request via the sessdata cookie
    public Session getSession() {
        return session.getSession(cookies.get("SESSID").getValue());
    }
}
