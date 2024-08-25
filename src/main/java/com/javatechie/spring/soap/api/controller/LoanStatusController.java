package com.javatechie.spring.soap.api.controller;

import com.javatechie.spring.soap.api.client.SoapClient;
import com.javatechie.spring.soap.api.loaneligibility.Acknowledgement;
import com.javatechie.spring.soap.api.loaneligibility.CustomerRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanStatusController {

  private final SoapClient soapClient;

  public LoanStatusController(SoapClient soapClient) {
    this.soapClient = soapClient;
  }

  @PostMapping("/getLoanStatus")
  public Acknowledgement invokeSoapClientToGetLoanStatus(@RequestBody CustomerRequest customerRequest) {
    return soapClient.getLoanStatus(customerRequest);
  }

}
