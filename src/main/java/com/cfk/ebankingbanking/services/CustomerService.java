package com.cfk.ebankingbanking.services;

import com.cfk.ebankingbanking.dtos.CustomerDTO;
import com.cfk.ebankingbanking.exceptions.CustomerDeleteException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> listCustomers();

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId) throws CustomerDeleteException;

    CustomerDTO getCustomerDTO(Long customerId) throws CustomerNotFoundException;

    CustomerDTO saveCustomer(CustomerDTO customerDTO);

}
