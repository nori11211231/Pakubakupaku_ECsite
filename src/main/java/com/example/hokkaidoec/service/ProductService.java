package com.example.hokkaidoec.service;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.mapper.ProductsMapper;

@Service
public class ProductService {

	private final ProductsMapper productsMapper;

	public ProductService(ProductsMapper productMapper) {
		this.productsMapper = productMapper;
	}

	public Product getProductById(int productId) {
		return productsMapper.findById(productId);
	}
}
