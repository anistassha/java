package main.TCP;

import lombok.Getter;
import lombok.Setter;
import main.Enums.ResponseStatus;

@Getter
@Setter
public class Response {
    private ResponseStatus responseStatus;
    private String responseMessage;
    private String responseData;

    public Response(){}

    public Response(ResponseStatus responseStatus, String responseMessage, String responseData) {
        this.responseStatus = responseStatus;
        this.responseMessage = responseMessage;
        this.responseData = responseData;
    }
}

