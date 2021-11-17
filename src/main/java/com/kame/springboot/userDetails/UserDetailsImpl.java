package com.kame.springboot.userDetails;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kame.springboot.entity.User;

public class UserDetailsImpl implements UserDetails {

	/**
	 * シリアル番号UID フィールド
	 */
	private static final long serialVersionUID = 1880595758442205066L;
	
	// フィールド
//	private String username;  // 注意 usersテーブルのカラム名は nameになってます
//    private String password;
    private Collection<GrantedAuthority> authorities;
    
    // Userインスタンスを参照する  これが大切重要
    private  User user;
    
    // これ付け足したけど大丈夫かな？？？ mappedByは相手側の データベースでリレーションが着いてるフィールド名??
//    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)  // userIdでいいのかしら？？ 
//	List<Schedule> Schedules;

    

//    public UserDetailsImpl(String username, String password, Collection<GrantedAuthority> authorities) {
//        this.username = username;
//        this.password = password;
//        this.authorities = authorities;
//    }
    
    /**
     * コンストラクタ
     * @param authorities
     * @param user
     */
    public UserDetailsImpl(Collection<GrantedAuthority> authorities, User user) {
    	super();
    	this.authorities = authorities;
    	this.user = user;
    }
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
//	@Override
//    public String getPassword() {
//        return password;
//    }
//    @Override
//    public String getUsername() {
//        return username;
//    }
    
    // 修正
    @Override
    public String getPassword() {   	
    	return user.getPassword();  // ここ修正
    }
    
    // 修正
    @Override
    public String getUsername() {   	
    	return user.getUsername();  // ここ修正
    }
    
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public String getEmail() {  	
    	return user.getEmail();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getEmail)  で、アクセスして取得できます
    }
    
 // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public int getId() {  	
    	return user.getId();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getEmail)  で、アクセスして取得できます
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


	@Override
    public boolean isAccountNonLocked() {
         return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}