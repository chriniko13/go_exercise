package com.assignment.go.ipservice.validator;

import com.assignment.go.ipservice.dto.ReserveIpsRequest;
import com.assignment.go.ipservice.entity.IPPool;
import com.assignment.go.ipservice.error.InvalidPoolIdProvidedException;
import com.assignment.go.ipservice.error.NotAvailableIpResourcesException;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReserveIpsRequestValidator implements Validator<ReserveIpsRequest, Void> {

	private final IPPoolRepository ipPoolRepository;

	public ReserveIpsRequestValidator(IPPoolRepository ipPoolRepository) {
		this.ipPoolRepository = ipPoolRepository;
	}

	@Transactional(readOnly = true)
	@Override public Void process(ReserveIpsRequest input) {

		long ipPoolId = input.getIpPoolId();

		IPPool ipPool = ipPoolRepository.findById(ipPoolId).orElseThrow(InvalidPoolIdProvidedException::new);

		long numberOfIps = input.getNumberOfIps();
		if (numberOfIps > (ipPool.getTotalCapacity() - ipPool.getUsedCapacity())) {
			throw new NotAvailableIpResourcesException();
		}

		return null;
	}
}
