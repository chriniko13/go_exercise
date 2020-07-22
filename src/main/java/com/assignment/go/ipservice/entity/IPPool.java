package com.assignment.go.ipservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.InetAddress;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "description" })
@ToString

@Entity
public class IPPool {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	//TODO add unique
	private String description;

	private Long totalCapacity;
	private Long usedCapacity;

	private String lowerBound;
	private String upperBound;

	@SneakyThrows
	public static long ipToLong(String ipValue) {
		InetAddress ip = InetAddress.getByName(ipValue);

		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

	public void initCapacities() {
		long delta = ipToLong(upperBound) - ipToLong(lowerBound);
		totalCapacity = delta + 1;
		usedCapacity = 0L;
	}

	public long getLowerBoundAsNum() {
		return ipToLong(lowerBound);
	}

	public long getUpperBoundAsNum() {
		return ipToLong(upperBound);
	}

	public boolean isInRange(String ipValue) {
		long l = getLowerBoundAsNum();
		long h = getUpperBoundAsNum();

		long toCheck = ipToLong(ipValue);

		return toCheck >= l && toCheck <= h;
	}

	public void increaseUsedCapacity(int size) {
		this.usedCapacity += size;
	}
}
