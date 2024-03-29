package com.cosmetics.myshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "brand")
	private String brand;

	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private Double price;

	@Column(name = "currency")
	private String currency;

	@Column(name = "image_link")
	private String imageLink;

	@Column(name = "description",columnDefinition = "TEXT")
	private String description;

	@Column(name = "rating")
	private Double rating;

	@Column(name = "product_type")
	private String productType;

	@Column(name="category_name")
	private String categoryName;

	@Column(name = "tag_list")
	private String tagList;

}
