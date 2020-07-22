package com.assignment.go.ipservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "value" })
@ToString

@Entity
@Table(indexes = {

		@Index(name = "IDX_IP_POOL_ID", columnList = "ipPoolId"),
		@Index(name = "UNIQUE_IDX_VALUE", columnList = "value", unique = true)

})
public class IPAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long ipPoolId;

	private String value;

	@Enumerated(EnumType.STRING)
	private IPAddressState state;

	public static IPAddress createReserved(Long ipPoolId, String value) {
		return new IPAddress(null, ipPoolId, value, IPAddressState.RESERVED);
	}

	public static IPAddress createBlacklisted(long ipPoolId, String value) {
		return new IPAddress(null, ipPoolId, value, IPAddressState.BLACKLISTED);
	}
}
