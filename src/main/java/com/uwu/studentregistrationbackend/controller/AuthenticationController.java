package com.uwu.studentregistrationbackend.controller;
import com.uwu.studentregistrationbackend.dto.AuthenticationDTO;
import com.uwu.studentregistrationbackend.dto.AuthenticationResponseDTO;
import com.uwu.studentregistrationbackend.dto.CommonResponseDTO;
import com.uwu.studentregistrationbackend.dto.RegisterDTO;
import com.uwu.studentregistrationbackend.entity.User;
import com.uwu.studentregistrationbackend.service.AuthenticationService;
import com.uwu.studentregistrationbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponseDTO> register(@RequestBody RegisterDTO request) {
        CommonResponseDTO<Integer> commonResponseDTO = new CommonResponseDTO<>();
        try{
            String message = service.register(request);
            commonResponseDTO.setMessage(message);
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.CREATED);
        }catch (Exception e){
            User exists = userService.findUserByEmail(request.getEmail());
            if(exists != null){
                commonResponseDTO.setMessage("Email already exists");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.ALREADY_REPORTED);
            }else{
                commonResponseDTO.setError(e.getMessage());
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }

        }

    }

    @PostMapping("/authenticate")
    public ResponseEntity<CommonResponseDTO> authenticate(@RequestBody AuthenticationDTO request) {
        CommonResponseDTO<AuthenticationResponseDTO> commonResponseDTO = new CommonResponseDTO<>();
        var data = service.authenticate(request);
        commonResponseDTO.setData(data);
        return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);

    }


}
