package org.example.filetool.TooUtil;

public class ResponseBuilder {

    public static Response buildOkResponse() {
        return new Response("200", "Success");
    }

    public static Response buildOkResponse(Object data) {
        Response response = new Response("200", "Success");
        response.setResultData(data);
        return response;
    }

    public static Response buildOkResponse(String code, String message) {
        Response response = new Response(code, message);
        return response;
    }

    public static Response buildOkResponse(String code, String message, Object data) {
        Response response = new Response(code, message);
        response.setResultData(data);
        return response;
    }

    public static Response buildErrorResponse(String code, String msg) {
        return new Response(code, msg);
    }
    public static Response buildErrorResponse(String code, String msg,Object data) {
        Response response = new Response(code, msg);
        response.setResultData(data);
        return response;
    }
    public static Response buildParameterErrorResponse(String append) {
        return new Response(Response.RES_PARAMETER, Response.RES_PARAMETER_MSG+" : ["+append+"]");
    }

    public static Response buildNotFoundErrorResponse(String append) {
        return new Response(Response.RES_NOT_FOUND, Response.RES_NOT_FOUND_MSG+" : ["+append+"]");
    }

    public static Response buildServerErrorResponse(String append) {
        return new Response(Response.RES_ERROR, Response.RES_ERROR_MSG+" : ["+append+"]");
    }
}