package torch.http;

public class TorchHttpResponse {

    private StringBuilder content = new StringBuilder();

    public void appendContent(String text) {
        content.append(text);
    }
    
    public String getContent() {
        return content.toString();
    }
}
