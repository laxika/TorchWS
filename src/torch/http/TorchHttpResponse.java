package torch.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import torch.cookie.ReadWriteCookieStorage;

public class TorchHttpResponse {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private final ReadWriteCookieStorage cookieStorage = new ReadWriteCookieStorage();

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

    /**
     * Return the cookie storage for new, need to send cookies. Only add cookies
     * what you plan to send to the client.
     * 
     * @return the cookie storage
     */
    public ReadWriteCookieStorage getCookieData() {
        return cookieStorage;
    }
}
