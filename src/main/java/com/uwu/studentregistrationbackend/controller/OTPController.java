package com.uwu.studentregistrationbackend.controller;
import com.uwu.studentregistrationbackend.config.JwtUtils;
import com.uwu.studentregistrationbackend.dto.AuthenticationResponseDTO;
import com.uwu.studentregistrationbackend.dto.CommonResponseDTO;
import com.uwu.studentregistrationbackend.dto.OtpCheckDTO;
import com.uwu.studentregistrationbackend.entity.OTP;
import com.uwu.studentregistrationbackend.entity.User;
import com.uwu.studentregistrationbackend.service.EmailService;
import com.uwu.studentregistrationbackend.service.OTPService;
import com.uwu.studentregistrationbackend.service.UserRoleDetailsServices;
import com.uwu.studentregistrationbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("api/v1/auth")
public class OTPController {

    @Autowired
    private final OTPService otpService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleDetailsServices userRoleDetailsServices;

    public OTPController(OTPService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("otp/check")
    public ResponseEntity<CommonResponseDTO> checkPolicy(@RequestBody OtpCheckDTO otp) {
        CommonResponseDTO<AuthenticationResponseDTO> commonResponseDTO = new CommonResponseDTO<>();
        User user = userService.findUserByEmail(otp.getEmail());
        OTP dbOtp = otpService.findOtpByuser(user);
        if (dbOtp != null && dbOtp.getOtpCode().equals(otp.getOtp())) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (dbOtp.getExprieDate().isAfter(currentDateTime)) {
                otpService.deleteOTP(dbOtp);
                user.setStatus("VERIFIED");
                userService.saveUser(user);
                otpService.deleteOTP(dbOtp);
                String jwtToken = jwtUtils.generateTokenFromUsername(user);
                var userroles = userRoleDetailsServices.getuserRoleDetails(user, true, "MAIN");
                var data = AuthenticationResponseDTO.builder()
                        .accessToken(jwtToken)
                        .userRoleDetails(userroles).build();
                commonResponseDTO.setMessage("OTP verified");
                commonResponseDTO.setData(data);
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);
            } else {
                commonResponseDTO.setMessage("OTP has expired");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } else {
            commonResponseDTO.setMessage("Invalid OTP");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("otp/send")
    public ResponseEntity<CommonResponseDTO> sentOTP(@RequestBody OtpCheckDTO otp) {
        CommonResponseDTO<String> commonResponseDTO = new CommonResponseDTO<>();
        try {
            User user = userService.findUserByEmail(otp.getEmail());
            try {
                var otpCode = otpService.generateOTP();
                LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(5);
                var existOTP = otpService.findOtpByuser(user);
                if (existOTP != null) {
                    existOTP.setOtpCode(otpCode);
                    existOTP.setExprieDate(expiryDateTime);
                    otpService.createOtp(existOTP);
                } else {
                    var newotp = OTP.builder()
                            .otpCode(otpCode)
                            .exprieDate(expiryDateTime)
                            .user(user).build();
                    otpService.createOtp(newotp);
                }

                emailService.sendMail(user.getEmail(), "OTP Verification", "This is your OTP " + otpCode);
                commonResponseDTO.setMessage("OTP Sent");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);
            } catch (Exception e) {
                commonResponseDTO.setMessage(e.getMessage());
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            commonResponseDTO.setMessage("Email not found");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.NOT_FOUND);
        }

    }
}
