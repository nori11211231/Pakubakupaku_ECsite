//package com.example.hokkaidoec.form;
//
//import jakarta.validation.constraints.AssertTrue;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//
//public class UserForm {
//	@NotBlank(message = "名前は必須です")
//	private String name;
//
//	@NotBlank(message = "メールアドレスは必須です")
//	@Email(message = "メールアドレスの形式が正しくありません")
//	private String email;
//
//	@NotBlank(message = "メール確認用アドレスを入力してください")
//	private String emailConfirm;
//
//	@NotBlank(message = "パスワードは必須です")
//	@Size(min = 6, message = "パスワードは6文字以上で入力してください")
//	private String password;
//
//	// --- ここから追加項目 ---
//
//	@NotBlank(message = "郵便番号は必須です")
//	@Pattern(regexp = "^\\d{3}-?\\d{4}$", message = "郵便番号は半角数字3桁-4桁（例: 060-0001）で入力してください")
//	private String zipcode;
//
//	@NotBlank(message = "住所は必須です")
//	private String address;
//
//	@NotBlank(message = "電話番号は必須です")
//	@Pattern(regexp = "^\\d{2,4}-\\d{2,4}-\\d{4}$", message = "電話番号はハイフンを含めて正しく入力してください（例: 011-XXX-XXXX）")
//	private String phone;
//
//	// --- バリデーションメソッド ---
//
//	@AssertTrue(message = "メールアドレスが一致しません")
//	public boolean isEmailConfirmed() {
//		return email != null && email.equals(emailConfirm);
//	}
//
//	// --- Getter / Setter ---
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getEmailConfirm() {
//		return emailConfirm;
//	}
//
//	public void setEmailConfirm(String emailConfirm) {
//		this.emailConfirm = emailConfirm;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getZipcode() {
//		return zipcode;
//	}
//
//	public void setZipcode(String zipcode) {
//		this.zipcode = zipcode;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	public String getPhone() {
//		return phone;
//	}
//
//	public void setPhone(String phone) {
//		this.phone = phone;
//	}
//}