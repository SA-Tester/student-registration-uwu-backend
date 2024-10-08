package com.uwu.studentregistrationbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uwu.studentregistrationbackend.entity.UserRoleDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthenticationResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("permission")
    private UserRoleDetails userRoleDetails;
}
