package torch.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import torch.cookie.ReadWriteCookieStorage;
import torch.http.header.ReadWriteHeaderStorage;

public class TorchHttpResponse {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private final ReadWriteCookieStorage cookieStorage = new ReadWriteCookieStorage();
    private final ReadWriteHeaderStorage headerStorage = new ReadWriteHeaderStorage();

    public void appendContent(String text) {
        content.append(text);
    }

    public String getContent() {
        return content.toString();
    }

    public HttpResponseStatus getStatus() {
        if(headerStorage.getHeader(Names.LOCATION) != null) {
            return HttpResponseStatus.SEE_OTHER;
        }
        
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
    
    public ReadWriteHeaderStorage getHeaderData() {
        return headerStorage;
    }
}
