package com.ha.transformers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "<h1>Transformers by Hassan Abdi</h1>";
	}

}
