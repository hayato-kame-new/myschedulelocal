package com.kame.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
// このアノテーションにより「Spring Security が提供しているコンフィギュレーションクラスがインポートされ、Spring Security を利用するために必要となるコンポーネントの Been 定義が自動で行われる」とのことです（Spring 徹底入門414ページ）
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // これを指定することで Spring Security の機能が有効化されます
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * DB使用のため
	 */
	@Autowired
    private UserDetailsService userDetailsService;
	
	/**
	 * @Beanと書いたメソッドでインスタンス化されたクラスがシングルトンクラスとしてDIコンテナに登録される。
	 * 自クラスで使えるし、任意のクラスで@Autowiredで注入してアクセスできる。
	 * @return
	 */
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**
	 * WebSecurity クラスを使用して、主にアプリケーション全体に関するセキュリティの設定を行います
	 * CSS フォルダにあるファイルに対して、Spring Security の処理を適用しないようにしています。
	 */
	@Override
    public void configure(WebSecurity web) throws Exception {
		// ignoring()   Spring Security が無視する RequestMatcher インスタンスを追加できる
		// antMatchers​(String... antPatterns) ant パターンに一致するリソース（List）を適用対象にする
        web.ignoring().antMatchers("/css/**");
    }
	
	
	/**
	 * Spring Boot 2.X でBasic認証を無効にする方法
	 * Spring Boot 2.0以降では Basic認証を無効にするには、http.authorizeRequests().antMatchers("/").permitAll();
	 * を書く必要があります
	 */
//	  @Override
//	    protected void configure(HttpSecurity http) throws Exception{
//	        http.authorizeRequests().antMatchers("/").permitAll();
//	    }
	
	  /**
	   * 認証管理を行う AuthenticationManagerBuilder クラスを引数にとった configure メソッド
	   * ログイン認証に使用するユーザー情報を設定する
	   * inMemoryAuthentication というメソッドを使用して、メモリ内にユーザー情報を格納して認証を行う時は、
	   * auth.inMemoryAuthentication().withUser(ユーザー名).password(パスワード).roles(権限情報);
	   * という形です
	   * inMemoryAuthentication()  メモリ内認証を追加する
	   * パスワードは平文ではなく、ハッシュ化した文字列を記載する必要があります
	   * あらかじめハッシュ化した文字列を指定したい場合は、どこか適宜の場所に次のコードを書けば、コンソール画面から取得できます
	   * System.out.println(new BCryptPasswordEncoder().encode("123456"));
	   * System.out.println(new BCryptPasswordEncoder().encode("password"));
	   */
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				
		// DB使用するときは、この１行だけ書く
		auth.userDetailsService(userDetailsService);
		
//		System.out.println(new BCryptPasswordEncoder().encode("123456")); //  $2a$10$gzyfKvPHR3UULwWCSSOtGOGuJayQGHxVco.Dw/QN6vG3Y7UwEeGSy
//		System.out.println(new BCryptPasswordEncoder().encode("password"));// $2a$10$qpHQ9aFpPnXdmaMudfccau/dIQazJ5IQQGoNj8ReKgAc4AFhHIEV2
		
		// DBを使う時には、コメントアウトします
		// DB はまだ使わないで、inMemoryAuthentication というメソッドを使用して、メモリ内にユーザー情報を格納して認証を行うようにしています
//        auth.inMemoryAuthentication()  // メモリ内に保存するためのメソッド
//            .withUser("yama3")
//            .password(passwordEncoder().encode("123456")) // パスワードは「ハッシュ化された文字列」をセットする必要があります
//            .roles("USER");
        
        // ハッシュ化されたパスワードを直接記載しても、問題なく動作します。
//        auth.inMemoryAuthentication()
//        .withUser("yama3")
//        .password("$2a$10$E55vg96856cWy4oyAUpQ6OH2mxO6eTt43A5lPwa3MszPbDpAOPiLG")
//        .roles("USER");
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {  // HttpSecurity クラスを使用して、主にURLごとのセキュリティの設定を行う
		// アクセス制限の設定
		// (1)
//		http.authorizeRequests()
//		.anyRequest()  // 全てのリクエスト対象
//		.authenticated();  // 認証済み（ログイン済み）のユーザーにのみリクエストを承認する
		
		 // formLogin メソッドでフォーム認証を使用することを指定しています。
        // loginPage メソッドでログイン画面のURLを /login と指定し、defaultSuccessUrl メソッドで認証後にリダイレクトされるページを指定し、
        // permitAll メソッドで全てのユーザーにアクセスの許可を与えています。
        // なお、Spring Security では、フォーム認証のほか、Basic 認証、Digest 認証、Remember Me 認証用のサーブレットフィルタクラスも提供されています（Spring 徹底入門423ページ）。
		// (2)
//		http.formLogin()
//		.loginPage("/login")
//		.defaultSuccessUrl("/")
//		.permitAll();
		
		// logout() メソッドでログアウト機能を有効にして、permitAll() で全てのユーザーに対してログアウト機能に関するアクセス権を付与しています
		// (3)
//		http.logout() // ログアウト機能を有効にする
//        .permitAll();
		
		// (1)(2)(3)を繋げて
//		 http.authorizeRequests().anyRequest().authenticated().and()
//         .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll().and()
//         .logout().permitAll();
		
		// (1)(2)(3)を繋げて さらにさらにサインアップ ユーザ登録(まだログインしていないユーザーにも「ユーザー登録画面（/signup）」にアクセスしてもらう)
		 http.authorizeRequests()
         .antMatchers("/signup").permitAll() // これにより、ログインしていないユーザーにも「ユーザー登録画面（/signup）」にアクセスすることが許可されます。
         .anyRequest().authenticated().and()
         .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll().and()
         .logout().permitAll();
		
	}

}
