package com.assignment.go.ipservice.validator;

public interface Validator<I, O> {

	O process(I input);

}
