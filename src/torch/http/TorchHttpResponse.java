package torch.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import torch.cookie.Cookie;
import torch.cookie.CookieStorage;
import static torch.http.HttpBase.sessionManager;
import torch.session.Session;

public class TorchHttpResponse extends HttpBase {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private final CookieStorage cookieStorage;
    
    public TorchHttpResponse(CookieStorage cookieStorage) {
        this.cookieStorage = cookieStorage;
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
        if(cookieStorage.getCookie("SESSID") != null) {
            sessionManager.removeSessionById(cookieStorage.getCookie("SESSID").getValue());
        }
        
        Session newSession = sessionManager.startNewSession();
        
        cookieStorage.addCookie(new Cookie("SESSID", newSession.getSessionId(), true));
        
        return newSession;
    }
    
    public CookieStorage getNewCookieData() {
        return cookieStorage;
    }
}
