package de.flubba.rallye.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

@Component
@Slf4j
public class MDNSAdvertiser implements AutoCloseable {
    private final JmDNS jmDNS;

    public MDNSAdvertiser(@Value("${server.port}") int port) {
        try {
            InetAddress ipv4Address = getIPv4Address();
            var byName = InetAddress.getByName(ipv4Address.getHostAddress());
            log.info("Starting mDNS on {}", byName);
            jmDNS = JmDNS.create(byName);

            // Register a service
            ServiceInfo serviceInfo = ServiceInfo.create("_ltc-rallye._tcp.flubba.", "There can only be one!", port, "");
            jmDNS.registerService(serviceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InetAddress getIPv4Address() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        var ipv4s = new ArrayList<Inet4Address>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback()
                    || !networkInterface.isUp()
                    || networkInterface.isVirtual()
                    || networkInterface.getName().startsWith("bridge") // on macOS bridge is virtual, but isVirtual() returns false for some readon
            ) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    ipv4s.add((Inet4Address) address);
                }
            }
        }

        ipv4s.forEach(v4 -> log.info("Found IPv4 address for mDNS: {}", v4.getHostAddress()));

        return ipv4s.stream().findFirst().orElseThrow(() -> new IOException("No IPv4 address found"));
    }


    @Override
    public void close() throws Exception {
        log.info("closing jmDNS");
        jmDNS.close();
    }
}
