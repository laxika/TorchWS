package io.torch.torchws.http.response;

import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.torch.torchws.cookie.ReadWriteCookieStorage;
import io.torch.torchws.http.header.ReadWriteHeaderStorage;

public class TorchHttpResponse {

    private StringBuilder content = new StringBuilder();
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";
    private boolean isError = false;
    private final ReadWriteCookieStorage cookieStorage = new ReadWriteCookieStorage();
    private final ReadWriteHeaderStorage headerStorage = new ReadWriteHeaderStorage();

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
    
    public ReadWriteHeaderStorage getHeaderData() {
        return headerStorage;
    }
    
    public void redirect(String target) {
        //Set the status to 303 (see other)
        status = HttpResponseStatus.SEE_OTHER;
        
        //Add the location header
        headerStorage.setHeader(Names.LOCATION, target);
    }
    
    public void error(int errorId) {
        status = HttpResponseStatus.valueOf(errorId);
        isError = true;
    }
    
    public boolean isError() {
        return isError;
    }
}
