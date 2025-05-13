package com.example.NAGOYAMESHI.entity;

import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "shops")
@Data
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToMany
	@JoinTable(name = "shop_category", joinColumns = @JoinColumn(name = "shop_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories;

	@Column(name = "name")
	private String name;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private Integer price;

	@Column(name = "capacity")
	private Integer capacity;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "address")
	private String address;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "monday_opening_hours")
	private String mondayOpeningHours;

	@Column(name = "monday_closing_hours")
	private String mondayClosingHours;

	@Column(name = "tuesday_opening_hours")
	private String tuesdayOpeningHours;

	@Column(name = "tuesday_closing_hours")
	private String tuesdayClosingHours;

	@Column(name = "wednesday_opening_hours")
	private String wednesdayOpeningHours;

	@Column(name = "wednesday_closing_hours")
	private String wednesdayClosingHours;

	@Column(name = "thursday_opening_hours")
	private String thursdayOpeningHours;

	@Column(name = "thursday_closing_hours")
	private String thursdayClosingHours;

	@Column(name = "friday_opening_hours")
	private String fridayOpeningHours;

	@Column(name = "friday_closing_hours")
	private String fridayClosingHours;

	@Column(name = "saturday_opening_hours")
	private String saturdayOpeningHours;

	@Column(name = "saturday_closing_hours")
	private String saturdayClosingHours;

	@Column(name = "sunday_opening_hours")
	private String sundayOpeningHours;

	@Column(name = "sunday_closing_hours")
	private String sundayClosingHours;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;// Shop.java

}
