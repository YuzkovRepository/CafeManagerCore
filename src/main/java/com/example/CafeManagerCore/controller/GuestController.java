package com.example.CafeManagerCore.controller;

import com.example.CafeManagerCore.DTO.GuestResponseDTO;
import com.example.CafeManagerCore.model.Guest;
import com.example.CafeManagerCore.service.GuestService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/guests")
@RequiredArgsConstructor
public class GuestController {
    final private GuestService guestService;

    @GetMapping("/me")
    public ResponseEntity<GuestResponseDTO> getCurrentGuest() {
        Guest guest = guestService.getCurrentGuest();
        return ResponseEntity.ok(mapToGuestResponse(guest));
    }

    private GuestResponseDTO mapToGuestResponse(Guest guest) {
        return new GuestResponseDTO(
                guest.getId(),
                guest.getPhone(),
                guest.getDateRegistration()
        );
    }
}
