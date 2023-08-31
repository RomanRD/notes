package com.service.notes.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.notes.domain.dto.AuthenticationRequestDTO;
import com.service.notes.domain.exception.BruteForceException;
import com.service.notes.model.Role;
import com.service.notes.persistence.document.User;
import com.service.notes.persistence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    static final String CONTROLLER_PATH = "/authenticate";

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BruteForceDefender bruteForceDefender;

    @AfterEach
    void clearTestData(){
        userRepository.deleteAll();
    }

    @Test
    void authenticate_ExpectOkAndJwt() throws Exception{
        final String username = "username";
        final String password = "password";

        User user = new User();
        user.setId("userId");
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(List.of(Role.USER));

        final User saveUser = userRepository.save(user);
        final AuthenticationRequestDTO request = new AuthenticationRequestDTO(username, password);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH)
                        .content(asJsonString(request))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final String jwt = result.getResponse().getContentAsString();
        assertEquals(saveUser.getUsername(), jwtUtil.extractUsername(jwt));

        verify(bruteForceDefender, times(1)).checkCurrentClientIPBlocked();
        verify(bruteForceDefender, times(0)).loginFailed();
    }

    @Test
    void authenticate_ExpectUnauthorizedAndRecordFailedAttempt() throws Exception {
        final AuthenticationRequestDTO request = new AuthenticationRequestDTO("nonExistentUsername", "nonExistentPassword");

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH)
                        .content(asJsonString(request))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        verify(bruteForceDefender, times(1)).checkCurrentClientIPBlocked();
        verify(bruteForceDefender, times(1)).loginFailed();
    }

    @Test
    void authenticate_WhenCredentialsAreCorrectButClientBlockedByBruteForceDefender_ExpectUnauthorized() throws Exception {
        final String username = "username";
        final String password = "password";

        User user = new User();
        user.setId("userId");
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(List.of(Role.USER));

        final AuthenticationRequestDTO request = new AuthenticationRequestDTO(username, password);

        doThrow(BruteForceException.class).when(bruteForceDefender).checkCurrentClientIPBlocked();

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH)
                        .content(asJsonString(request))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        verify(bruteForceDefender, times(1)).checkCurrentClientIPBlocked();
        verify(bruteForceDefender, times(0)).loginFailed();
    }

    private String asJsonString(final Object obj) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

}
