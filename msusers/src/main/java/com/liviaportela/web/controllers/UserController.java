package com.liviaportela.web.controllers;

import com.liviaportela.entities.User;
import com.liviaportela.exceptions.ExceptionResponse;
import com.liviaportela.security.dto.AuthenticationDto;
import com.liviaportela.security.dto.LoginResponseDto;
import com.liviaportela.security.TokenService;
import com.liviaportela.security.UserDetailsImpl;
import com.liviaportela.services.UserService;
import com.liviaportela.web.dto.UpdatePasswordDto;
import com.liviaportela.web.dto.UserCreateDto;
import com.liviaportela.web.dto.UserResponseDto;
import com.liviaportela.web.dto.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Endpoints to create, update password, and authenticate a user.")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Create a bearer token", description = "Feature to create a bearer token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Token created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                                    UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "User already registered in the system.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid AuthenticationDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @Operation(summary = "Create a new user", description = "Feature to create a new user. Request requires use of a bearer token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                                    UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "User already registered in the system.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Error",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PostMapping(value = "/register")
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto dto) {
        User newUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @Operation(summary = "Update password", description = "Feature to update a user's password. Request requires a Bearer Token.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Password does not match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthenticated user"),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PutMapping(value = "/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        userService.updateUserPassword(updatePasswordDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
