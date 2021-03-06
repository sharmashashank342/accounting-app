package com.accountingapp.model;


import com.accountingapp.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long userId;

    private String userName;

    private String emailAddress;

    @JsonIgnore
    private Status status;

    private Timestamp createdOn;

    private Timestamp modifiedOn;
}
