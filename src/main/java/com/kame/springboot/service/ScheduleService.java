package com.kame.springboot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.Schedule;
import com.kame.springboot.pojo.Monthly;
import com.kame.springboot.repository.ScheduleRepository;

@Service
@Transactional  //  クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class ScheduleService {
	
	@Autowired
	ScheduleRepository scheduleRepository;
	
	// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
		@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
		private EntityManager entityManager;

		/**
		 * 認証ユーザの指定の月のレコードを全件取得する。
		 * @param monthly
		 * @param userId
		 * @return List<Object[]>
		 */
		@Transactional
	  public List<Object[]> getMonthlyScheduleList(Monthly monthly, int userId){
		 // ひと月分のリストを保持する resultlistを newで確保しておく
		  List<Object[]> resultlist = new ArrayList<>();
		  
		  //  月の初めから最後の日までループで回す
		  for(int i = 1; i <= monthly.getThisMonthLastDay(); i++) {
			  // 認証ユーザの i日の1日分のスケジュールを開始時間順にselectする			 
			  Query query = entityManager.createNativeQuery("select * from schedule where userid = ? and scheduledate = ? order by starttime"); //  order by starttime が必要です 
			  query.setParameter(1, userId);
			  LocalDate localdate = LocalDate.of(monthly.getYear(), monthly.getMonth(), i);
			  java.sql.Date sqlDate = java.sql.Date.valueOf(localdate);
			  query.setParameter(2, sqlDate);
			  // ループの中で i日の1日分のスケジュールがリストになって取得できる 開始時間順になってる
			  Collection<? extends Object[]> oneDayList = query.getResultList();  
				  // 取得したoneDayList は、ループの中で ひと月分のリストに足し込んでおく				  
				  resultlist.addAll(oneDayList);  // addAllは1つ目のリストに2つ目のリストの要素をすべて追加する						
		  }
	
			return resultlist;  // 例 リストの先頭の要素[2, 8, 2021-11-12, 01:30:00, 03:30:00, 絵を, fdsfeweg				 
	  }
		
		
		  /**
		   * 認証ユーザの指定の1日分のスケジュールをリストにして取得する ユーザIDから検索してます
		   * @param userId
		   * @param localDate
		   * @return List<Object[]>
		   */
		 public List<Object[]> getOneDayScheduleList(int userId, LocalDate localDate) {
			 //  認証ユーザの指定の1日分のリストを newで確保しておく
			 List<Object[]> oneDayScheduleList = new ArrayList<>();
			 // PostgreSQLでは カラム名テーブル名を全て小文字にしてください  userid  scheduledate 
			 Query query = entityManager.createNativeQuery("select * from schedule where userid = ? and scheduledate = ? order by starttime"); //  order by starttime が必要です ?::にしないように！間違えた
			 query.setParameter(1, userId);
			  
			  java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
			  query.setParameter(2, sqlDate);
			  
			   oneDayScheduleList = query.getResultList();
			 return oneDayScheduleList;
		 }
		 
		 /**
		  * リポジトリのメソッド自動生成機能を使った スケジュール新規作成
		  * saveAndFlushの戻り値は保存したエンティティオブジェクト
		  * @param schedule
		  * @return Scheduleインスタンス
		  */
