package com.assignment.go.ipservice.repository;

import com.assignment.go.ipservice.entity.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long> {

}
