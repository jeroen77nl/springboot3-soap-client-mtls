package com.javatechie.spring.soap.api.config;

import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponents5MessageSender;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

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
  public WebServiceTemplate webServiceTemplate()
      throws UnrecoverableKeyException, CertificateException, IOException,
      KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

    WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    var client = httpClient();

    HttpComponents5MessageSender messageSender = new HttpComponents5MessageSender();
    messageSender.setHttpClient(client);

    webServiceTemplate.setMessageSender(messageSender);
    webServiceTemplate.setMarshaller(marshaller());
    webServiceTemplate.setUnmarshaller(marshaller());
    return webServiceTemplate;
  }

  @Bean
  public CloseableHttpClient httpClient()
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

    KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword);
    KeyStore trustStore = loadTrustStore(trustStorePath, trustStorePassword);

    SSLContext sslContext = SSLContexts.custom()
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .loadTrustMaterial(trustStore, null)
        .build();

    final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
        .setSslContext(sslContext)
        .build();

    final HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        .setSSLSocketFactory(sslSocketFactory)
        .setDefaultTlsConfig(TlsConfig.custom()
            .setHandshakeTimeout(Timeout.ofSeconds(30))
            .setSupportedProtocols(TLS.V_1_3)
            .build())
        .build();

    HttpRequestInterceptor contentLengthInterceptor =
        (request, entity, context) -> request.removeHeaders("Content-Length");

    return HttpClients.custom()
        .setConnectionManager(connectionManager)
        .addRequestInterceptorFirst(contentLengthInterceptor)
        .build();
  }

  private KeyStore loadKeyStore(String path, String password)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    Resource resource = new ClassPathResource(path);
    try (InputStream inputStream = resource.getInputStream()) {
      keyStore.load(inputStream, password.toCharArray());
    }
    return keyStore;
  }

  private KeyStore loadTrustStore(String path, String password)
      throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    Resource resource = new ClassPathResource(path);
    try (InputStream inputStream = resource.getInputStream()) {
      trustStore.load(inputStream, password.toCharArray());
    }
    return trustStore;
  }
}
