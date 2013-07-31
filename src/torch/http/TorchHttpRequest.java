package torch.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import java.util.HashMap;
import java.util.List;
import torch.cookie.ReadOnlyCookieDataStorage;
import torch.http.request.ReadOnlyRouteDataStorage;
import torch.route.Route;

public class TorchHttpRequest {

    private final HttpRequest request;
    private final HashMap<String, String> postVariables;
    private final ReadOnlyRouteDataStorage routeVariables;
    private final ReadOnlyCookieDataStorage cookieStorage;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.request = request;
        this.cookieStorage = new ReadOnlyCookieDataStorage(request.headers().get(COOKIE));
        this.postVariables = new HashMap<>();

        if (request.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

            List<InterfaceHttpData> data = decoder.getBodyHttpDatas();
            for (InterfaceHttpData interf : data) {
                if (interf.getHttpDataType() == HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) interf;

                    postVariables.put(attribute.getName(), attribute.getValue());
                }
            }
        }

        if (route != null) {
            this.routeVariables = new ReadOnlyRouteDataStorage(route.calculateVariablesValuesFromUrl(request.getUri()));
        } else {
            this.routeVariables = new ReadOnlyRouteDataStorage(new HashMap<String,String>());
        }
    }

    public RequestMethod getMethod() {
        HttpMethod method = request.getMethod();

        if (method == HttpMethod.POST) {
            return RequestMethod.POST;
        }

        if (method == HttpMethod.DELETE) {
            return RequestMethod.DELETE;
        }

        if (method == HttpMethod.PUT) {
            return RequestMethod.PUT;
        }

        return RequestMethod.GET;
    }
    
    public ReadOnlyRouteDataStorage getRouteData() {
        return routeVariables;
    }

    public String getPostVariable(String name) {
        return postVariables.get(name);
    }

    public ReadOnlyCookieDataStorage getCookieData() {
        return cookieStorage;
    }
}
