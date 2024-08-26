package com.javatechie.spring.soap.api.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

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
        // Load the keystore and truststore
        KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword);
        KeyStore trustStore = loadTrustStore(trustStorePath, trustStorePassword);

        // Create SSLContext using the loaded keystore and truststore
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                .loadTrustMaterial(trustStore, (TrustStrategy) null)
                .build();

        // Create SSLConnectionSocketFactory using the SSLContext
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Create a registry of connection socket factories for supported schemes (http, https)
        Registry<SSLConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<SSLConnectionSocketFactory>create()
                        .register("https", sslSocketFactory)
                        .build();

        // Create a connection manager that uses the SSL connection factory
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        // Set socket configuration if needed
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(60))
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        // Fix for Content-Length header already present error
        HttpRequestInterceptor contentLengthInterceptor = (request, entity, context) -> {
            request.removeHeaders(HTTP.CONTENT_LEN);
        };

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .addRequestInterceptorFirst(contentLengthInterceptor)
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
        Resource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            trustStore.load(inputStream, password.toCharArray());
        }
        return trustStore;
    }
}
