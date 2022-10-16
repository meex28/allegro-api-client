package com.example.allegroapiclient.api_client.command_id_manager;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table
public class CommandId implements Persistable<String> {
    @Id
    private String uuid;
    private String username;
    private Date created;
    private Status status;
    private Type type;

    @Transient
    private boolean isNew;

    public CommandId() {
    }

    public CommandId(String uuid, String username, Date created, Status status, Type type) {
        this.uuid = uuid;
        this.username = username;
        this.created = created;
        this.status = status;
        this.type = type;
    }

    public CommandId(String uuid, String username, Date created, Status status, Type type, boolean isNew) {
        this.uuid = uuid;
        this.username = username;
        this.created = created;
        this.isNew = isNew;
        this.status = status;
        this.type = type;
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public boolean isNew(){
        return isNew;
    }

}
