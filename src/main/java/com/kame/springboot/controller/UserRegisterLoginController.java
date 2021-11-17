package com.kame.springboot.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kame.springboot.form.SignupForm;
import com.kame.springboot.userDetailsService.UserDetailsServiceImpl;

@Controller
@RequestMapping("/")
public class UserRegisterLoginController {
	
	@Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
	
	
	/**
	 * ＠AuthenticationPrincipal からユーザー情報を取得する
	 * ユーザー情報は、アノテーション ＠AuthenticationPrincipal を使用すると、より簡単に取得することができます
	 * 
	 * @param userDetails
	 * @return "index"
	 */
//	@GetMapping
//    public String index(@AuthenticationPrincipal UserDetailsImpl userDetails) {  // org.springframework.security.core.annotation.AuthenticationPrincipal
//        System.out.println(userDetails.getUsername());  // ユーザー名を表示
//        System.out.println(userDetails.getPassword());  // パスワードを表示
//        System.out.println(userDetails.getAuthorities().toString());  // 権限情報を表示
//        return "index";
//    }

	
	@GetMapping
    public String index () {
		
		// SecurityContextHolder からユーザー情報を取得する  トップページを表示する際に、コンソール画面に出力されます
		//  SecurityContextHolderからAuthenticationオブジェクトを取得 
		SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        
     // Authenticationオブジェクトからユーザー情報を取得
        System.out.println(authentication.getName());  // ユーザー名を表示
        System.out.println(authentication.getAuthorities());  // 権限情報を表示
        System.out.println(authentication.toString());
        
      
     // Authenticationオブジェクトからユーザー情報を取得
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        System.out.println(principal.getUsername());  // ユーザー名を表示
        System.out.println(principal.getPassword());  // パスワードを表示
        System.out.println(principal.getAuthorities());  // 権限情報を表示
        System.out.println(principal.toString());
        
      
                
        return "index";
    }
	
	
	@GetMapping("/login")
    public String login () {
        return "login";
    }
	
	/**
	 * ユーザー登録ページの表示
	 * @param signupForm
	 * @return "signup"
	 */
	 @GetMapping("/signup")
	    public String newSignup(SignupForm signupForm) {
	        return "signup";
	    }

	 	/**
	 	 * ユーザー情報の登録実行
	 	 * 後で、メールアドレスのフィールドを追加しました
	 	 * @param signupForm
	 	 * @param result
	 	 * @param model
	 	 * @return "signup":失敗した時のフォワード遷移先 <br /> "redirect:/":成功したときのリダイレクト先
	 	 */
	    @PostMapping("/signup")
	    public String signup(@Validated SignupForm signupForm, BindingResult result, Model model, 
	    		HttpServletRequest request) {
	       
	    	 // バリデーションチェックを行うために
	        if (result.hasErrors()) {
	            return "signup";
	        }
	        
	        // ユーザ名を登録しようとして、同一のユーザ名が存在しないかのチェックをし、
	        // 同一のユーザ名が存在すれば、
	        // エラーメッセージを返し、ユーザ登録画面へフォワードする
	        if (userDetailsServiceImpl.isExistUser(signupForm.getUsername())) {
	            model.addAttribute("signupError", "ユーザー名 " + signupForm.getUsername() + "は既に登録されています");
	            return "signup";
	        }
	        
	    	try {
	        	// UserDetailsServiceImpl において作成した register メソッドを使用して
	        	// ユーザー情報をデータベースに格納します emailからむ追加
	        	// 追加からむ
	            userDetailsServiceImpl.register(signupForm.getUsername(), signupForm.getPassword(), "ROLE_USER", signupForm.getEmail());
	        } catch (DataAccessException e) {
	        	// ユーザー登録に失敗した場合は、「ユーザー登録に失敗しました」というメッセージを表示す
	            model.addAttribute("signupError", "ユーザー登録に失敗しました");
	            return "signup";
	        }
	    	// 自動でログイン処理を行う ポイントは、 自動ログインのため、もし、ログインしてたら、一旦ログアウトさせます！！
	    	// 既にログインしている場合は、一旦ログアウトさせるようにしてます
	    	// なお、メソッドの引数に HttpServletRequest request を追加しています。
	    	
	    	// 自動的にログインさせる処理を行う前に、既にログインしているユーザーをログアウトさせる処理を追加します
	    	// SecurityContext は、セキュリティ情報（コンテキスト）を定義するインターフェース
	    	 
	    	// 実行中のアプリケーションに関するセキュリティ情報（コンテキスト）は、SecurityContextHolder の getContext メソッドを使用することで取得することができます
	    	// この SecurityContextHolder には、現在アプリケーションとやり取りをしているユーザーに関する情報も含まれています
	    	
	    	// このユーザーに関する情報は、Authentication インターフェイスで定義されており、SecurityContextHolder の getAuthentication メソッドを使用することにより取得することができます
	    	SecurityContext context = SecurityContextHolder.getContext();
	         Authentication authentication = context.getAuthentication();  // この Authentication オブジェクトから、ユーザー名や権限情報（ロール）なども取得することができます

	         //  匿名ユーザー（anonymousUser）であることの判定 ログインしてないユーザであることの判明
	         // こで必要なのは「ログイン済みのユーザー」であることを判定することですが、そのために「匿名ユーザー」（ログインしていないユーザー） かどうかを判定する手段を用います
	         if (authentication instanceof AnonymousAuthenticationToken == false) {  
	        	 // 自動ログインのため、もし、ログインしてたら、一旦ログアウトさせます！！
	             SecurityContextHolder.clearContext(); // この SecurityContextHolder には、現在アプリケーションとやり取りをしているユーザーに関する情報も含まれています
	             // ユーザー新規登録後に、自動でログイン処理を行うようにするために、
	             // ポイントは、 自動ログインのため、もし、ログインしてたら、一旦ログアウトさせます！！
	        // 匿名ユーザー ログインしてない人のことです！！（anonymousUser）かどうかは、Authentication オブジェクトが AnonymousAuthenticationToken インスタンスであるかどうかを確認することで判定することができます。
	         }
	         
	         // もし、ログインしていたら、clearContext()で、ログアウトを実行しています
	         // 以上の処理から、現在ログイン中と判定されたユーザーに対してログアウト処理を実行します
	         // ログアウト処理は、SecurityContextHolder に格納されているセキュリティ情報（コンテキスト）を消去することで実行します
	         // 消去には、clearContext メソッドを使用
	         
	         
	         // それから、自動ログイン処理をします
	         // HttpServletRequest オブジェクトの login メソッドを使用して、新たに登録されたユーザー名とパスワードでログイン処理を行っています
	         try {
	             request.login(signupForm.getUsername(), signupForm.getPassword());  // ログイン処理の実行
	         } catch (ServletException e) {
	             e.printStackTrace();
	         }

	        return "redirect:/";  // 成功したら、トップへリダイレクトする リダイレクトはリクエストハンドラのメソッド実行させるので
	    }

}
