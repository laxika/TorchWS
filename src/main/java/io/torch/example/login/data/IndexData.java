package io.torch.example.login.data;

public class IndexData {

    private final boolean isLoggedIn;
    private String username;

    public IndexData(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