//		public Schedule create(Schedule schedule) {
//			return scheduleRepository.saveAndFlush(schedule);  // リレーションがあるとうまく取れない？？
//		}
		 
		 /**
		  * スケジュール新規作成
		  * @param schedule
		  * @return true:成功<br /> false:失敗
		  */
		public boolean create(Schedule schedule) {
			
			// createNativeQueryは　JPQLではなくて、素のSQL  PostgreSQLのテーブル名カラム名は全て小文字にすること
			// id serial primary key   シーケンス（データ型のserial）とは シーケンスとはINSERTで値を入れなくとも、自動で採番される列 なので idカラムには入れない
			
			Query query = entityManager.createNativeQuery("insert into schedule (userid, scheduledate, starttime, endtime, schedulesubject,  schedulememo ) values (:a, :b, :c, :d, :e, :f)"); 
			 query.setParameter("a", schedule.getUserId());
			 
			 java.time.LocalDate localDate =  schedule.getScheduleDate();
			 java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
			 query.setParameter("b", sqlDate);
			 
			 java.time.LocalTime localTimeStart = schedule.getStartTime();
			 java.sql.Time sqlTimeStart = java.sql.Time.valueOf(localTimeStart);		 
			 query.setParameter("c", sqlTimeStart);
			 			 
			 java.time.LocalTime localTimeEnd = schedule.getEndTime();
			 java.sql.Time sqlTimeEnd = java.sql.Time.valueOf(localTimeEnd);		 
			 query.setParameter("d", sqlTimeEnd);
			 
			 query.setParameter("e", schedule.getScheduleSubject());
			 query.setParameter("f", schedule.getScheduleMemo());
			 
			 int result = query.executeUpdate(); // 戻り値は、データの更新や、削除に成功したエンティティの数です
			 if (result != 1) { // 失敗
					return false;  // 失敗したら falseを返す
				}
				// 成功したら、
				return true;
		}
		
		
		/**
		 * 主キーのidを元にして、Scheduleのデータを取得する 取得したデータは List<Object[]>になってます
		 * @param id
		 * @return List<Object[]>
		 */
		public List<Object[]> findScheduleDataById(int id) {
			Query query = entityManager.createNativeQuery("select * from schedule where id = :id ");
			//Query query = entityManager.createNativeQuery("select * from schedule where id = ? ");
			query.setParameter("id", id);
			
			 List<Object[]> resultDataList = query.getResultList();
			
			return resultDataList;			
		}
		
		/**
		 * Scheduleのデータの更新
		 * @param id
		 * @param scheduleDate
		 * @param startTime
		 * @param endTime
		 * @param scheduleSubject
		 * @param scheduleMemo
		 * @return true:成功<br /> false:失敗
		 */
		public boolean update(int id,  LocalDate scheduleDate, LocalTime startTime, LocalTime endTime, String scheduleSubject, String scheduleMemo) {
			
			Query query = entityManager.createNativeQuery("update schedule set (scheduledate, starttime, endtime, schedulesubject, schedulememo) = (?,?,?,?,?) where id = ?  ");
			
			// データベースに更新する際には、LocalDate を jav.sql.Dateに変換しないといけない
			java.sql.Date sqlDate = java.sql.Date.valueOf(scheduleDate);
			//  TemporalType.DATE は java.sql.Dateで登録するという意味
			query.setParameter(1, sqlDate ,TemporalType.DATE );  // TemporalType.DATE：java.sql.Dataと同じ
			// TemporalType.TIMESTAMPもある  TemporalType.TIMESTAMP：java.sql.Timestampと同じです
			
			// データベースに更新する際には、LocalTime を jav.sql.Timeに変換しないといけない
			java.sql.Time sqlTimeStart = java.sql.Time.valueOf(startTime);	
			// TemporalType.TIME：java.sql.Timeと同じ  java.sql.Timeで登録するという意味
			query.setParameter(2, sqlTimeStart, TemporalType.TIME);
			
			// データベースに更新する際には、LocalTime を jav.sql.Timeに変換しないといけない
			java.sql.Time sqlTimeEnd = java.sql.Time.valueOf(endTime);			
			query.setParameter(3, sqlTimeEnd ,TemporalType.TIME); // TemporalType.TIME：java.sql.Timeと同じ
			
			query.setParameter(4, scheduleSubject);
			query.setParameter(5, scheduleMemo);
			query.setParameter(6, id);
			
			int result = query.executeUpdate();
			if (result != 1) { // 失敗
				return false; // 失敗したら false が返る
			}
			return true;
		}
		
		
		/**
		 * Scheduleのデータの 削除
		 * @param id
		 * @return true:成功<br /> false:失敗
		 */
		public boolean delete(int id) {
			Query query = entityManager.createNativeQuery("delete from  schedule where id = :id  ");
			query.setParameter("id", id);
			int result = query.executeUpdate();
			if (result != 1) { // 失敗
				return false; // 失敗したら false が返る
			}
			return true;
		}

}
