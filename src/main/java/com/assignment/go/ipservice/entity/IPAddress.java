package com.assignment.go.ipservice.entity;

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
@EqualsAndHashCode(of = {"id"})
@ToString

@Entity
public class IPAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	private Long ipPoolId;
	private String value;
	private IPAddressState state;


}
