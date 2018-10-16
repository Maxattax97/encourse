package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.UserRepository;
import edu.purdue.cs.encourse.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(username);
    }

    public void changeUserEnabled(User user) {
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(userPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public List<User> getDisabledAccounts() {
        return userRepository.findAllByEnabledIsFalse();
    }

    public void deleteAccount(User user) {
        userRepository.delete(user);
    }

    public void deleteAccount(String userName) {
        userRepository.delete(userRepository.findByUsername(userName));
    }

}
