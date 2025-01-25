package uz.gym.crm.dto;

public class ResponseWrapper<T> {
    private String username;
    private T data;

    public ResponseWrapper(String username, T data) {
        this.username = username;
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}