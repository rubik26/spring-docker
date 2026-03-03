package com.darpasyan.docker.builders.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;

public interface DirectBuilder {
    DirectBuilder setSender(User sender);
    DirectBuilder setRecipient(User recipient);
    DirectBuilder setDateOfStart(LocalDate dateOfStart);
}
