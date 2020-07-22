package com.assignment.go.ipservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReserveIpRequest {

	private long ipPoolId;
	private String ipValue;

	public ReserveIpRequest(long ipPoolId) {
		this.ipPoolId = ipPoolId;
	}

}
