package com.service.notes.persistence.repository;

import com.service.notes.persistence.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query(value = "{'username': ?0}")
    User findByUsername(String username);

    Boolean existsByUsername(String username);
}
