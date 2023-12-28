package com.sp.fc.web.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestWrapper extends HttpServletRequestWrapper {

    private String requestData;

    public RequestWrapper(HttpServletRequest request, String data) throws IOException {
        super(request);
        String originRequestData = requestDataByte(request).replaceAll("[{}]", "");
        requestData = "{" + originRequestData + ","+ data.replaceAll("[{}]", "") + "}";
    }

    private String requestDataByte(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] rawData = StreamUtils.copyToByteArray(inputStream);
        return new String(rawData);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getRequestData() {
        return requestData;
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestData.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    private JsonNode getJsonNode(ServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        String requestBodyString = requestBody.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBodyString);
        return jsonNode;
    }

}
