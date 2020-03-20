package com.company;
/* ******************************************************************************
 * Copyright (c) 2020. This code follow the GPL v3 license scheme.
 ******************************************************************************/

import java.util.HashMap;

/**
 * La versione originaria e mantenuta è nel progetto RobTexApi
 * è stata copiata qui per semplicità, quando sarà sviluppata la libreria specifica usare quella classe
 */
public enum HttpStatusCodes {
    Informational(1, "Informational"),
    Continue(100, "Continue"),
    Switching_Protocols(101, "Switching Protocols"),
    Processing(102, "Processing"),

    Success(2, "Success"),
    OK(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    Non_authoritative_Information(203, "Non-authoritative Information"),
    No_Content(204, "No Content"),
    Reset_Content(205, "Reset Content"),
    Partial_Content(206, "Partial Content"),
    Multi_Status(207, "Multi-Status"),
    Already_Reported(208, "Already Reported"),
    IM_Used(226, "IM Used"),

    Redirection(3, "Redirection"),
    Multiple_Choices(300, "Multiple Choices"),
    Moved_Permanently(301, "Moved Permanently"),
    Found(302, "Found"),
    See_Other(303, "See Other"),
    Not_Modified(304, "Not Modified"),
    Use_Proxy(305, "Use Proxy"),
    Temporary_Redirect(307, "Temporary Redirect"),
    Permanent_Redirect(308, "Permanent Redirect"),

    Client_Error(4, "Client Error"),
    Bad_Request(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"),
    Payment_Required(402, "Payment Required"),
    Forbidden(403, "Forbidden"),
    Not_Found(404, "Not Found"),
    Method_Not_Allowed(405, "Method Not Allowed"),
    Not_Acceptable(406, "Not Acceptable"),
    Proxy_Authentication_Required(407, "Proxy Authentication Required"),
    Request_Timeout(408, "Request Timeout"),
    Conflict(409, "Conflict"),
    Gone(410, "Gone"),
    Length_Required(411, "Length Required"),
    Precondition_Failed(412, "Precondition Failed"),
    Payload_Too_Large(413, "Payload Too Large"),
    Request_URI_Too_Long(414, "Request-URI Too Long"),
    Unsupported_Media_Type(415, "Unsupported Media Type"),
    Requested_Range_Not_Satisfiable(416, "Requested Range Not Satisfiable"),
    Expectation_Failed(417, "Expectation Failed"),
    Im_a_teapot(418, "I'm a teapot"),
    Misdirected_Request(421, "Misdirected Request"),
    Unprocessable_Entity(422, "Unprocessable Entity"),
    Locked(423, "Locked"),
    Failed_Dependency(424, "Failed Dependency"),
    Upgrade_Required(426, "Upgrade Required"),
    Precondition_Required(428, "Precondition Required"),
    Too_Many_Requests(429, "Too Many Requests"),
    Request_Header_Fields_Too_Large(431, "Request Header Fields Too Large"),
    Connection_Closed_Without_Response(444, "Connection Closed Without Response"),
    Unavailable_For_Legal_Reasons(451, "Unavailable For Legal Reasons"),
    Client_Closed_Request(499, "Client Closed Request"),

    Server_Error(5, "Server Error"),
    Internal_Server_Error(500, "Internal Server Error"),
    Not_Implemented(501, "Not Implemented"),
    Bad_Gateway(502, "Bad Gateway"),
    Service_Unavailable(503, "Service Unavailable"),
    Gateway_Timeout(504, "Gateway Timeout"),
    HTTP_Version_Not_Supported(505, "HTTP Version Not Supported"),
    Variant_Also_Negotiates(506, "Variant Also Negotiates"),
    Insufficient_Storage(507, "Insufficient Storage"),
    Loop_Detected(508, "Loop Detected"),
    Not_Extended(510, "Not Extended"),
    Network_Authentication_Required(511, "Network Authentication Required"),
    Network_Connect_Timeout_Error(599, "Network Connect Timeout Error"),
    UNKNOWN_CODE(-1,"Unknown code");

    private int code;
    private String desc;
    private String codeText;

    private final static java.util.Map<Integer, HttpStatusCodes> CONVERSION_MAP = new HashMap<>();
    static {
        for (HttpStatusCodes type : HttpStatusCodes.values()) {
            CONVERSION_MAP.put(type.code,type);
        }
    }

    public String getCodeAsText() {
        return codeText;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    HttpStatusCodes(int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.codeText=Integer.toString(code);
    }

    public static HttpStatusCodes intToHttpStatusCode(int code){
        HttpStatusCodes type = CONVERSION_MAP.get(code);
        if (type == null)
            return HttpStatusCodes.UNKNOWN_CODE;
        return type;
//        for (HttpStatusCodes type : HttpStatusCodes.values()){
//            if (type.getCode() == code){
//                return type;
//            }
//        }
//        return HttpStatusCodes.UNKNOWN_CODE;
    }

}
