package torch.http.post;

import torch.http.post.PostVariable;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ReadOnlyPostDataStorage implements Iterable<PostVariable> {

    private HashMap<String, PostVariable> postVariableStorage = new HashMap<>();

    public ReadOnlyPostDataStorage(HttpRequest request) {
        if (request.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

            List<InterfaceHttpData> data = decoder.getBodyHttpDatas();
            for (InterfaceHttpData interf : data) {
                if (interf.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) interf;

                    postVariableStorage.put(attribute.getName(), new PostVariable(attribute.getName(), attribute.getValue()));
                }
            }
        }
    }

    public PostVariable getVariable(String name) {
        return postVariableStorage.get(name);
    }

    @Override
    public Iterator<PostVariable> iterator() {
        return postVariableStorage.values().iterator();
    }
}