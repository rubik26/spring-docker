package com.darpasyan.docker.builders.direct.impl;

import com.darpasyan.docker.builders.direct.DirectBuilder;
import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;

public class DirectBuilderImpl implements DirectBuilder {
    private int id;
    private User sender;
    private User recipient;
    private LocalDate dateOfStart;

    @Override
    public DirectBuilderImpl setSender(User sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public DirectBuilderImpl setRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    @Override
    public DirectBuilderImpl setDateOfStart(LocalDate dateOfStart) {
        this.dateOfStart = dateOfStart;
        return this;
    }

    public Direct build(){
        id++;
        return new Direct(id, sender, recipient, dateOfStart);
    }
}
