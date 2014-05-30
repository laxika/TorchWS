package io.torch.http.request;

import io.netty.handler.codec.http.HttpMethod;

public enum RequestMethod {

    GET, POST, PUT, DELETE;

    public static RequestMethod getMethodByNettyMethod(HttpMethod method) {
        switch (method.name()) {
            case "POST":
                return POST;
            case "DELETE":
                return DELETE;
            case "PUT":
                return PUT;
            default:
                return GET;
        }
    }
}
