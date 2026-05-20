package com.example.hokkaidoec.service;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.mapper.CategoryMapper;

@Service
public class CategoryService {

	private final CategoryMapper categoryMapper;

	public CategoryService(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}

	public Category getCategoryById(int categoryId) {
		return categoryMapper.findById(categoryId);
	}
}
