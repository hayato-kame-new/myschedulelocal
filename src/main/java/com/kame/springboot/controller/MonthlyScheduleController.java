package com.kame.springboot.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // coreの方にする
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Schedule;
import com.kame.springboot.pojo.Monthly;
import com.kame.springboot.service.ScheduleService;
import com.kame.springboot.userDetails.UserDetailsImpl;

//コントローラでは、@Transactionalつけないこと. サービスクラスを利用するので、
// サービスのクラスの方に@Transactionalアノテーションをつけて、コントローラのリクエストハンドラでtry-catchするため
@Controller // コンポーネントです
public class MonthlyScheduleController {

	@Autowired
	ScheduleService scheduleService; // サービスもコンポーネントです

	@Autowired
	ViewBean viewBean; // コンポーネントです
	
 // @RequestParamでは、パラメータ名が指定されていない場合:MissingServletRequestParameterException例外が発生
	// パラメータ値が指定されていない場合:引数に空文字列がセットされる
	// 認証ユーザの指定の月を表示するので、認証ユーザのidが必要
	
	@RequestMapping(value = "/monthly", method = RequestMethod.GET)
	public ModelAndView monthlyShow(@AuthenticationPrincipal UserDetailsImpl userDetails, // これが必要
			Model model, // Flash Scopeから値の取り出しに必要 スケジュール登録や編集や削除の後にリダイレクトしてくるので
			@RequestParam(name = "display", required = false) String display, // デフォルトでは必須パラメータ
			@RequestParam(name = "year", required = false) Integer year, // int だとエラー java.lang.IllegalStateException																			
			@RequestParam(name = "month", required = false) Integer month, // intだとエラー java.lang.IllegalStateException																		
			ModelAndView mav) {

		// スケジュールを登録してきた後は、リダイレクトしてきます
		// Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
		if(model.getAttribute("display") != null) {
			display = (String) model.getAttribute("display");
		}
		
		
		// Userクラスを作って、ゲッターで取れるように修正したので id も emailも取れるようになってます！！
		System.out.println(userDetails.getId()); // id
		System.out.println(userDetails.getUsername()); // username
		System.out.println(userDetails.getPassword()); // password
		System.out.println(userDetails.getEmail()); // email

		
		//データベースから検索してきたリストを newで確保
		List<Object[]> monthlyScheduleList = new ArrayList<>();
	
		Map<Integer, List<Integer>> weekMap = new LinkedHashMap<>(); // 格納した順番を記憶するLinkedHashMap
		Monthly monthly = null;
		LocalDate localDate = null;
		int[] array = null;
		String flashMsg = "";

		switch (display) {
		case "currentMonth":
			// 今月を表示する ユーザのidが必要
			// 今月の情報は、引数なしのコンストラクタを呼ぶと取得できる
			monthly = new Monthly(); // 今月の 年と月の情報
			monthlyScheduleList = scheduleService.getMonthlyScheduleList(monthly, userDetails.getId());

			year = monthly.getYear(); // 今月のときは、monthlyインスタンスから取得する
			month = monthly.getMonth(); // 今月のときは、monthlyインスタンスから取得する

			array = monthly.getCalendarDay();
			// コンポーネントのメソッドを使って、表示に必要なMapを取得する
			weekMap = viewBean.createWeekMap(array); // {1=[31, 1, 2, 3, 4, 5, 6], 2=[7, 8, 9, 10, 11, 12, 13], 3=[14, 15, 16, 17, 18, 19, 20], 4=[21, 22, 23, 24, 25, 26, 27], 5=[28, 29, 30, 1, 2, 3, 4], 6=[0, 0, 0, 0, 0, 0, 0]}
			break; // switch文を抜ける

		case "beforeMonth":
			// year monthは、aリンクの クエリー文字列で送られてきてる この情報から、前の月のインスタンスを得る
			// .minusMonths(1) で１ヶ月前にする
			localDate = LocalDate.of(year, month, 1).minusMonths(1); // 1つ前の月の初日を生成してる
			// 引数ありのコンストラクタを使うと、指定の月のインスタンスが取得できる
			monthly = new Monthly(localDate);
			// ビューで使用
			monthlyScheduleList = scheduleService.getMonthlyScheduleList(monthly, userDetails.getId());

			// ビューで使うため year monthを上書きしておく
			year = monthly.getYear();
			month = monthly.getMonth();
			// まず、指定の月の日にちが入った配列を取得し
			array = monthly.getCalendarDay();
			// 配列を元に コンポーネントのメソッドを使って、ビューで必要なMapを取得する
			weekMap = viewBean.createWeekMap(array);
			break; // switch文を抜ける

		case "afterMonth":
			// year monthは、aリンクの クエリー文字列で送られてきてる この情報から、後ろの月のインスタンスを得る
			localDate = LocalDate.of(year, month, 1).plusMonths(1);
			monthly = new Monthly(localDate);
			monthlyScheduleList = scheduleService.getMonthlyScheduleList(monthly, userDetails.getId());
			year = monthly.getYear();
			month = monthly.getMonth();
			array = monthly.getCalendarDay();
			weekMap = viewBean.createWeekMap(array);
			break; // switch文を抜ける

		case "return":

			// schedule.htmlの戻るのリンクで、前に表示していた 年 月 の 表示をする
			// year monthは、aリンクの クエリー文字列で送られてきてる この情報から、戻って表示するの月のインスタンスを得る
			localDate = LocalDate.of(year, month, 1);
			monthly = new Monthly(localDate);
			monthlyScheduleList = scheduleService.getMonthlyScheduleList(monthly, userDetails.getId());
			year = monthly.getYear();
			month = monthly.getMonth();
			array = monthly.getCalendarDay();
			weekMap = viewBean.createWeekMap(array);

			break; // switch文を抜ける

		case "redirect":
			// スケジュールを新規登録した後や、編集・削除をした後リダイレクトしてくる フラッシュメッセージ Flash Scopeから値の取り出す
			// Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
			flashMsg = (String) model.getAttribute("flashMsg");// 返り値がObject型なので、キャストすること
			// リダイレクトしてきたので、year monthも Flash Scopeから取り出して上書きする
			year = (Integer) model.getAttribute("year");// 返り値がObject型なので、キャストすること
			month = (Integer) model.getAttribute("month");// 返り値がObject型なので、キャストすること
			localDate = LocalDate.of(year, month, 1);
			monthly = new Monthly(localDate); // 指定の年と月のインスタンスを生成して
			// その指定の年と月の１ヶ月分のスケジュールを取得する			
			monthlyScheduleList = scheduleService.getMonthlyScheduleList(monthly, userDetails.getId());
		
			array = monthly.getCalendarDay();  // その月が何日あるのかを取得
			weekMap = viewBean.createWeekMap(array);  // その月の週の情報を取得
			break; // switch文を抜ける
		}
		// ビューを指定
		mav.setViewName("monthlyDisplay");

		// 「addObject」は、ModelAndViewに値を保管するものです
		mav.addObject("weekMap", weekMap);

		mav.addObject("monthlyScheduleList", monthlyScheduleList);

		// monthly year month それぞれ表示に必要
		mav.addObject("monthly", monthly); // 年 月 の情報
		mav.addObject("year", year); // 年 の情報
		mav.addObject("month", month); // 月 の情報
		// 本日の日付の情報を送る
		LocalDate localdateToday = LocalDate.now();

		mav.addObject("localdateToday", localdateToday);
		mav.addObject("display", display);
		mav.addObject("flashMsg", flashMsg); // リダイレクトしてきた後に、メッセージを表示するので
		return mav;
	}
	
	
	
