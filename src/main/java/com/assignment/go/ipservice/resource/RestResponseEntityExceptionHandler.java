package com.assignment.go.ipservice.resource;

import com.assignment.go.ipservice.error.InvalidPoolIdProvidedException;
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



}
