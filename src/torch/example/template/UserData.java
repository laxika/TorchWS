
package torch.example.template;

public class UserData {
    
    private final String userName;
    private final String extraInfo;
    
    public UserData(String userName, String extraInfo) {
        this.userName = userName;
        this.extraInfo = extraInfo;
    }
    
    public String getUsername() {
        return userName;
    }
    
    public String getExtrainfo() {
        return extraInfo;
    }
}
