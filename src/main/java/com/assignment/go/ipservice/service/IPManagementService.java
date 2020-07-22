package com.assignment.go.ipservice.service;

import com.assignment.go.ipservice.dto.BlacklistIpRequest;
import com.assignment.go.ipservice.dto.FreeIpRequest;
import com.assignment.go.ipservice.dto.ReserveIpRequest;
import com.assignment.go.ipservice.dto.ReserveIpsRequest;
import com.assignment.go.ipservice.entity.IPAddress;
import com.assignment.go.ipservice.entity.IPAddressState;
import com.assignment.go.ipservice.entity.IPPool;
import com.assignment.go.ipservice.error.InfrastructureException;
import com.assignment.go.ipservice.error.IpBlacklistedException;
import com.assignment.go.ipservice.error.IpReservedException;
import com.assignment.go.ipservice.error.IpValueNotReservedOrBlacklistedException;
import com.assignment.go.ipservice.error.IpValueNotWithinIpPoolRangeException;
import com.assignment.go.ipservice.repository.IPAddressRepository;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Optional;
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

		IPPool ipPool = ifIpInRangeGetPool(ipPoolId, ipValue);

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

	@Transactional
	public void blacklist(BlacklistIpRequest request) {

		long ipPoolId = request.getIpPoolId();
		String ipValue = request.getIpValue();

		IPPool ipPool = ifIpInRangeGetPool(ipPoolId, ipValue);

		Optional<IPAddress> recordHolder = ipAddressRepository.findByValueEquals(ipValue);

		if (recordHolder.isPresent()) {

			IPAddress record = recordHolder.get();
			if (record.getState() == IPAddressState.RESERVED) {
				throw new IpReservedException();
			}

			// Note: we ignore if it is already blacklist (respond ok)

		} else {
			ipPool.increaseUsedCapacity(1);
			ipAddressRepository.save(IPAddress.createBlacklisted(ipPoolId, ipValue));
		}
	}

	@Transactional
	public void free(FreeIpRequest request) {

		long ipPoolId = request.getIpPoolId();
		String ipValue = request.getIpValue();

		IPPool ipPool = ifIpInRangeGetPool(ipPoolId, ipValue);

		Optional<IPAddress> recordHolder = ipAddressRepository.findByValueEquals(ipValue);
		if (recordHolder.isPresent()) {

			Long ipAddressRecordId = recordHolder.get().getId();
			ipAddressRepository.deleteById(ipAddressRecordId);
			ipPool.decreaseUsedCapacity(1);

		} else {
			throw new IpValueNotReservedOrBlacklistedException();
		}

	}

	// --- infra ---

	private Set<IPAddress> calculate(long ipPoolId, IPPool ipPool, long numberOfIps, Set<String> reservedOrBlacklistedIpAddressValues) {

		final Set<IPAddress> result = new LinkedHashSet<>();

		for (long walker = ipPool.getLowerBoundAsNum();
			 walker <= ipPool.getUpperBoundAsNum() && result.size() != numberOfIps;
			 walker++) {

			final String ipValue = numToIpAddress(walker);

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

	private IPPool ifIpInRangeGetPool(long ipPoolId, String ipValue) {
		IPPool ipPool = ipPoolRepository.findById(ipPoolId).orElseThrow(IllegalStateException::new);

		if (!ipPool.isInRange(ipValue)) {
			throw new IpValueNotWithinIpPoolRangeException();
		}
		return ipPool;
	}

	private String numToIpAddress(long walker) {
		final String ipValue;
		try {
			ipValue = InetAddress.getByName(String.valueOf(walker)).getHostAddress();
		} catch (Exception e) {
			LOG.error("invalid ip value, message: " + e.getMessage(), e);
			throw new InfrastructureException(e);
		}
		return ipValue;
	}
}
