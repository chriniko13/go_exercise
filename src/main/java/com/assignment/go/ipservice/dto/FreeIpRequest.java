package com.assignment.go.ipservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FreeIpRequest {

	private long ipPoolId;
	private String ipValue;

}
