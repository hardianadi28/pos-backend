package com.retail.pos.modules.user.usecase;

import com.retail.pos.modules.user.domain.User;

public interface RegisterUserUseCase {
    User execute(RegisterUserCommand command);
}
