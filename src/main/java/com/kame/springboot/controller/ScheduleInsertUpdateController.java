package com.kame.springboot.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Schedule;
import com.kame.springboot.entity.User;
import com.kame.springboot.form.ScheduleForm;
import com.kame.springboot.service.ScheduleService;
import com.kame.springboot.service.UserService;
// コントローラには @Transactional をつけないこと 
// サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので 
// ここのリクエストハンドラでtry-catchできなくなるので、@Transactionalつけないこと
@Controller
public class ScheduleInsertUpdateController {
	
	@Autowired  // @Autowiredをつけると自動でインスタンスを生成してくれる
	ScheduleService scheduleService;
	
	@Autowired  // @Autowiredをつけると自動でインスタンスを生成してくれる
	UserService userService;
	
	@Autowired
	ViewBean viewBean; // コンポーネントです
	
	/**
	 * スケジュールを登録・編集する 登録・編集フォームHTTPメソッドがPOSTでアクセスしてくる
	 * @param action
	 * @param id
	 * @param userId
	 * @param scheduleForm
	 * @param result
	 * @param redirectAttributes
	 * @param mav
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/schedule", method = RequestMethod.POST)
	public ModelAndView schedule(
			@RequestParam(name = "action") String action, // 必須パラメータ
			@RequestParam(name = "id" , required = false) Integer id,  // 新規の時には無いのでnull  required = false で任意パラメータにする
			@RequestParam(name = "userId" , required = false) Integer userId,  // 新規の時には無いのでnull  required = false で任意パラメータにする
			@ModelAttribute(name = "scheduleForm")@Validated ScheduleForm scheduleForm,
			// @Validated ScheduleForm scheduleForm,  // 上のに変更してみた
			BindingResult result,
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
			ModelAndView mav, 
    		HttpServletRequest request
			) {
		
		String flashMsg = "";  
		int year = scheduleForm.getYear();
		int month = scheduleForm.getMonth();
		int day = scheduleForm.getDay();
        if (result.hasErrors()) {
        	// フォワード
        	mav.setViewName("schedule");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			mav.addObject("scheduleForm", scheduleForm);
			mav.addObject("action", action);
        	mav.addObject("year", year);
        	mav.addObject("month", month);
        	mav.addObject("day", day);
        	mav.addObject("id",id);
        	// フォームの年
			List<Integer> yearList = viewBean.yearList();
			// フォームの分
			List<Integer> minuteList = viewBean.minuteList();
			
			// タイムスケジュールの表示
			LinkedList<String> timeList = viewBean.createTimeList();
			
			// SecurityContextHolder からユーザー情報を取得する 
			//  SecurityContextHolderからAuthenticationオブジェクトを取得 
			SecurityContext context = SecurityContextHolder.getContext();
	        Authentication authentication = context.getAuthentication();
			String name = authentication.getName();  // 認証ユーザの名前を取得	
			// まず ユーザ名はユニーク(一意制約)なので、ユーザ名を元に usersテーブルから ユーザーのデータを取得する
			User user = userService.getUserData(name);		
			// ユーザIDを元に scheduleテーブルから  認証ユーザの指定の日にちの1日のスケジュールのリストを取得する
			// oneDayScheduleList スケジュールがない時には [] 空のリストが返る
			List<Object[]> oneDayScheduleList = scheduleService.getOneDayScheduleList(user.getId(), LocalDate.of(year, month, day));
			
			mav.addObject("timeList", timeList);
			mav.addObject("oneDayScheduleList", oneDayScheduleList);  
			mav.addObject("yearList", yearList);
			mav.addObject("minuteList", minuteList);
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
        }
        // バリデーションチェックを通過したら、処理を進める
     Schedule schedule = null;
     boolean success = false;     
     LocalDate scheduleDate = LocalDate.of(year, month, day);     
     LocalTime startTime = LocalTime.of(scheduleForm.getStartHour(), scheduleForm.getStartMinute());  // 01:00
     LocalTime endTime = LocalTime.of(scheduleForm.getEndHour(), scheduleForm.getEndMinute()); // 03:00
     String scheduleSubject = scheduleForm.getScheduleSubject();
     String scheduleMemo = scheduleForm.getScheduleMemo();
     
     switch(action) {
        case "add":
        	// 新規の時にはid  userId無い
        	// ユーザIDはusersテーブルから取得する
        	// SecurityContextHolder からユーザー情報を取得する 
        	//  SecurityContextHolderからAuthenticationオブジェクトを取得 
        	SecurityContext context = SecurityContextHolder.getContext();
        	Authentication authentication = context.getAuthentication();
        	String name = authentication.getName();  // 認証ユーザの名前を取得	
        	// まず ユーザ名はユニーク(一意制約)なので、ユーザ名を元に usersテーブルから ユーザーのデータを取得する
        	User user = userService.getUserData(name);
        	userId = user.getId();  // これは新規の時だけ必要な作業 編集と削除の時には、hiddenのパラメータで取れる      	 
        	  schedule = new Schedule(userId, scheduleDate, startTime, endTime, scheduleSubject, scheduleMemo);
        	// データベースに登録 保存に成功したら trueが返る
        	    success = scheduleService.create(schedule);
        	 if(success == false) { // 失敗
        		 // 失敗のメッセージと return 
        		 flashMsg = "スケジュールを登録できませんでした";
        		 mav.setViewName("schedule");
        		 return mav;  // return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
        	 } else {
        		 // 成功
        		 flashMsg = "スケジュールを登録しました";
        	 }
        	break;  // switch文を抜ける
        	
        case "edit":
        	// 編集の時にはid userIdが hiddenフィールドで送ってるからパラメータで送られてくる       	
        	success = scheduleService.update(id,  scheduleDate,  startTime,  endTime,  scheduleSubject,  scheduleMemo);
        	 if(success == false) { // 失敗
        		 // 失敗のメッセージと return 
        		 flashMsg = "スケジュールを更新きませんでした";
        		 mav.setViewName("schedule");
        		 return mav;  // return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
        	 } else {
        		 // 成功
        		 flashMsg = "スケジュールを更新しました";
        	 }
//        	 findScheduleDataList = scheduleService.findScheduleDataById(id);
//        	 // このリストの先頭の要素は 例  [4, 8, 2021-11-12, 00:00:00, 01:00:00, iii, dd ]
//        	 Iterator itr =  findScheduleDataList.iterator();
//        	
//        	 while(itr.hasNext()) {
//     			Object[] obj = (Object[]) itr.next(); //例として [4, 8, 2021-11-12, 00:00:00, 01:00:00, iii, dd ]
//     			id = Integer.parseInt(String.valueOf(obj[0]));  // Object型の 4 を文字列型にして"4" を さらに intに変換する
//     			userId = Integer.parseInt(String.valueOf(obj[1]));  //  Object型の 8 を文字列型にして"8" を さらに intに変換する
//     				
//     			// obj[2] は、Object型ですのでjava.sql.Date へ変換するデータベースから取り出した直後はjava.sql.Dateだから それからjava.time.LocalDateへ変換をします
//     			java.sql.Date sqlDate = (java.sql.Date) obj[2];
//     			scheduleDate = sqlDate.toLocalDate(); // OK
//     			
//     			 // obj[3] は、Object型ですのでjava.sql.Time へ変換するデータベースから取り出した直後はjava.sql.Timeだから それからjava.time.LocalTimeへ変換をします
//     			java.sql.Time sqlTimeStart = (java.sql.Time) obj[3];
//     			startTime = sqlTimeStart.toLocalTime();
//     			
//     			java.sql.Time sqlTimeEnd = (java.sql.Time) obj[4];
//     			endTime = sqlTimeEnd.toLocalTime();
//     			
//     			String scheduleSubject = String.valueOf(obj[5]);
//     			String scheduleMemo = String.valueOf(obj[6]);
//     			// Scheduleインスタンスを生成する newと引数ありのコンストラクタで生成し、上で nullを代入してるから nullを上書きして、各フィールは更新したい内容にする
////     			schedule.setId(id);
////     			schedule.setUserId(userId);
////     			schedule.setScheduleDate(scheduleDate);
////     			schedule.setStartTime(startTime);
////     			schedule.setEndTime(endTime);
////     			schedule.setScheduleSubject(scheduleSubject);
////     			schedule.setScheduleMemo(scheduleMemo);
//     			// newすることで、メモリに確保される ただし、更新には、idさえあればいいから使わない
//     			// schedule = new Schedule(id, userId, scheduleDate, startTime, endTime, scheduleSubject, scheduleMemo);
//     			// idだけで、更新できる
//        	 }      	
        	break;  // switch文から抜ける     
        } 
        // 成功したらリダイレクトする リダイレクトは、コントロ-ラのリクエストハンドラを実行させる
    	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
        // Flash Scop は Request Scope より長く、Session Scope より短いイメージ
		redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
		redirectAttributes.addFlashAttribute("year", year);	
		redirectAttributes.addFlashAttribute("month", month);	
		// redirectAttributes.addFlashAttribute("day", day);  // いらん
		redirectAttributes.addFlashAttribute("display", "redirect");
		// Flash Scopeに保存して、リダイレクトする  登録をした月 編集や削除をした月を表示させる
		return new ModelAndView("redirect:/monthly");	
	}	
}
