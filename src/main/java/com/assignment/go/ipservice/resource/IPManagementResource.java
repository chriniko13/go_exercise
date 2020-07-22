package com.assignment.go.ipservice.resource;

import com.assignment.go.ipservice.dto.BlacklistIpRequest;
import com.assignment.go.ipservice.dto.FreeIpRequest;
import com.assignment.go.ipservice.dto.ReserveIpRequest;
import com.assignment.go.ipservice.dto.ReserveIpsRequest;
import com.assignment.go.ipservice.dto.ReserveIpsResult;
import com.assignment.go.ipservice.entity.IPAddress;
import com.assignment.go.ipservice.service.IPManagementService;
import com.assignment.go.ipservice.validator.ReserveIpRequestValidator;
import com.assignment.go.ipservice.validator.ReserveIpsRequestValidator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class IPManagementResource {

	private final ReserveIpsRequestValidator reserveIpsRequestValidator;
	private final ReserveIpRequestValidator reserveIpRequestValidator;
	private final IPManagementService ipManagementService;

	public IPManagementResource(IPManagementService ipManagementService, ReserveIpsRequestValidator reserveIpsRequestValidator, ReserveIpRequestValidator reserveIpRequestValidator) {
		this.ipManagementService = ipManagementService;
		this.reserveIpsRequestValidator = reserveIpsRequestValidator;
		this.reserveIpRequestValidator = reserveIpRequestValidator;
	}

	@RequestMapping(path = "ip-addresses", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<ReserveIpsResult> reserveIps(@RequestBody ReserveIpsRequest request) {

		reserveIpsRequestValidator.process(request);
		Set<IPAddress> reservedIps = ipManagementService.reserve(request);
		ReserveIpsResult result = new ReserveIpsResult(reservedIps);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@RequestMapping(path = "ip-address/reserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Void> reserveIp(@RequestBody ReserveIpRequest request) {
		reserveIpRequestValidator.process(request);
		ipManagementService.reserve(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	@RequestMapping(path = "ip-address/blacklist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Void> blacklistIp(@RequestBody BlacklistIpRequest request) {
		reserveIpRequestValidator.process(new ReserveIpRequest(request.getIpPoolId()));
		ipManagementService.blacklist(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	@RequestMapping(path = "ip-address/free", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Void> freeIp(@RequestBody FreeIpRequest request) {
		reserveIpRequestValidator.process(new ReserveIpRequest(request.getIpPoolId()));
		ipManagementService.free(request);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}
