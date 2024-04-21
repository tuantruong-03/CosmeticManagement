package com.cosmetics.myshop.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.cosmetics.myshop.model.Const;
import com.cosmetics.myshop.model.Product;
import com.cosmetics.myshop.repository.ProductRepository;
import com.cosmetics.myshop.service.ProductService;
import com.cosmetics.myshop.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductController {
	@Autowired
	ProductService productService;

	@GetMapping("/product/{productId}")
	String getProduct(@PathVariable(name = "productId") Integer id, Model model)
			throws JsonMappingException, JsonProcessingException {
		Product product = productService.findProductByid(id);
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> tagList = new ArrayList<>();
		tagList = Arrays.asList(objectMapper.readValue(product.getTagList(), String[].class));
		StringUtils stringUtils = new StringUtils();
		long totalRelatedProducts = productService.countRelatedProducts(product);
//		System.out.println(totalRelatedProducts);
		model.addAttribute("product", product);
		model.addAttribute("tagList", tagList);
		model.addAttribute("productId", id);
		model.addAttribute("stringUtils", stringUtils);
		model.addAttribute("totalRelatedProducts", totalRelatedProducts);
		return "/user/product_details";
	}

	@GetMapping("/products/{categoryName}")
	String getProductsByCategory(@PathVariable(name = "categoryName") String categoryName, Model model) {
		List<String> brandList = productService.findBrandsByCategory(categoryName);
		List<String> typeList = productService.findProductTypesByCategory(categoryName);
		long totalProductsByCategory = productService.countProductsByCategoryName(categoryName);
		
		model.addAttribute("brandList", brandList);
		model.addAttribute("typeList", typeList);
		model.addAttribute("totalProductsByCategory", totalProductsByCategory);
		model.addAttribute("categoryName", categoryName);
		
		return "/user/products";
	}
	
	@ResponseBody
	@PostMapping("/admin/product")
	ResponseEntity<Product> processPostProduct(@RequestParam("image") MultipartFile file
			, @RequestParam Map<String, Object> body, HttpServletResponse response) throws Exception {
		Product product = extractAttributesFromBody(body);
		String originalFilename = file.getOriginalFilename();
	    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1); // Get extension of file
		String milliseconds = String.valueOf(new Date().getTime()); // Get milliseconds for unique image name
		String filePath = Const.IMAGE_UPLOAD_DIRECTORY + "/img/categories/" + milliseconds + "." + extension;
		Path fileNameAndPath = Paths.get(filePath);
		Files.write(fileNameAndPath,file.getBytes());
		String imageLink = "/img/categories/" + milliseconds + "." + extension;
		product.setImageLink(imageLink);
		product.setCreatedAt(new Date());
		productService.saveProduct(product);
		Thread.sleep(1000);
		response.sendRedirect("/admin/products/" + product.getCategoryName());
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	private Product extractAttributesFromBody(Map<String, Object> body) {
		System.out.println(body.toString());
		String categoryName = (String)body.get("categoryName");
		String name = (String)body.get("name");
		String brand = (String)body.get("brand");
		Double price = Double.parseDouble((String)body.get("price"));
		String productType = (String)body.get("productType");
		String tagList = (String)body.get("tagList");
		String description = (String)body.get("description");
		Double rating = new Random().nextDouble() + 4;
		String[] tagListArray = tagList.split(", "); // cruelty, free => [cruelty, free]
		for (int i = 0; i < tagListArray.length; i++) {
			tagListArray[i] = "\"" + tagListArray[i] + "\""; // "cruelty", "free"
		}
		Stream<String> tagListStream = Arrays.stream(tagListArray);
		tagList = tagListStream.map(tag -> tag)
				.collect(Collectors.joining(","));
		tagList = "[" + tagList + "]";
		Product product = new Product(brand, name, price, "USD", "", description, rating, productType, categoryName, tagList);
		return product;
	}
	
}
