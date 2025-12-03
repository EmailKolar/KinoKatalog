package com.example.kinokatalog.controller;

import com.example.kinokatalog.dto.UserDTO;
import com.example.kinokatalog.service.ProfilePhotoService;
import com.example.kinokatalog.service.impl.UserServiceSqlImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceSqlImpl userService;
    private final ProfilePhotoService profilePhotoService;

    // GET /api/users/me -> returns the authenticated user's DTO (by id from token)
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        // try to read userId from authentication details (set by JwtAuthenticationFilter)
        Object details = authentication.getDetails();
        try {
            if (details instanceof Integer) {
                Integer id = (Integer) details;
                UserDTO dto = userService.getUserById(id);
                return ResponseEntity.ok(dto);
            }
        } catch (Exception ignored) {}

        // fallback: use username -> map to DTO
        try {
            UserDTO dto = userService.getUserByUsername(authentication.getName());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/users/{id} -> only allow the account owner to fetch their data
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Integer id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        // obtain principal id from details if present
        Object details = authentication.getDetails();
        Integer principalId = null;
        if (details instanceof Integer) principalId = (Integer) details;

        // if detail missing, try to resolve principal id via username -> service lookup
        if (principalId == null) {
            try {
                UserDTO me = userService.getUserByUsername(authentication.getName());
                principalId = me.getId();
            } catch (RuntimeException ex) {
                return ResponseEntity.status(401).build();
            }
        }

        if (!principalId.equals(id)) return ResponseEntity.status(403).build();

        try {
            UserDTO dto = userService.getUserById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws Exception {

        if (authentication == null) return ResponseEntity.status(401).build();

        // enforce "can only update yourself"
        Object details = authentication.getDetails();
        Integer principalId = (details instanceof Integer) ? (Integer) details : null;

        if (principalId == null || !principalId.equals(id))
            return ResponseEntity.status(403).body("Not allowed");

        String key = profilePhotoService.upload(file, Long.valueOf(id));
        userService.updateProfileImageKey(id, key);

        return ResponseEntity.ok("Profile photo updated");
    }

    // PRESIGN endpoint - client sends metadata, server returns uploadUrl + objectKey
    @PostMapping("/{id}/profile-image/presign")
    public ResponseEntity<?> presignProfileImage(
            @PathVariable Integer id,
            @RequestBody PresignRequest req,
            Authentication authentication
    ) throws Exception {
        if (authentication == null) return ResponseEntity.status(401).build();

        Object details = authentication.getDetails();
        Integer principalId = (details instanceof Integer) ? (Integer) details : null;
        if (principalId == null) {
            // fallback resolve by username
            try {
                UserDTO me = userService.getUserByUsername(authentication.getName());
                principalId = me.getId();
            } catch (RuntimeException ex) {
                return ResponseEntity.status(401).build();
            }
        }
        if (!principalId.equals(id)) return ResponseEntity.status(403).build();

        Map<String, String> res = profilePhotoService.presignUpload(id, req.filename, req.contentType, req.size);
        return ResponseEntity.ok(res);
    }

    // CONFIRM endpoint - client asks server to validate and promote uploaded object
    @PostMapping("/{id}/profile-image/confirm")
    public ResponseEntity<?> confirmProfileImage(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) throws Exception {
        if (authentication == null) return ResponseEntity.status(401).build();

        Object details = authentication.getDetails();
        Integer principalId = (details instanceof Integer) ? (Integer) details : null;
        if (principalId == null) {
            try {
                UserDTO me = userService.getUserByUsername(authentication.getName());
                principalId = me.getId();
            } catch (RuntimeException ex) {
                return ResponseEntity.status(401).build();
            }
        }
        if (!principalId.equals(id)) return ResponseEntity.status(403).build();

        String objectKey = body.get("objectKey");
        if (objectKey == null) return ResponseEntity.badRequest().body("objectKey required");

        String finalKey = profilePhotoService.confirmAndPromote(id, objectKey);
        userService.updateProfileImageKey(id, finalKey);

        UserDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    // ...existing upload endpoint and others...

    // DTO for presign request
    public static class PresignRequest {
        public String filename;
        public String contentType;
        public Long size;
    }

}