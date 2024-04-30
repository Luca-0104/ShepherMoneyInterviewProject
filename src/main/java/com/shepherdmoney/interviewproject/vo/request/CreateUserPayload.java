package com.shepherdmoney.interviewproject.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreateUserPayload {

    private String name;

    @Email(message = "An invalid email format")
    private String email;
}
