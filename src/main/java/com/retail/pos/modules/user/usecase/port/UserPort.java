package com.retail.pos.modules.user.usecase.port;

import com.retail.pos.modules.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    boolean existsByUsername(String username);
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    List<User> findByRoles(Collection<String> roleNames);
}
