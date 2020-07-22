package com.assignment.go.ipservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReserveIpsRequest {

	private long numberOfIps;

	private long ipPoolId;
}
