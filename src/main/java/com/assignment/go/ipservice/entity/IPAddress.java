package com.assignment.go.ipservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "value" })
@ToString

@Entity
public class IPAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// TODO index...
	private Long ipPoolId;

	// TODO index...
	private String value;

	//TODO valueAsInteger

	private IPAddressState state;

	public static IPAddress createReserved(Long ipPoolId, String value) {
		return new IPAddress(null, ipPoolId, value, IPAddressState.RESERVED);
	}

	public static IPAddress createBlacklisted(long ipPoolId, String value) {
		return new IPAddress(null, ipPoolId, value, IPAddressState.BLACKLISTED);
	}
}
