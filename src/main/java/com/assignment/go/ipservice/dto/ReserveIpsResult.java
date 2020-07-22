package com.assignment.go.ipservice.dto;

import com.assignment.go.ipservice.entity.IPAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveIpsResult {

	private Set<IPAddress> reservedIpAddresses;

}
