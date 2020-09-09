package com.github.sammyvimes.bootnite.config;


import com.github.sammyvimes.bootnite.discovery.TcpDiscoveryConsulIpFinder;
import com.github.sammyvimes.bootnite.model.Employee;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.springdata22.repository.config.EnableIgniteRepositories;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableIgniteRepositories(value = "com.github.sammyvimes.bootnite.repo")
public class IgniteConfig {

    @Bean(name = "igniteInstance")
    public Ignite igniteInstance(Ignite ignite) {
        ignite.active(true);
        return ignite;
    }

    @Bean
    public TcpDiscoveryConsulIpFinder finder(final ConsulDiscoveryClient client,
                                             final ConsulServiceRegistry registry,
                                             final ConsulDiscoveryProperties properties,
                                             @Value("${ignition.disco.host}") final String host,
                                             @Value("${ignition.disco.port}") final int port) {
        return new TcpDiscoveryConsulIpFinder(client, registry, properties, host, port);
    }

    @Bean
    public IgniteConfigurer configurer(final TcpDiscoveryConsulIpFinder finder) {
        return igniteConfiguration -> {
            CacheConfiguration cache = new CacheConfiguration("employeeCache");
            cache.setIndexedTypes(UUID.class, Employee.class);

            cache.setCacheMode(CacheMode.REPLICATED);

            igniteConfiguration.setCacheConfiguration(cache);
            final TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
            tcpDiscoverySpi.setIpFinder(finder);

            igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
        };
    }

}
