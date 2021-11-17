package com.kame.springboot.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.User;
import com.kame.springboot.form.ScheduleForm;
import com.kame.springboot.service.ScheduleService;
import com.kame.springboot.service.UserService;
/**
 * スケジュールのフォームを表示するコントローラ
 * また、指定の日のタイムスケジュールも表示している
 * @author skame
 *
 */
@Controller  // ここのクラス宣言や、メソッドには@Transactional をつけないこと サービスクラスで @Transactionalをつけるから
public class ScheduleFormController {
	
	@Autowired
	ViewBean viewBean;  // コンポーネントです
	
	@Autowired
	ScheduleService scheduleService;  // サービスもコンポーネントです
	
	@Autowired
	UserService userService;  // サービスもコンポーネントです
	
	/**
	 * フォームを表示する  スケジュールも表示する
	 * @param scheduleForm
	 * @param action
	 * @param year
	 * @param month
	 * @param day
	 * @param id
	 * @param result
	 * @param model
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/formDisplay", method = RequestMethod.GET) 
	public ModelAndView formDisplay(
			@ModelAttribute(name = "scheduleForm")ScheduleForm scheduleForm, // 変更してみた
			// ScheduleForm scheduleForm,
			@RequestParam(name = "action")String action,
			@RequestParam(name = "year", required = false)Integer year,  // schedule.htmlの aリンクで使う  ビューで表示に使う getOneDayScheduleListメソッドに使う 
			@RequestParam(name = "month", required = false)Integer month, // schedule.htmlの aリンクで使う  ビューで表示に使う  getOneDayScheduleListメソッドに使う 
			@RequestParam(name = "day", required = false)Integer day, // リンクには使わないけど、ビューで表示に使う   getOneDayScheduleListメソッドに使う 
			@RequestParam(name = "id", required = false)Integer id,  // 編集時にidをパラメータで送ってくる
			ModelAndView mav) {
		
	
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
		//例えば　要素は 
//		[4, 8, 2021-11-12, 00:00:00, 01:00:00, iii, dd	    	]	
//		 [5, 8, 2021-11-12, 00:30:00, 03:00:00, grgar, weawsas	]
//		  [2, 8, 2021-11-12, 01:30:00, 03:30:00, 絵を, fdsfeweg	]
//		   [3, 8, 2021-11-12, 03:30:00, 06:30:00, ああああ, 	] これはメモを入力してないから、空文字になってる
		switch(action) {
		case "add":
			// このままでいい
			break; // switch文抜ける
		
		case "edit":	
			// 編集と削除の時には パラメータで idが送られてるので
			// idを元にしてscheduleテーブルからデータを検索して、その値でscheduleFormオブジェクトを上書きする
			List<Object[]> resultDataList = scheduleService.findScheduleDataById(id);
			Iterator itr =  resultDataList.iterator();
			// int id = 0;
			int userId = 0;
			LocalDate scheduleDate = null;
			LocalTime startTime = null;
			LocalTime endTime = null;
			String scheduleSubject = null;
			String scheduleMemo = null;
			while(itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				 // id = obj[0];
				userId = Integer.parseInt(String.valueOf(obj[1]));
				
			// obj[2] は、Object型ですのでjava.sql.Date へ変換する それからjava.time.LocalDateへ変換をします
			java.sql.Date sqlDate = (java.sql.Date) obj[2];
			scheduleDate = sqlDate.toLocalDate(); // OK
			
			java.sql.Time sqlTimeStart = (java.sql.Time) obj[3];
			startTime = sqlTimeStart.toLocalTime();
			
			java.sql.Time sqlTimeEnd = (java.sql.Time) obj[4];
			endTime = sqlTimeEnd.toLocalTime();
			
			scheduleSubject = String.valueOf(obj[5]);
			scheduleMemo = String.valueOf(obj[6]);
				
			}
			// scheduleFormオブジェクトを上書きする それをビューでフォームのオブジェクトにするので
			scheduleForm.setId(id);
			scheduleForm.setUserId(userId);
			scheduleForm.setYear(scheduleDate.getYear());
			scheduleForm.setMonth(scheduleDate.getMonthValue());
			scheduleForm.setDay(scheduleDate.getDayOfMonth());
			scheduleForm.setStartHour(startTime.getHour());
			scheduleForm.setEndHour(endTime.getHour());
			scheduleForm.setStartMinute(startTime.getMinute());
			scheduleForm.setStartMinute(endTime.getMinute());
			scheduleForm.setScheduleSubject(scheduleSubject);
			scheduleForm.setScheduleMemo(scheduleMemo);
			break; // switch文抜ける
		}
		mav.setViewName("schedule");
		
		mav.addObject("timeList", timeList);
		mav.addObject("oneDayScheduleList", oneDayScheduleList);  
		mav.addObject("year", year); 
		mav.addObject("month", month); 
		mav.addObject("day", day); 
		 mav.addObject("scheduleForm", scheduleForm);  // 編集・削除の時に上書きして、セットするのに必要
		mav.addObject("yearList", yearList);
		mav.addObject("minuteList", minuteList);
		mav.addObject("action", action);
		return mav;
	}
}
