package com.liviaportela.services;

import com.liviaportela.entities.Address;
import com.liviaportela.entities.User;
import com.liviaportela.exceptions.*;
import com.liviaportela.feign.AddressFeign;
import com.liviaportela.repositories.UserRepository;
import com.liviaportela.security.UserDetailsImpl;
import com.liviaportela.web.dto.AddressResponseDto;
import com.liviaportela.web.dto.UpdatePasswordDto;
import com.liviaportela.web.dto.UserCreateDto;
import com.liviaportela.web.dto.mapper.UserMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final AddressFeign feign;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public User createUser(UserCreateDto dto) {
        try {
            if (repository.findByUsername(dto.getUsername())
                    .isPresent()) throw new UserAlreadyRegisteredException("This user already exists.");
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            User newUser = UserMapper.toUser(dto);
            newUser.setAddress(getAddressByCep(dto.getCep()));
            String message = String.format("%s, CREATE", dto.getUsername());
            System.out.println("Message sent: " + message);
            kafkaTemplate.send("notify", message);
            return repository.save(newUser);
        } catch (FeignException.BadRequest e) {
            throw new InvalidCepException("Invalid CEP provided.");
        }
    }

    @Transactional
    public void updateUserPassword(UpdatePasswordDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        User existentUser = repository.findByUsername(dto.getUsername())
                .orElseThrow((() -> new NotFoundException("User not found with username: " + dto.getUsername())));
        if (!authenticatedUsername.equals(existentUser.getEmail())) {
            throw new UnauthorizedException("You are not authorized to change another user's password.");
        }
        if (passwordEncoder.matches(dto.getOldPassword(), existentUser.getPassword())) {
            existentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            String message = String.format("%s, UPDATE", dto.getUsername());
            System.out.println("Message sent: " + message);
            kafkaTemplate.send("notify", message);
            repository.save(existentUser);
        } else {
            throw new PasswordDoesNotMatchException("The password does not match.");
        }
    }

    @Transactional(readOnly = true)
    public Address getAddressByCep(String cep) {
        AddressResponseDto response = feign.searchAddressCep(cep);
        Address address = new Address();
        address.setZipCode(response.getZipCode());
        address.setStreet(response.getStreet());
        address.setComplement(response.getComplement());
        address.setNeighborhood(response.getNeighborhood());
        address.setCity(response.getCity());
        address.setState(response.getState());
        return address;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return new UserDetailsImpl(user);
    }
}
