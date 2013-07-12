package torch.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.HashMap;
import java.util.Set;
import static torch.http.HttpBase.session;
import torch.session.Session;

public class TorchHttpResponse extends HttpBase {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private HashMap<String,String> cookies = new HashMap<>();
    private String sessionId = "";
    
    public TorchHttpResponse(HttpRequest request) {
        
        String cookieString = request.headers().get(COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);

            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("SESSID")) {
                    sessionId = cookie.getValue();
                }
            }
        }
    }

    public void appendContent(String text) {
        content.append(text);
    }
    
    public String getContent() {
        return content.toString();
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Session startNewSession() {
        //Remove the existing session
        if(!sessionId.equals("")) {
            session.removeSessionById(sessionId);
        }
        
        Session newSession = session.startNewSession();
        
        cookies.put("SESSID", newSession.getSessionId());
        
        return newSession;
    }
    
    public HashMap<String,String> getNewCookieData() {
        return cookies;
    }
}
