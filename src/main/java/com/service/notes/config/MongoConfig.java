package com.service.notes.config;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
@EnableMongock
public class MongoConfig {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
