package com.uwu.studentregistrationbackend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int policyID;

    @Column(nullable = false)
    private String policy;

    @Column(nullable = false)
    private String type;

    @Column(unique = true,nullable = false)
    private String policyCode;


}
