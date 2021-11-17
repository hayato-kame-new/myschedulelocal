package com.kame.springboot.service;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.User;
import com.kame.springboot.repository.UserRepository;

@Service
@Transactional  //  クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class UserService {
	
	
	@Autowired
	UserRepository userRepository;
	
	// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
		@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
		private EntityManager entityManager;
		
		
		// javax.persistence.Query
		public User getUserData(String name) {
			// createNativeQueryメソッドを使う  NativeなSQLで JPQLではありません  usersテーブル nameカラム 全て小文字で PostgreSQLだから
			Query query = entityManager.createNativeQuery("select * from users where name = ?");  
			query.setParameter(1, name);
			List<User> userDataList = (List<User>)query.getResultList();
			Iterator itr =  userDataList.iterator();
			User user = new User();
			// new して確保 詰め直しリスト今回は要らないけど参考として
			// List<User> resultDataList = new ArrayList<User>();
			
			int id = 0;
			String password = "";
			String authority = "";
			String email = "";
			while(itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();  //  [8, misa, $2a$10$cXBXEQcRDoMhv3XmRh0V0OPOrzgM0IKNQ6vrFMeKO60ZVMGTpwbju, ROLE_USER, misa@misa.com]
				id = Integer.parseInt(String.valueOf(obj[0]));  // Object型の 8 を文字列型にして"8" を さらに intに変換する
				// String name = String.valueOf(obj[1]);  // "misa"
				 password = String.valueOf(obj[2]);	//	"$2a$10$cXBXEQcRDoMhv3XmRh0V0OPOrzgM0IKNQ6vrFMeKO60ZVMGTpwbju"
				 authority = String.valueOf(obj[3]);  // "ROLE_USER"
				email = String.valueOf(obj[4]);  // "misa@misa.com"
			}
			// Userインスタンスにセットしていく
			user.setId(id);
			user.setUsername(name);
			user.setPassword(password);
			user.setAuthority(authority);
			user.setEmail(email);
			return user;
		}

}
