package com.javatechie.spring.soap.api.client;

import com.javatechie.spring.soap.api.loaneligibility.Acknowledgement;
import com.javatechie.spring.soap.api.loaneligibility.CustomerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class SoapClient {

  private final String serverUrl;
  private final WebServiceTemplate webServiceTemplate;

  public SoapClient(WebServiceTemplate webServiceTemplate, @Value("${server.url}") String serverUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serverUrl = serverUrl;
  }

  public Acknowledgement getLoanStatus(CustomerRequest request) {
    Object response = webServiceTemplate.marshalSendAndReceive(serverUrl, request);
    return (Acknowledgement) response;
  }

}