package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.User;
import com.absat.techshop.TechStore.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UsersRepository usersRepository;

    @Autowired
    public UserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = usersRepository.findByEmailEquals(email);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        return new com.absat.techshop.TechStore.security.UserDetails(user.get());
    }
}
