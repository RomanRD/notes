package com.service.notes.config.dbmigrations;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.service.notes.model.Role;
import com.service.notes.persistence.document.User;
import com.service.notes.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@ChangeLog(order = "001")
public class DatabaseInitChangeLog {

    @ChangeSet(order = "001", id = "init_users", author = "roman")
    public void initDepartments(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        User user = new User();
        user.setFirstName("FirstNameUser");
        user.setSecondName("SecondNameUser");
        user.setUsername("userTest");
        user.setPassword(passwordEncoder.encode("userTest"));
        user.setRoles(List.of(Role.USER));

        User admin = new User();
        admin.setFirstName("FirstNameAdmin");
        admin.setSecondName("SecondNameAdmin");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(List.of(Role.ADMIN));

        User userAdmin = new User();
        userAdmin.setFirstName("FirstNameUserAdmin");
        userAdmin.setSecondName("SecondNameUserAdmin");
        userAdmin.setUsername("userAdmin");
        userAdmin.setPassword(passwordEncoder.encode("userAdmin"));
        userAdmin.setRoles(List.of(Role.USER, Role.ADMIN));

        userRepository.saveAll(List.of(user, admin, userAdmin));
    }

}
