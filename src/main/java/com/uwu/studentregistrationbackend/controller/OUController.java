package com.uwu.studentregistrationbackend.controller;
import com.uwu.studentregistrationbackend.dto.CommonResponseDTO;
import com.uwu.studentregistrationbackend.entity.OU;
import com.uwu.studentregistrationbackend.entity.User;
import com.uwu.studentregistrationbackend.entity.UserRoleDetails;
import com.uwu.studentregistrationbackend.service.OUService;
import com.uwu.studentregistrationbackend.service.UserRoleDetailsServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/ou")
public class OUController {
    @Autowired
    public final OUService ouService;

    @Autowired
    private UserRoleDetailsServices userRoleDetailsServices;

    public OUController(OUService ouService) {
        this.ouService = ouService;
    }

    @PostMapping
    public ResponseEntity<CommonResponseDTO> addOu(HttpServletRequest request, @RequestBody OU ou) {
        CommonResponseDTO<OU> commonResponseDTO = new CommonResponseDTO<>();
        User user = (User) request.getAttribute("user");
        UserRoleDetails userRoleDetails = userRoleDetailsServices.getuserRoleDetails(user, true, "MAIN");
        boolean isOtherPolicyAvailable = userRoleDetailsServices.isPolicyAvailable(userRoleDetails, "OTHER");
        if (isOtherPolicyAvailable) {
            try {
                OU newOu = ouService.createOU(ou);
                commonResponseDTO.setData(newOu);
                commonResponseDTO.setMessage("Successfully OU Added");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.CREATED);
            } catch (Exception e) {
                commonResponseDTO.setMessage("Failed to add OU");
                commonResponseDTO.setError(e.getMessage());
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } else {
            commonResponseDTO.setMessage("No Authority to Add OU");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.UNAUTHORIZED);
        }

    }

    @PutMapping
    public ResponseEntity<CommonResponseDTO> updateOu(HttpServletRequest request, @RequestBody OU ou) {
        CommonResponseDTO<OU> commonResponseDTO = new CommonResponseDTO<>();
        User user = (User) request.getAttribute("user");
        UserRoleDetails userRoleDetails = userRoleDetailsServices.getuserRoleDetails(user, true, "MAIN");
        boolean isOtherPolicyAvailable = userRoleDetailsServices.isPolicyAvailable(userRoleDetails, "OTHER");
        if (isOtherPolicyAvailable) {
            try {
                String message = ouService.updateOU(ou);
                commonResponseDTO.setData(ou);
                commonResponseDTO.setMessage(message);
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);
            } catch (Exception e) {
                commonResponseDTO.setMessage("Failed to Edit OU");
                commonResponseDTO.setError(e.getMessage());
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } else {
            commonResponseDTO.setMessage("No Authority to Edit OU");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping(value = "/getOus")
    public ResponseEntity<CommonResponseDTO> getAllOus(HttpServletRequest request) {
        CommonResponseDTO<List<OU>> commonResponseDTO = new CommonResponseDTO<>();
        User user = (User) request.getAttribute("user");
        UserRoleDetails userRoleDetails = userRoleDetailsServices.getuserRoleDetails(user, true, "MAIN");
        boolean isAllPOlicyAvailable = userRoleDetailsServices.isPolicyAvailable(userRoleDetails, "EXCOM_ALL");

        if (isAllPOlicyAvailable) {
            try {
                List<OU> data = ouService.getAllOUs();
                commonResponseDTO.setData(data);
                commonResponseDTO.setMessage("Successfully retrieved Ous");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);

            } catch (Exception e) {
                commonResponseDTO.setError(e.getMessage());
                commonResponseDTO.setMessage("Failed to retrieve Ous");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        } else {
            try {

                List<OU> ouList =ouService.getAllOUsByUser(user);
                commonResponseDTO.setData(ouList);

                commonResponseDTO.setMessage("Successfully retrieved Ous sep");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);

            } catch (Exception e) {
                commonResponseDTO.setError(e.getMessage());
                commonResponseDTO.setMessage("Failed to retrieve Ous");
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping(value = "/deleteOU/{ouID}")
    public ResponseEntity<CommonResponseDTO> deleteOu(HttpServletRequest request, @PathVariable int ouID) {
        CommonResponseDTO<OU> commonResponseDTO = new CommonResponseDTO<>();
        User user = (User) request.getAttribute("user");
        UserRoleDetails userRoleDetails = userRoleDetailsServices.getuserRoleDetails(user, true, "MAIN");
        boolean isOtherPolicyAvailable = userRoleDetailsServices.isPolicyAvailable(userRoleDetails, "OTHER");
        if (isOtherPolicyAvailable) {
            try {
                String message = ouService.deleteOU(ouID);
                commonResponseDTO.setMessage(message);
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.OK);
            } catch (Exception e) {
                commonResponseDTO.setMessage("Failed to Delete OU");
                commonResponseDTO.setError(e.getMessage());
                return new ResponseEntity<>(commonResponseDTO, HttpStatus.BAD_REQUEST);
            }

        } else {
            commonResponseDTO.setMessage("No Authority to Delete OU");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.UNAUTHORIZED);
        }
    }

}
