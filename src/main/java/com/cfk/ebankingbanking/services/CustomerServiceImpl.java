package com.cfk.ebankingbanking.services;

import com.cfk.ebankingbanking.constant.ExceptionMessages;
import com.cfk.ebankingbanking.dtos.CustomerDTO;
import com.cfk.ebankingbanking.entities.Customer;
import com.cfk.ebankingbanking.exceptions.CustomerDeleteException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;
import com.cfk.ebankingbanking.mappers.BankAccountMapperImpl;
import com.cfk.ebankingbanking.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public List<CustomerDTO> listCustomers() {
        //return customerRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers
                .stream()
                .map(cust-> dtoMapper
                .fromCustomer(cust))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public CustomerDTO getCustomerDTO(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND));
        CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
        return customerDTO;
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating a customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerDTO;
        //return dtoMapper.fromCustomer(savedCustomer);
    }
    @Override
    public void deleteCustomer(Long customerId) throws CustomerDeleteException {
        log.info("Deleting a customer");
       // customerRepository.deleteById(customerId);

        try {
            customerRepository.deleteById(customerId);
        } catch (DataIntegrityViolationException  e) {
            throw new CustomerDeleteException(ExceptionMessages.CUSTOMER_CANT_BE_DELETED_BECAUSE_ITS_ASSOCIATED_WITH_AN_ACCOUNT);
        }
    }


    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        //customer.setAddress(customerDTO.getAddress());
        Customer savedCustomer = customerRepository.save(customer);
        //return customerDTO;
        return dtoMapper.fromCustomer(savedCustomer);
    }

}
