package com.example.hokkaidoec.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.form.UserForm;
import com.example.hokkaidoec.mapper.UserMapper;

@Service
public class UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	public void register(UserForm form) {
		// ★1. メールアドレスで重複チェックを行う
		User existingUser = userMapper.findByEmail(form.getEmail());
		if (existingUser != null) {
			// すでにユーザーが見つかった場合は、エラーメッセージを持たせた例外を投げる
			throw new IllegalArgumentException("このメールアドレスはすでに登録されています。");
		}

		System.out.println("ユーザ登録：" + form.getName() + " <" + form.getEmail() + ">");

		User user = new User();
		user.setName(form.getName());
		user.setEmail(form.getEmail());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setAddress(form.getAddress());
		user.setPhone(form.getPhone());
		user.setRankId(1);

		userMapper.insert(user);
	}
}