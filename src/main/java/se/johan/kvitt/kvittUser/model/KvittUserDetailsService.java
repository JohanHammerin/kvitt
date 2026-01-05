package se.johan.kvitt.kvittUser.model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

@Service
public class KvittUserDetailsService implements UserDetailsService {

    private final KvittUserRepository kvittUserRepository;

    //@Autowired
    public KvittUserDetailsService(KvittUserRepository kvittUserRepository) {
        this.kvittUserRepository = kvittUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        KvittUser kvittUser = kvittUserRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User with username " + username + " Was not found")
                );


        return new KvittUserDetails(kvittUser);
    }
}

