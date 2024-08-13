package com.uwu.studentregistrationbackend.service;
import com.uwu.studentregistrationbackend.config.JwtUtils;
import com.uwu.studentregistrationbackend.dto.AuthenticationDTO;
import com.uwu.studentregistrationbackend.dto.AuthenticationResponseDTO;
import com.uwu.studentregistrationbackend.dto.RegisterDTO;
import com.uwu.studentregistrationbackend.entity.AcademicYear;
import com.uwu.studentregistrationbackend.entity.OTP;
import com.uwu.studentregistrationbackend.entity.User;
import com.uwu.studentregistrationbackend.entity.UserRoleDetails;
import com.uwu.studentregistrationbackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo repository;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final AcademicYearService academicYearService;
    private final UserRoleDetailsServices userRoleDetailsServices;
    private final RoleServices roleServices;


    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private View error;

    public String register(RegisterDTO request) throws Exception {

        AcademicYear academicYear = academicYearService.getAcademicYearById(request.getAcademicId());
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .contactNo(request.getContactNo())
                .createdDate(LocalDateTime.now())
                .status("NOT_VERIFIED")
                .academicYear(academicYear)
                .build();

        var memberRole = roleServices.getRoleByName("Member");
        if(memberRole != null){
            var savedUser = repository.save(user);
            var userRoleDetails = UserRoleDetails.builder()
                    .user(savedUser)
                    .role(memberRole)
                    .isActive(true)
                    .type(memberRole.getType())
                    .start_date(LocalDateTime.now()).build();
            userRoleDetailsServices.createUserRoleDetails(userRoleDetails);
            var otpCode = otpService.generateOTP();
            LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(5);
            var otp = OTP.builder()
                    .otpCode(otpCode)
                    .exprieDate(expiryDateTime)
                    .user(savedUser).build();

            otpService.createOtp(otp);
            emailService.sendMail(savedUser.getEmail(),"OTP Verification","This is your OTP "+otpCode);
            return "OTP Sent";
        }else{
            throw new Exception("Member Role Not Found");
        }

    }


    public AuthenticationResponseDTO authenticate(AuthenticationDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwtToken;

        if(user.getStatus().equals("VERIFIED")){
            jwtToken = jwtUtils.generateTokenFromUsername(user);
        }else{
            jwtToken = "";
            var otpCode = otpService.generateOTP();
            LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(5);
            var otp = OTP.builder()
                    .otpCode(otpCode)
                    .exprieDate(expiryDateTime)
                    .user(user).build();
            otpService.createOtp(otp);
            emailService.sendMail(user.getEmail(),"OTP Verification","This is your OTP "+otpCode);

        }

        UserRoleDetails userRoleDetails = userRoleDetailsServices.getuserRoleDetails(user,true,"MAIN");
       return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .userRoleDetails(userRoleDetails)
                .build();

    }





}
