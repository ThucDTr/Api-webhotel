package com.hotel.webhotel.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtRespone {
    private Long id;
    private String email;
    private String token;
    private String type = "Bearer";
    private List<String> roles;

    public JwtRespone(Long id, String email, String token, List<String> roles){
        this.id = id;
        this.email = email;
        this.token = token;
        this.roles = roles;
    }
}
