package com.kame.springboot.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignupForm {
	
	// クラスにアノテーションはない
	// 入力値を保持するためのフォームクラス
	// このフォームクラスにユーザーの入力値を保持してデータのやり取りを行います。
	// コントローラのメソッドの引数に SignupForm signupForm とか @Validated SignupForm signupForm などと利用する
	
	// フォームには、入力チェックを行うためのアノテーションを付けていきます
	// Null チェックには、一般的には ＠NotNull を使いますが、
	// ここでは、＠NotBlank を使用しています
	// ＠NotNull 又は ＠NotEmpty を使用した場合、半角スペースのみでもユーザー名として登録ができてしまいますが、この半角スペースのユーザー名ではログインすることができないためです
	@NotBlank  // javax.validation.constraints.NotBlank            Null、空文字、空白をエラーとする
    @Size(min = 1, max = 50, message = "ユーザー名は1文字以上50文字以下で入力してください")  // javax.validation.constraints.Size	
	private String username;  // 注意 usersテーブルの nameカラムへの このクラスはエンティティじゃないから同じ名前でなくてもいい
    
	
	@NotBlank  // javax.validation.constraints.NotBlank            Null、空文字、空白をエラーとする
    @Size(min = 6, max = 20)
	private String password;
	
	// 追加
	@NotBlank  // javax.validation.constraints.NotBlank            Null、空文字、空白をエラーとする
 @Email  // javax.validation.constraints.Email
	private String email;
	
	/*
	 * なお、Spring MVC では「文字列の入力フィールドに未入力の状態でフォームを送信した場合、
	 * デフォルトではフォームオブジェクトにnullではなく、
	 * 空文字がバインドされる」（参考）ため、＠NotNull は入力値のチェックでは働きません
	 */

	// 追加
	public String getEmail() {
		return email;
	}
	// 追加
	public void setEmail(String email) {
		this.email = email;
	}
	
    public String getUsername() {
        return username;
    }
	public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
