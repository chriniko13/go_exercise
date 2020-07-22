package com.assignment.go.ipservice.service;

import com.assignment.go.ipservice.dto.ReserveIpsRequest;
import com.assignment.go.ipservice.entity.IPAddress;
import com.assignment.go.ipservice.entity.IPPool;
import com.assignment.go.ipservice.repository.IPAddressRepository;
import com.assignment.go.ipservice.repository.IPPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class IPManagementServiceTest {

	private IPManagementService service;

	@Mock
	private IPPoolRepository mockedIpPoolRepository;

	@Mock
	private IPAddressRepository mockedIpAddressRepository;

	@BeforeEach
	public void init() {
		service = new IPManagementService(mockedIpPoolRepository, mockedIpAddressRepository);
	}

	@Test
	void reserve_ReserveIpsRequest_works_as_expected_reserved() {

		// given (let's reserve some)
		ReserveIpsRequest req = new ReserveIpsRequest();
		req.setIpPoolId(1L);
		req.setNumberOfIps(10);

		Mockito.when(mockedIpPoolRepository.findById(1L))
				.thenReturn(Optional.of(new IPPool(1L, "some pool 1", 100L, 0L, "10.70.26.1", "10.70.26.100")));

		String[] reservedIpValues = "10.70.26.1 -- 10.70.26.2 -- 10.70.26.3 -- 10.70.26.4 -- 10.70.26.5 -- 10.70.26.6 -- 10.70.26.7 -- 10.70.26.8 -- 10.70.26.9 -- 10.70.26.10".split(" -- ");
		Set<IPAddress> reserved = Arrays.stream(reservedIpValues).map(ipValue -> IPAddress.createReserved(1L, ipValue)).collect(Collectors.toSet());

		Mockito.when(mockedIpAddressRepository.findByIpPoolIdEquals(1L)).thenReturn(
				reserved
		);

		// when
		Set<IPAddress> result = service.reserve(req);

		// then
		assertEquals(10, result.size());
		assertEquals(
				"10.70.26.11 -- 10.70.26.12 -- 10.70.26.13 -- 10.70.26.14 -- 10.70.26.15 -- 10.70.26.16 -- 10.70.26.17 -- 10.70.26.18 -- 10.70.26.19 -- 10.70.26.20"
				,
				result.stream().map(IPAddress::getValue).collect(Collectors.joining(" -- ")));

		Mockito.verify(mockedIpAddressRepository, times(1)).saveAll(Mockito.anyCollection());

	}

	@Test
	void reserve_ReserveIpsRequest_works_as_expected_not_reserved() {

		// given
		ReserveIpsRequest req = new ReserveIpsRequest();
		req.setIpPoolId(1L);
		req.setNumberOfIps(10);

		Mockito.when(mockedIpPoolRepository.findById(1L))
				.thenReturn(Optional.of(new IPPool(1L, "some pool 1", 100L, 0L, "10.70.26.1", "10.70.26.100")));

		Set<IPAddress> reservedOrBlacklistedIps = new LinkedHashSet<>();
		Mockito.when(mockedIpAddressRepository.findByIpPoolIdEquals(1L)).thenReturn(
				reservedOrBlacklistedIps
		);

		// when
		Set<IPAddress> result = service.reserve(req);

		// then
		assertNotNull(result);
		assertEquals(10, result.size());
		assertEquals(
				"10.70.26.1 -- 10.70.26.2 -- 10.70.26.3 -- 10.70.26.4 -- 10.70.26.5 -- 10.70.26.6 -- 10.70.26.7 -- 10.70.26.8 -- 10.70.26.9 -- 10.70.26.10"
				,
				result.stream().map(IPAddress::getValue).collect(Collectors.joining(" -- ")));

		Mockito.verify(mockedIpAddressRepository, times(1)).saveAll(Mockito.anyCollection());

	}

}
