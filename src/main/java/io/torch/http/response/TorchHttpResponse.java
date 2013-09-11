package io.torch.http.response;

import io.netty.handler.codec.http.HttpHeaders.Names;
import io.torch.cookie.ReadWriteCookieStorage;
import io.torch.http.header.ReadWriteHeaderStorage;
import io.torch.http.response.status.ClientErrorResponseStatus;
import io.torch.http.response.status.RedirectionResponseStatus;
import io.torch.http.response.status.ResponseStatus;
import io.torch.http.response.status.ServerErrorResponseStatus;
import io.torch.http.response.status.SuccessfulResponseStatus;

public class TorchHttpResponse {

    private final ReadWriteCookieStorage cookieStorage = new ReadWriteCookieStorage();
    private final ReadWriteHeaderStorage headerStorage = new ReadWriteHeaderStorage();
    private final StringBuilder content = new StringBuilder();
    private ResponseStatus status = SuccessfulResponseStatus.OK;
    private String contentType = "text/html; charset=UTF-8";

    public void appendContent(String text) {
        content.append(text);
    }

    public String getContent() {
        return content.toString();
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
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
        status = RedirectionResponseStatus.SEE_OTHER;

        //Add the location header
        headerStorage.setHeader(Names.LOCATION, target);
    }
}
