package de.flubba.rallye.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

@Component
@Slf4j
public class MDNSAdvertiser implements AutoCloseable {
    private final JmDNS jmDNS;

    public MDNSAdvertiser(@Value("${server.port}") int port) {
        try {
            jmDNS = JmDNS.create();

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create("_ltc-rallye._tcp.local.", "There can only be one!", port, "");
            jmDNS.registerService(serviceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        log.info("closing jmDNS");
        jmDNS.close();
    }
}
