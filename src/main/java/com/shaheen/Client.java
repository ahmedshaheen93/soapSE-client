package com.shaheen;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class Client {
    private static final String endPoint = "http://www.dneonline.com/calculator.asmx?wsld";

    // read xml ---> string
    private String readXmlRequest(File xmlRequest) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(xmlRequest.getPath()));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendRequest(String request) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            // connection to connect to endpoint
            SOAPConnection connection = soapConnectionFactory.createConnection();

            // to creat soap message
            MessageFactory factory = MessageFactory.newInstance();
            // to add different headers
            MimeHeaders mimeHeaders = new MimeHeaders();

            SOAPMessage message = factory.createMessage(mimeHeaders, new ByteArrayInputStream(request.getBytes()));

            SOAPMessage response = connection.call(message, new URL(endPoint));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.println(strMsg);
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAllRequests() {

        try (Stream<Path> paths = Files.walk(Paths.get("src/main/resources/requests"))) {
            paths
                    .filter(Files::isRegularFile)
                    .map(path -> new File(String.valueOf(path)))
                    .forEach((file) -> {
                        String request = readXmlRequest(file);
                        System.out.println(request);
                        System.out.println("**************************************");
                        System.out.println("**************************************");
                        sendRequest(request);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
