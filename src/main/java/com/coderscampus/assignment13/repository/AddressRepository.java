package com.coderscampus.assignment13.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coderscampus.assignment13.domain.Address;

public interface AddressRepository extends JpaRepository <Address, Long>{

	Address findAddressByUserId(Long userId);
	
}
