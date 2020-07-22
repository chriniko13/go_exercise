package com.assignment.go.ipservice.resource;

import com.assignment.go.ipservice.error.InvalidPoolIdProvidedException;
import com.assignment.go.ipservice.error.IpBlacklistedException;
import com.assignment.go.ipservice.error.IpReservedException;
import com.assignment.go.ipservice.error.IpValueNotWithinIpPoolRangeException;
import com.assignment.go.ipservice.error.NotAvailableIpResourcesException;
import com.assignment.go.ipservice.error.ValidationErrorMessage;
import com.assignment.go.ipservice.error.ValidationErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidPoolIdProvidedException.class)
	public ResponseEntity<Object> handle(InvalidPoolIdProvidedException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("provided id is not valid - not exists");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotAvailableIpResourcesException.class)
	public ResponseEntity<Object> handle(NotAvailableIpResourcesException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("not available ip resources");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.EXPECTATION_FAILED);
	}

	@ExceptionHandler(IpValueNotWithinIpPoolRangeException.class)
	public ResponseEntity<Object> handle(IpValueNotWithinIpPoolRangeException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("ip value provided is not within range of provided ip pool id");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IpReservedException.class)
	public ResponseEntity<Object> handle(IpReservedException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("ip value provided is reserved");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IpBlacklistedException.class)
	public ResponseEntity<Object> handle(IpBlacklistedException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("ip value provided is blacklisted");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.CONFLICT);
	}

}
