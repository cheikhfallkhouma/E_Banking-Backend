package com.cfk.ebankingbanking.web;

import com.cfk.ebankingbanking.constant.ExceptionMessages;
import com.cfk.ebankingbanking.dtos.CustomerDTO;
import com.cfk.ebankingbanking.exceptions.CustomerDeleteException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;
import com.cfk.ebankingbanking.services.BankAccountService;
import com.cfk.ebankingbanking.services.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
@Api(tags = "Customer", description = "API for customers")

public class CustomerRestController {
    private BankAccountService bankAccountService;
    private CustomerService customerService;
    //private AccountOperationService accountOperationService;

    //ResponseEntity<CustomerDTO> = null;

    @ApiOperation("Get all customers")
    @GetMapping("/customers")
   // @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<CustomerDTO> customers(){
        return customerService.listCustomers();
    }

    @GetMapping("customers/search")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<CustomerDTO> searchCustomers(@RequestParam (name = "keyword", defaultValue = "") String keyword){
        return bankAccountService.searchCustomersByName(keyword);
    }

    @ApiOperation("Récupérer un customer par ID")
    @GetMapping("customers/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public CustomerDTO getCustomerDTO( @ApiParam(value = "ID de l'exemple à récupérer", required = true) @PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return customerService.getCustomerDTO(customerId);
    }
    @PostMapping("/customers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
      return customerService.saveCustomer(customerDTO);
      //return customerDTO;
    }
    @PutMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return customerService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteCustomer(@PathVariable Long id) throws CustomerDeleteException {

        try {
            customerService.deleteCustomer(id);

        } catch (DataIntegrityViolationException e) {
            throw new CustomerDeleteException(ExceptionMessages.CUSTOMER_CANT_BE_DELETED_BECAUSE_ITS_ASSOCIATED_WITH_AN_ACCOUNT);
        }
    }
}
