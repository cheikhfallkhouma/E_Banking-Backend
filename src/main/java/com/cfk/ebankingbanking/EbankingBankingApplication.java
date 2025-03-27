package com.cfk.ebankingbanking;

import com.cfk.ebankingbanking.dtos.*;
import com.cfk.ebankingbanking.entities.*;
import com.cfk.ebankingbanking.enums.AccountStatus;
import com.cfk.ebankingbanking.enums.OperationType;
import com.cfk.ebankingbanking.exceptions.BalanceNotSufficientException;
import com.cfk.ebankingbanking.exceptions.BankAccountNotFoundException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;
import com.cfk.ebankingbanking.repositories.AccountOperationRepository;
import com.cfk.ebankingbanking.repositories.AddressRepository;
import com.cfk.ebankingbanking.repositories.BankAccountRepository;
import com.cfk.ebankingbanking.repositories.CustomerRepository;
import com.cfk.ebankingbanking.security.entities.AppRole;
import com.cfk.ebankingbanking.security.entities.AppUser;
import com.cfk.ebankingbanking.security.repositories.AppRoleRepository;
import com.cfk.ebankingbanking.security.repositories.AppUserRepository;
import com.cfk.ebankingbanking.services.BankAccountService;
import com.cfk.ebankingbanking.services.CustomerService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
@Configuration
@OpenAPIDefinition(info = @Info(title = "E_banking API", version = "2.0", description = "E_banking Microservice"))
public class EbankingBankingApplication {

//	@Bean (name="entityManagerFactory")
//	public LocalSessionFactoryBean sessionFactory() {
//		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//		return sessionFactory;
//	}

	public static void main(String[] args) {
		SpringApplication.run(EbankingBankingApplication.class, args);
	}
	@Bean
			CommandLineRunner commandLineRunner (BankAccountService bankAccountService, CustomerService customerService,
												 AppRoleRepository appRoleRepository, AppUserRepository appUserRepository,
												 CustomerRepository customerRepository){
		return args -> {
			Stream.of("Modou","Jean-Claude", "Anne-Marie","Moustapha", "Gabriel").forEach(name->{
				CustomerDTO customerDTO = new CustomerDTO();
				//customerDTO.setAddress(new Address(1L, "33, bvd Gallieni", "Neuilly-Plaisance", "93360","France",null ));
				customerDTO.setName(name);
				customerDTO.setEmail(name + "@gmail.com");
				customerService.saveCustomer(customerDTO);
			});


			AppRole appRole = new AppRole();
			appRole.setId(1L);
			appRole.setRoleName("user1");
			appRoleRepository.save(appRole);

			AppRole appRole1 = new AppRole();
			appRole.setId(2L);
			appRole.setRoleName("user2");
			appRoleRepository.save(appRole1);

			AppRole appRole2 = new AppRole();
			appRole.setId(3L);
			appRole.setRoleName("user3");
			appRoleRepository.save(appRole2);

			AppRole appRole3 = new AppRole();
			appRole.setId(4L);
			appRole.setRoleName("user4");
			appRoleRepository.save(appRole3);

			AppUser appUser = new AppUser();
			appUser.setUserName("gabriel");
		//	appUser.setAppRoles();



			customerService.listCustomers().forEach(customer->{
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000, 9000, customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*90000, 5.5, customer.getId());
					List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
					for (BankAccountDTO bankAccount : bankAccounts) {
						for (int i = 0; i < 10; i++) {
							String accountId;
							if(bankAccount instanceof SavingAccountDTO){
								accountId = ((SavingAccountDTO) bankAccount).getId();
							}else{
								accountId = ((CurrentAccountDTO) bankAccount).getId();
							}

							bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "Credit");
							bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");
						}
					}
				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				} catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
					throw new RuntimeException(e);

				}

			});
		};
	}


	@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository,
							AddressRepository addressRepository,
							BankAccountService bankAccountService,
							CustomerService customerService){
		return args -> {
			Stream.of("Modou","Jean-Claude", "Anne-Marie","Moustapha", "Gabriel" , "Fatou", "David", "Rokhaya", "Aminata").forEach(name->{
				CustomerDTO customer=new CustomerDTO();
				//Address address = new Address(null, "33, bvd Gallieni", "Neuilly-Plaisance", "93360","France");
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				Address address1 = new Address();
				address1.setAddressLabel("33, bvd Gallieni");
				address1.setCity("Neuilly-Plaisance");
				address1.setZipCode("93360");
				address1.setCountry("France");
				addressRepository.save(address1);
				customer.setAddress(address1);
				Address address2 = new Address();
				address2.setAddressLabel("15, rue Louis Pasteur");
				address2.setCity("Paris");
				address2.setZipCode("75012");
				address2.setCountry("France");
				addressRepository.save(address2);
				customer.setAddress(address2);
				customerService.saveCustomer(customer);
			});
			customerService.listCustomers().forEach(customer->{
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});
			List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
			for (BankAccountDTO bankAccount:bankAccounts){
				for (int i = 0; i <10 ; i++) {
					String accountId;
					if(bankAccount instanceof SavingAccountDTO){
						accountId=((SavingAccountDTO) bankAccount).getId();
					} else{
						accountId=((CurrentAccountDTO) bankAccount).getId();
					}
					bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
					bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
				}
			}
		};

	}
}
