package torch.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.Attribute;
import java.util.HashMap;
import java.util.List;
import torch.route.Route;

public class TorchHttpRequest {

    private final HttpRequest request;
    private final HashMap<String, String> routeVariables;
    private final HashMap<String, String> postVariables;

    public TorchHttpRequest(HttpRequest request, Route route) {
        this.request = request;
        
        this.postVariables = new HashMap<>();

        if (request.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

            List<InterfaceHttpData> data = decoder.getBodyHttpDatas();
            for (InterfaceHttpData interf : data) {
                if (interf.getHttpDataType() == HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) interf;

                    postVariables.put(attribute.key().name(), attribute.get().toString());
                }
            }
        }

        if (route != null) {
            this.routeVariables = route.calculateVariablesValuesFromUrl(request.getUri());
        } else {
            this.routeVariables = new HashMap<>();
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

    public String getUrlVariable(String name) {
        return routeVariables.get(name);
    }

    public String getPostVariable(String name) {
        return postVariables.get(name);
    }
}
