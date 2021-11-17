package com.kame.springboot.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

// 他のクラスの内部でフィールドとして使うクラスです @Entityをつけて @Id をつけないと@OneToManyがつけられない

@Entity
@Table(name = "users")
public class User {
	// SpringSecurity関連のチュートリアルでは、認証に使うusername,passwordはセッションから参照できるとあるが、
		// 他の任意のパラメータ idを取得したり、emailを取得したりしたいので、そのやり方
		// UserDetailsをimplementsしたクラスにgetterをつければいいだけ
		// まず、そのUserDetailsをimplementsしたクラスにフィールドとして利用するUserクラスを

	// フォームクラスSignupFormクラスも見てください！！
	
	    @Id  // 主キー @Entityをつけたら これがないと起動しません
	    @Column(name = "id") // カラム名は、postgreSQL 全て小文字なので小文字にする リレーションのあるカラム
		private int id; // 主キーになるもの idもゲッターで取得できるようにしたい
						// SpringSecurityの認証済みユーザの情報にusername,password以外をもたせるて、getterで参照できるようにしたい
		
	   // @Column(name = "username") // カラム名は、postgreSQL 全て小文字なので小文字にする
	    @Column(name = "name")
	    private String username; // usersテーブルのカラム名は name   usernameでいい userNameにしない 注意UserDetailsImplに合わせること
	    
	    @Column(name = "password")
		private String password;
	    
	    @Column(name = "authority")
		private String authority;
	    

		@Column(name = "email")
		private String email; // SpringSecurityの認証済みユーザの情報にusername,password以外をもたせるて、getterで参照できるようにしたい
	    
	   
	    
		// @Entityをつけて @Id をつけないと@OneToManyがつけられない
	    // これ付け足したけど大丈夫らしい？ mappedByは相手側の データベースでリレーションが着いてるフィールド名??
//	    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)  // userIdでいいのかしら？？ 
//		List<Schedule> Schedules;

		// mappedByに指定する値は「対応する(＠ManyToOneがついた)フィールド変数名」になります。
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // 相手側の@ManyToOneが着いてるのがUser user; なので mappedBy = "user"
		List<Schedule> Schedules;
	    
	    /**
	     * 引数なしのコンストラクタも、明示的に作ること　　必要
	     */
		public User() {
			super();
			// TODO 自動生成されたコンストラクター・スタブ
		}

		/**
		 * コンストラクタ
		 * 
		 * @param id
		 * @param username
		 * @param password
		 * @param email
		 */
		public User(int id, String username, String password, String email) {
			super();
			this.id = id;
			this.username = username;
			this.password = password;
			this.email = email;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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
		
		public String getAuthority() {
			return authority;
		}
		
		public void setAuthority(String authority) {
			this.authority = authority;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	}
