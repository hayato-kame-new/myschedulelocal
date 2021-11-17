package com.kame.springboot.userDetailsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.User;
import com.kame.springboot.userDetails.UserDetailsImpl;


@Service  // アノテーション必要
public class UserDetailsServiceImpl implements UserDetailsService {

	// DBに登録する際に必要 SecurityConfig クラスで Bean 定義した PasswordEncode を取得します
	 @Autowired
	    PasswordEncoder passwordEncoder;
	
	@Autowired
    JdbcTemplate jdbcTemplate;

	
	/**
	 * 名前から検索
	 * usersテーブル nameカラムです PostgreSQLは テーブル名カラム名全て小文字にすること
	 */
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		// ここでは、JdbcTemplate を使用し、SQL文でレコードを抽出して、取得したユーザー情報を UserDetails（UserDetailsImpl）に詰め込んでいます
//		
//		// Spring Security のデフォルトの動作では、UsernameNotFoundException は BadCredentialsException という例外に変換してからエラー処理が行われるようになっています
//		// そのため、クライアント側では「Bad credentials（ユーザー名かパスワードが正しくありません）」という通知を受け取ることになります
//		
//		try {
//            String sql = "SELECT * FROM users WHERE name = ?";  // usersテーブルです  nameカラムです カラムとテーブルの名前は小文字にしてください
//            Map<String, Object> map = jdbcTemplate.queryForMap(sql, username);
//            String password = (String)map.get("password");
//            Collection<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));
//            return new UserDetailsImpl(username, password, authorities);
//            
//        } catch (Exception e) {
//            throw new UsernameNotFoundException("user not found.", e);
//        }
//	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// ここでは、JdbcTemplate を使用し、SQL文でレコードを抽出して、取得したユーザー情報を UserDetails（UserDetailsImpl）に詰め込んでいます
		
		// Spring Security のデフォルトの動作では、UsernameNotFoundException は BadCredentialsException という例外に変換してからエラー処理が行われるようになっています
		// そのため、クライアント側では「Bad credentials（ユーザー名かパスワードが正しくありません）」という通知を受け取ることになります
		
		try {
            String sql = "SELECT * FROM users WHERE name = ?";  // usersテーブルです  nameカラムです カラムとテーブルの名前は小文字にしてください
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, username);
            String password = (String)map.get("password");
            // 追加
            String email = (String)map.get("email");
            int id = (Integer)map.get("id");
            
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));
           // return new UserDetailsImpl(username, password, authorities);
            User user = new User(id, username, password, email);
            return new UserDetailsImpl(authorities, user);
            
        } catch (Exception e) {
            throw new UsernameNotFoundException("user not found.", e);
        }
	}
	
	/**
	 * ユーザー情報をデータベースに登録する register メソッド
	 * usersテーブル nameカラムです PostgreSQLは テーブル名カラム名全て小文字にすること
	 * @param username
	 * @param password
	 * @param authority
	 */
//	@Transactional
//    public void register(String username, String password, String authority) {
//		
//		// JdbcTemplate の update メソッドで、データベースにユーザー情報を登録します
//		// パスワードは、PasswordEncoder（BCrypt）でハッシュ化しておきます。
//        String sql = "INSERT INTO users(name, password, authority) VALUES(?, ?, ?)";
//        jdbcTemplate.update(sql, username, passwordEncoder.encode(password), authority);
//    }
	
	@Transactional
  public void register(String username, String password, String authority, String email) {
		
		// JdbcTemplate の update メソッドで、データベースにユーザー情報を登録します
		// パスワードは、PasswordEncoder（BCrypt）でハッシュ化しておきます。
      String sql = "INSERT INTO users(name, password, authority, email) VALUES(?, ?, ?, ?)";
      jdbcTemplate.update(sql, username, passwordEncoder.encode(password), authority, email);
  }
	
	/**
	 * データベースに同一ユーザー名が既に登録されているかを確認する
	 * usersテーブル nameカラムです PostgreSQLは テーブル名カラム名全て小文字にすること
	 * JdbcTemplate の queryForObject メソッドを使用してデータベース内の検索結果を取得
	 * 同一ユーザー名が存在すれば true、存在しなければ　false を返します
	 * 
	 * @param username
	 * @return true:同一ユーザーが存在する <br /> false:存在しない
	 */
	public boolean isExistUser(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";  // 小文字でusersテーブル   nameカラムです
        int count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[] { username });
        if (count == 0) {  // 存在しない
            return false;  // returnで即終了し、引数を呼び出し元へ返す
        }
        return true; 
    }
	

}
