package com.example.hokkaidoec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegionController {
	@GetMapping("/regions")
	public String showDetail(Model model) {
		return "regions";
	}
}
