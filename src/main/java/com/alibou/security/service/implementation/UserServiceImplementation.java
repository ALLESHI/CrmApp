package com.alibou.security.service.implementation;

import com.alibou.security.dto.AuthenticationResponseDto;
import com.alibou.security.dto.UserResponseDto;
import com.alibou.security.model.Role;
import com.alibou.security.repository.RoleRepository;
import com.alibou.security.security.JwtService;
import com.alibou.security.dto.LoginRequestDto;
import com.alibou.security.dto.UserRegisterDto;
import com.alibou.security.model.User;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;
    private final RoleRepository roleRepository;
    @Override
    public UserResponseDto create(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()){
            throw new IllegalStateException("Email is already taken!");
        }
        User user = dtoToEntity(userRegisterDto);
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        userRepository.save(user);
        return convertToResponseDto(user);
    }

    @Override
    public Optional<UserResponseDto> findById(Integer id) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(this::convertToResponseDto).orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id: " + id)));
    }

    @Override
    public List<UserResponseDto> findAll() {
        return null;
    }

    @Override
    public UserResponseDto update(UserRegisterDto userRegisterDto) {
        return null;
    }

    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public User dtoToEntity(UserRegisterDto userRegisterDto) {
        User user = mapper.map(userRegisterDto, User.class);
        List<Role> roles = userRegisterDto.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found with id " + roleId)))
                .collect(Collectors.toList());
        user.setRoles(roles);
        return user;
    }
    public UserResponseDto convertToResponseDto(User user) {
        UserResponseDto responseDto = mapper.map(user, UserResponseDto.class);
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toList());
        responseDto.setRoleNames(roleNames);
        return responseDto;
    }
}
