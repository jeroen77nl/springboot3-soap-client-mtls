package com.javatechie.spring.soap.api.client;

import com.javatechie.spring.soap.api.loaneligibility.Acknowledgement;
import com.javatechie.spring.soap.api.loaneligibility.CustomerRequest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class SoapClient {

  private static final String URL = "http://localhost:8080/ws";

  private final Jaxb2Marshaller jaxb2Marshaller;

  public SoapClient(Jaxb2Marshaller jaxb2Marshaller) {
    this.jaxb2Marshaller = jaxb2Marshaller;
  }

  public Acknowledgement getLoanStatus(CustomerRequest request) {
    var webServiceTemplate = new WebServiceTemplate(jaxb2Marshaller);
    Object response = webServiceTemplate.marshalSendAndReceive(URL, request);
    return (Acknowledgement) response;
  }

}