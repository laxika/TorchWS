package torch.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.HashMap;
import torch.session.Session;

public class TorchHttpResponse extends HttpBase {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private HashMap<String,String> cookies = new HashMap<>();

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
        Session newSession = session.startNewSession();
        
        cookies.put("SESSID", newSession.getSessionId());
        
        return newSession;
    }
    
    public HashMap<String,String> getNewCookieData() {
        return cookies;
    }
}
