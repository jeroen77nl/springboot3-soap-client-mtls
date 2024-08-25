package com.javatechie.spring.soap.api.config;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

@Configuration
public class SoapConfig {
  @Value("${client.ssl.keystore}")
  private String keyStorePath;

  @Value("${client.ssl.keystore-password}")
  private String keyStorePassword;

  @Value("${client.ssl.truststore}")
  private String trustStorePath;

  @Value("${client.ssl.truststore-password}")
  private String trustStorePassword;

  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("com.javatechie.spring.soap.api.loaneligibility");
    return marshaller;
  }

  @Bean
  public WebServiceTemplate webServiceTemplate() throws Exception {
    WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    webServiceTemplate.setMessageSender(httpComponentsMessageSender());
    webServiceTemplate.setMarshaller(marshaller());
    webServiceTemplate.setUnmarshaller(marshaller());
    return webServiceTemplate;
  }

  @Bean
  public HttpComponentsMessageSender httpComponentsMessageSender() throws Exception {
    return new HttpComponentsMessageSender(httpClient());
  }

  @Bean
  public CloseableHttpClient httpClient() throws Exception {
    SSLContext sslContext = SSLContextBuilder.create()
            .loadKeyMaterial(loadKeyStore(keyStorePath, keyStorePassword), keyStorePassword.toCharArray())
            .loadTrustMaterial(loadTrustStore(trustStorePath, trustStorePassword), null)
            .build();

    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

    // Configure request settings
    RequestConfig requestConfig = RequestConfig.custom()
            .setExpectContinueEnabled(false) // Disable 'Expect: 100-Continue' handshake
            .build();

    // Fixes a strange runtime exception 'Content-Length header already present'
    HttpRequestInterceptor contentLengthInterceptor =
            (request, context) -> request.removeHeaders("Content-Length");

    return HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .setDefaultRequestConfig(requestConfig)
            .addInterceptorFirst(contentLengthInterceptor)
            .build();
  }

  private KeyStore loadKeyStore(String path, String password) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    Resource resource = new ClassPathResource(path);
    try (InputStream inputStream = resource.getInputStream()) {
      keyStore.load(inputStream, password.toCharArray());
    }
    return keyStore;
  }

  private KeyStore loadTrustStore(String path, String password) throws Exception {
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//    KeyStore trustStore = KeyStore.getInstance("JKS");
    Resource resource = new ClassPathResource(path);
    try (InputStream inputStream = resource.getInputStream()) {
      trustStore.load(inputStream, password.toCharArray());
    }
    return trustStore;
  }
}
