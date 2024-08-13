package com.uwu.studentregistrationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AssignPolicyDTO {
    private int roleId;
    private Integer[] policies;
}
