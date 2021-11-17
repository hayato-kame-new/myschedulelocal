package com.kame.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
//コントローラには @Transactional をつけないこと 
//サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので ここのリクエストハンドラでtry-catchできなくなるので、つけないこと
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.form.ScheduleForm;
import com.kame.springboot.service.ScheduleService;

@Controller
public class ScheduleDeleteController {
	
	@Autowired  // @Autowiredをつけると自動でインスタンスを生成してくれる
	ScheduleService scheduleService;
	
	@RequestMapping(value = "/scheduleDelete", method = RequestMethod.POST)
	public ModelAndView scheduleDelete(
			@ModelAttribute(name = "scheduleForm")ScheduleForm scheduleForm,  // 必要
			RedirectAttributes redirectAttributes,
			ModelAndView mav, 
    		HttpServletRequest request
    		) {
		
		// Flash scopeへ保存して リダイレクトする Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
	     String flashMsg = "";
	     
	     int id = scheduleForm.getId();
	     int year = scheduleForm.getYear();
	     int month = scheduleForm.getMonth();
	     	     
//	     List<Object[]> findScheduleDataList = scheduleService.findScheduleDataById(id);
	     // リダイレクトする先の年と月の情報を取得する
	  // このリストの先頭の要素は 例  [4, 8, 2021-11-12, 00:00:00, 01:00:00, iii, dd ]
//	     Iterator itr =  findScheduleDataList.iterator();
//	     int userId = 0;
//	     LocalDate scheduleDate = null;
//	     LocalTime startTime = null;
//	     LocalTime endTime = null;
//	     String scheduleSubject = "";
//	     String scheduleMemo = "";
	     
//    	 while(itr.hasNext()) {
// 			Object[] obj = (Object[]) itr.next(); //例として [4, 8, 2021-11-12, 00:00:00, 01:00:00, iii, dd ]
// 			// id = Integer.parseInt(String.valueOf(obj[0]));  // Object型の 4 を文字列型にして"4" を さらに intに変換する
// 			// userId = Integer.parseInt(String.valueOf(obj[1]));  //  Object型の 8 を文字列型にして"8" を さらに intに変換する
// 				
// 			// // obj[2] は、Object型ですのでjava.sql.Date へ変換するデータベースから取り出した直後はjava.sql.Dateだから それからjava.time.LocalDateへ変換をします
// 			
// 			java.sql.Date sqlDate = (java.sql.Date) obj[2];
// 			scheduleDate = sqlDate.toLocalDate(); // OK
// 			
 			 //  // obj[3] は、Object型ですのでjava.sql.Time へ変換するデータベースから取り出した直後はjava.sql.Timeだから それからjava.time.LocalTimeへ変換をします
// 			
// 			java.sql.Time sqlTimeStart = (java.sql.Time) obj[3];
// 			startTime = sqlTimeStart.toLocalTime();
// 			
// 			java.sql.Time sqlTimeEnd = (java.sql.Time) obj[4];
// 			endTime = sqlTimeEnd.toLocalTime();
// 			
// 			 scheduleSubject = String.valueOf(obj[5]);
// 			 scheduleMemo = String.valueOf(obj[6]);
 			// Scheduleインスタンスを生成する newと引数ありのコンストラクタで生成し、上で nullを代入してるから nullを上書きして、各フィールは更新したい内容にする
// 			schedule.setId(id);
// 			schedule.setUserId(userId);
// 			schedule.setScheduleDate(scheduleDate);
// 			schedule.setStartTime(startTime);
// 			schedule.setEndTime(endTime);
// 			schedule.setScheduleSubject(scheduleSubject);
// 			schedule.setScheduleMemo(scheduleMemo);
 			// newすることで、メモリに確保される ただし、削除には、idさえあればいいから使わない
 			// schedule = new Schedule(id, userId, scheduleDate, startTime, endTime, scheduleSubject, scheduleMemo);
 			// idだけで、削除できる
 //   	 }
//	     int year = scheduleDate.getYear();
//	     int month = scheduleDate.getMonthValue();
	   
	 // 削除の時にはid userIdが hiddenフィールドで送ってるからパラメータで送られてくる
	  boolean success = scheduleService.delete(id);
	  if(success == false) { // 失敗
		 // 失敗のメッセージと return 
		 flashMsg = "スケジュールを削除きませんでした";
		 mav.setViewName("schedule");
		 return mav;  // return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
	 } else {
		 // 成功
		 flashMsg = "スケジュールを削除しました";
	 }
	  // 成功したらリダイレクトする リダイレクトは、コントロ-ラのリクエストハンドラを実行させる
 	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
     // Flash Scop は Request Scope より長く、Session Scope より短いイメージ
		redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
		redirectAttributes.addFlashAttribute("year", year);	
		redirectAttributes.addFlashAttribute("month", month);	
	
		redirectAttributes.addFlashAttribute("display", "redirect");
		// Flash Scopeに保存して、リダイレクトする  登録をした月 編集や削除をした月を表示させる
		return new ModelAndView("redirect:/monthly");
	}
}
