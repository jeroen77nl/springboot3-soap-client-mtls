package com.javatechie.spring.soap.api.client;

import com.javatechie.spring.soap.api.loaneligibility.Acknowledgement;
import com.javatechie.spring.soap.api.loaneligibility.CustomerRequest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class SoapClient {

  private static final String URL = "https://localhost:8443/ws";

  private final WebServiceTemplate webServiceTemplate;

  public SoapClient(WebServiceTemplate webServiceTemplate) {
    this.webServiceTemplate = webServiceTemplate;
  }

  public Acknowledgement getLoanStatus(CustomerRequest request) {
    Object response = webServiceTemplate.marshalSendAndReceive(URL, request);
    return (Acknowledgement) response;
  }

}