package com.sharmachait.ws.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Entity
public class UserDto {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    private Status status;
}
