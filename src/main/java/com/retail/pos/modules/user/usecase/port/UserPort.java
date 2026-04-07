package com.retail.pos.modules.user.usecase.port;

import com.retail.pos.modules.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    boolean existsByUsername(String username);
    User save(User user);
    Optional<User> findById(UUID id);
}
