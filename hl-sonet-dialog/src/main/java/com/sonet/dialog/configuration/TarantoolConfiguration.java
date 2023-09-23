package com.sonet.dialog.configuration;


import io.tarantool.driver.api.TarantoolClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.tarantool.config.AbstractTarantoolDataConfiguration;
import io.tarantool.driver.api.TarantoolClient;

@Configuration
public class TarantoolConfiguration extends AbstractTarantoolDataConfiguration {

    @Value("${spring.tarantool.host}")
    protected String host;

    @Value("${spring.tarantool.port}")
    protected int port;

    @Value("${spring.tarantool.username}")
    protected String username;

    @Value("${spring.tarantool.password}")
    protected String password;

    @Bean
    public TarantoolClient tarantoolClient() {
        return TarantoolClientFactory.createClient()
                // If any addresses or an address provider are not specified,
                // the default host 127.0.0.1 and port 3301 are used
                .withAddress(host, port)
                // For connecting to a Cartridge application, use the value of cluster_cookie parameter in the init.lua file
                .withCredentials(username, password)
                // you may also specify more client settings, such as:
                // timeouts, number of connections, custom MessagePack entities to Java objects mapping, etc.
                .build();
    }
}
