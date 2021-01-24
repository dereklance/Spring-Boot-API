package com.smoothstack.booking.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "tbl_traveler")
@NoArgsConstructor
@RequiredArgsConstructor
public class Traveler {
	
	@Id private Integer travelerId;
	
	@NonNull private String name;
	
	@NonNull private String address;
	
	@NonNull private String phone;
	
	@NonNull private String email;
	
	@NonNull private Timestamp dob;
}
