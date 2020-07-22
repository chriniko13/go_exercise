package com.assignment.go.ipservice.repository;

import com.assignment.go.ipservice.entity.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long> {

	Set<IPAddress> findByIpPoolIdEquals(long ipPoolId);

}
