package main.TCP;

import lombok.Getter;
import lombok.Setter;
import main.Enums.RequestType;

@Getter
@Setter
public class Request {
    private RequestType requestType;
    private String requestMessage;

    public Request(){}

    public Request(RequestType requestType, String requestMessage) {
        this.requestType = requestType;
        this.requestMessage = requestMessage;
    }
}