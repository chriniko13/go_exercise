package com.assignment.go.ipservice.validator;

import com.assignment.go.ipservice.dto.ReserveIpRequest;
import com.assignment.go.ipservice.error.InvalidIpValueProvidedException;
import com.assignment.go.ipservice.error.InvalidPoolIdProvidedException;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ReserveIpRequestValidator implements Validator<ReserveIpRequest, Void> {

	private final IPPoolRepository ipPoolRepository;

	public ReserveIpRequestValidator(IPPoolRepository ipPoolRepository) {
		this.ipPoolRepository = ipPoolRepository;
	}

	@Transactional(readOnly = true)
	@Override public Void process(ReserveIpRequest input) {

		long ipPoolId = input.getIpPoolId();

		ipPoolRepository.findById(ipPoolId).orElseThrow(InvalidPoolIdProvidedException::new);

		String ipValue = input.getIpValue();

		try {
			InetAddress.getByName(ipValue);
		} catch (UnknownHostException e) {
			throw new InvalidIpValueProvidedException();
		}

		return null;
	}
}
