package com.ams.booking;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

import com.ams.booking.entity.Inventory;
import com.ams.booking.repository.InventoryRepository;

@SpringBootApplication
@EnableCircuitBreaker
public class BookingServiceApplication extends WebSecurityConfigurerAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceApplication.class);

    @Autowired
    InventoryRepository inventoryRepository;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    public static void main(String[] args)
    {
        SpringApplication.run(BookingServiceApplication.class,
                              args);
    }

    @Bean
    CommandLineRunner init()
    {
        return (args) ->
        {
            inventoryRepository.deleteAll();
            logger.info("Creating Inventory Repository data");
            Inventory[] invs = { new Inventory("BF100", "22-JAN-16", 100),
                    new Inventory("BF101", "22-JAN-16", 100),
                    new Inventory("BF102", "22-JAN-16", 100),
                    new Inventory("BF103", "22-JAN-16", 100),
                    new Inventory("BF104", "22-JAN-16", 100),
                    new Inventory("BF105", "22-JAN-16", 100),
                    new Inventory("BF106", "22-JAN-16", 100) };
            Arrays.asList(invs)
                    .forEach(inventory -> inventoryRepository.save(inventory));

        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .httpBasic()
                .disable()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

}
