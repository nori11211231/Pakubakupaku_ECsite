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
		System.out.println("ユーザ登録：" + form.getName() + " <" + form.getEmail() + ">");

		User user = new User();
		user.setName(form.getName());
		user.setEmail(form.getEmail());

		// パスワードを暗号化してセット（これは元のコードでバッチリでした！）
		user.setPassword(passwordEncoder.encode(form.getPassword()));

		// ★2. フォームから追加した項目をしっかりエンティティに詰め替える！
		user.setAddress(form.getAddress());
		user.setPhone(form.getPhone());

		// ★3. 最初の質問で決めた「初期ランクのID（例: 1 = 一般会員）」を設定！
		user.setRankId(1);

		// データベースへ保存
		userMapper.insert(user);
	}
}