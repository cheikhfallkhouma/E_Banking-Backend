package com.cfk.ebankingbanking.dtos;
import com.cfk.ebankingbanking.entities.Address;
import lombok.Data;
@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private Address address;
}