	// このクラスのインスタンス(非static)メソッドの定義 今回は使わない参考とっとく このクラスで呼び出すときはthis.をつける
	public List<Schedule> newMonthlyDataList(List<Object[]> list) {
		// 詰め直し用のリストを newで確保
		List<Schedule> newMonthlyDataList = new ArrayList<Schedule>();	
		Schedule schedule = new Schedule();
		Iterator itr =  list.iterator();
				
		int id = 0;
		int userId = 0;
		LocalDate scheduleDate = null;
		LocalTime startTime = null;
		LocalTime endTime = null;
		String scheduleSubject = null;
		String scheduleMemo = null;
		
		while(itr.hasNext()) {
			Object[] obj = (Object[]) itr.next(); //例として [2, 8, 2021-11-12, 01:30:00, 03:30:00, 絵を, fdsfeweg ] などの可変長のコレクションになってます
			id = Integer.parseInt(String.valueOf(obj[0]));  // Object型の 2 を文字列型にして"2" を さらに intに変換する
			userId = Integer.parseInt(String.valueOf(obj[1]));
				
			// obj[2] は、Object型ですのでまず java.sql.Dateにしてから、java.time.LocalDateへ変換をします
			java.sql.Date sqlDate = (Date) obj[2];
			scheduleDate = sqlDate.toLocalDate(); // OK
			
			java.sql.Time sqlTimeStart = (Time) obj[3];
			startTime = sqlTimeStart.toLocalTime();
			
			java.sql.Time sqlTimeEnd = (Time) obj[4];
			endTime = sqlTimeEnd.toLocalTime();
			
			scheduleSubject = String.valueOf(obj[5]);
			scheduleMemo = String.valueOf(obj[6]);
			// Scheduleインスタンスにセットしていく
			schedule.setId(id);
			schedule.setUserId(userId);
			schedule.setScheduleDate(scheduleDate);
			schedule.setStartTime(startTime);
			schedule.setEndTime(endTime);
			schedule.setScheduleSubject(scheduleSubject);
			schedule.setScheduleMemo(scheduleMemo);
		}
		// リストに詰め直し
		newMonthlyDataList.add(schedule);
	
		return newMonthlyDataList;
	}

}
