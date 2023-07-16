package com.service.notes.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.notes.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private String id;
    @NotBlank
    private String username;
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String firstName;
    private String secondName;
    private List<Role> roles;

}
