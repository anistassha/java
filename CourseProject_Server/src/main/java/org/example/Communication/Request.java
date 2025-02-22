package org.example.Communication;

import lombok.Getter;
import lombok.Setter;
import org.example.Enums.RequestType;

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
