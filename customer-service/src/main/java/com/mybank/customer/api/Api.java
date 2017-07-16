package com.mybank.customer.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mybank.customer.intercomm.AccountClient;
import com.mybank.customer.model.Account;
import com.mybank.customer.model.Customer;
import com.mybank.customer.model.CustomerType;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class Api {
	
	@Autowired
	private AccountClient accountClient;
	
	protected static Logger logger = LoggerFactory.getLogger(Api.class.getName());
	
	private List<Customer> customers;
	
	public Api() {
		customers = new ArrayList<>();
		customers.add(new Customer(1, "12345", "Adam Kowalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(2, "12346", "Anna Malinowska", CustomerType.COMPANY));
		customers.add(new Customer(3, "12347", "Paweł Michalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(4, "12348", "Karolina Lewandowska", CustomerType.COMPANY));
	}
	
	/*@RequestMapping("/customers/ssn/{ssn}")
	public Customer findBySSN(@PathVariable("ssn") String ssn) {
		logger.info(String.format("Customer.findBySSN(%s)", Integer.parseInt(ssn)));
		Customer c = customers.stream().filter(it -> it.getSsn().equals(ssn)).findFirst().get();
		logger.info(String.format("Customer.findBySSN: %s", Integer.parseInt(ssn)));
		return c;
	}
	*/
	@RequestMapping("/customers")
	public List<Customer> findAll() {
		logger.info("Customer.findAll()");
		System.out.println("customers.size()-----"+customers.size());
		logger.info(String.format("Customer.findAll: ", customers));
		return customers;
	}
	
	
	@RequestMapping("/customers/{id}")
	@HystrixCommand(fallbackMethod="handleExternalServiceDown")
	public Customer findById(@PathVariable("id") String id) {
		Integer uniqId = Integer.parseInt(id);
		logger.info(String.format("Customer.findById(%s)", uniqId.intValue()));
		Customer customer = customers.stream().filter(it -> it.getId().intValue()==uniqId.intValue()).findFirst().get();		 
		List<Account> accounts =  accountClient.getAccounts(uniqId);
		
		customer.setAccounts(accounts);
		logger.info(String.format("Customer.findById: ", customer));
		return customer;
	}
	
	public Customer handleExternalServiceDown(String id)
	{
		Customer customer = new Customer();
		
		return customer;
	}
	
}
