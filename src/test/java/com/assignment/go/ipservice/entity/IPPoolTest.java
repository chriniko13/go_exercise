package com.assignment.go.ipservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IPPoolTest {


	@Test
	public void initCapacitiesWorksAsExpected() {


		// given
		IPPool p = new IPPool();
		p.setLowerBound("10.70.26.1");
		p.setUpperBound("10.70.26.100");

		// when
		p.initCapacities();

		// then
		assertEquals(100, p.getTotalCapacity());
		assertEquals(0, p.getUsedCapacity());


		// given
		IPPool p2 = new IPPool();
		p2.setLowerBound("10.50.0.0");
		p2.setUpperBound("10.50.255.255");


		// when
		p2.initCapacities();

		// then
		assertEquals(65536, p2.getTotalCapacity());
		assertEquals(0, p2.getUsedCapacity());

	}


}
