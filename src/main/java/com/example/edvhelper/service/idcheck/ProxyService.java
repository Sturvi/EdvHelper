package com.example.edvhelper.service.idcheck;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProxyService {
    private RotatingQueue<WebClient> webClients;


    public WebClient getWebClient (){
        return webClients.poll();
    }

    @PostConstruct
    private void init() {
        webClients = new RotatingQueue<>();
        webClients.add(WebClient.create());

        loadIpAddresses().forEach(proxyAddress -> webClients.add(createWebClientWithProxy(proxyAddress.getIp(), proxyAddress.getPort())));
    }

    private List<ProxyAddress> loadIpAddresses() {
        List<ProxyAddress> proxyAddresses = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("ip-addresses.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String ip = parts[0].trim();
                    Integer port = Integer.parseInt(parts[1].trim());
                    proxyAddresses.add(new ProxyAddress(ip, port));
                }
            }
            reader.close();
        } catch (IOException e) {
            log.error("Error loading IP addresses", e);
        }
        return proxyAddresses;
    }


    private WebClient createWebClientWithProxy(String ip, Integer port) {
        int timeoutInMilliseconds = 4000; // 4 seconds

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutInMilliseconds)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeoutInMilliseconds, TimeUnit.MILLISECONDS))
                )
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                        .host(ip)
                        .port(port));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }



    @Getter
    @AllArgsConstructor
    private class ProxyAddress {
        private String ip;
        private Integer port;
    }
}
