package com.assignment.go.ipservice.service;

import com.assignment.go.ipservice.dto.ReserveIpRequest;
import com.assignment.go.ipservice.dto.ReserveIpsRequest;
import com.assignment.go.ipservice.entity.IPAddress;
import com.assignment.go.ipservice.entity.IPPool;
import com.assignment.go.ipservice.error.InfrastructureException;
import com.assignment.go.ipservice.error.IpBlacklistedException;
import com.assignment.go.ipservice.error.IpReservedException;
import com.assignment.go.ipservice.error.IpValueNotWithinIpPoolRangeException;
import com.assignment.go.ipservice.repository.IPAddressRepository;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IPManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(IPManagementService.class);

	private final IPPoolRepository ipPoolRepository;
	private final IPAddressRepository ipAddressRepository;

	public IPManagementService(IPPoolRepository ipPoolRepository, IPAddressRepository ipAddressRepository) {
		this.ipPoolRepository = ipPoolRepository;
		this.ipAddressRepository = ipAddressRepository;
	}

	@Transactional
	public Set<IPAddress> reserve(ReserveIpsRequest req) {

		long ipPoolId = req.getIpPoolId();
		IPPool ipPool = ipPoolRepository.findById(ipPoolId).orElseThrow(IllegalStateException::new);

		long numberOfIps = req.getNumberOfIps();

		Set<String> reservedOrBlacklistedIpAddressValues = getReservedOrBlacklistedIpValues(ipPool);

		Set<IPAddress> reserved = calculate(ipPoolId, ipPool, numberOfIps, reservedOrBlacklistedIpAddressValues);

		ipPool.increaseUsedCapacity(reserved.size());
		ipAddressRepository.saveAll(reserved);

		return reserved;
	}

	@Transactional
	public void reserve(ReserveIpRequest req) {

		long ipPoolId = req.getIpPoolId();
		String ipValue = req.getIpValue();

		IPPool ipPool = ipPoolRepository.findById(ipPoolId).orElseThrow(IllegalStateException::new);

		if (!ipPool.isInRange(ipValue)) {
			throw new IpValueNotWithinIpPoolRangeException();
		}

		ipAddressRepository.findByValueEquals(ipValue).ifPresent(rec -> {
			switch (rec.getState()) {
				case RESERVED:
					throw new IpReservedException();

				case BLACKLISTED:
					throw new IpBlacklistedException();
			}
		});

		ipPool.increaseUsedCapacity(1);
		ipAddressRepository.save(IPAddress.createReserved(ipPoolId, ipValue));
	}

	// --- infra ---

	private Set<IPAddress> calculate(long ipPoolId, IPPool ipPool, long numberOfIps, Set<String> reservedOrBlacklistedIpAddressValues) {

		Set<IPAddress> result = new LinkedHashSet<>();

		for (long walker = ipPool.getLowerBoundAsNum();
			 walker <= ipPool.getUpperBoundAsNum() && result.size() != numberOfIps;
			 walker++) {

			final String ipValue;
			try {
				ipValue = InetAddress.getByName(String.valueOf(walker)).getHostAddress();
			} catch (Exception e) {
				LOG.error("invalid ip value, message: " + e.getMessage(), e);
				throw new InfrastructureException(e);
			}

			if (reservedOrBlacklistedIpAddressValues.contains(ipValue)) { // O(1)
				continue;
			}

			result.add(IPAddress.createReserved(ipPoolId, ipValue));
		}
		return result;
	}

	private Set<String> getReservedOrBlacklistedIpValues(IPPool ipPool) {
		return ipAddressRepository
				.findByIpPoolIdEquals(ipPool.getId())
				.stream()
				.map(IPAddress::getValue)
				.collect(Collectors.toSet());
	}

}
