package com.kame.springboot.pojo;  // Plain Old Java Object シンプルなJavaのオブジェクト 特定のフレームワークやオブジェクトモデル、規約などに縛られてない

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * その月の情報を得て、コントローラなどで使用するためのクラス
 * エンティティのアノテーションはつけない テーブルじゃないから
 * @author skame
 *
 */
public class Monthly {
	
	
	 private int year;
	    private int month;
	    private int day;
	    private int startWeek; // 今月が何曜日から開始されているか
	    private int beforeMonthLastDay;  // 先月が何日までだったか
	    private int thisMonthLastDay;  // 今月が何日までか
	    private int[] calendarDay;  // カレンダーに載せる日数   最大で7日×6週 要素は最大で42個 最大で [0]から[41]まで    [26, 27, 28, 29, 30, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 1, 2, 3, 4, 5, 6]
	    private int weekCount; // 今月は何週あるか

	   /**
	    * 引数なしのコンストラクタ 今月の情報を生成してる
	    */
	public Monthly() {
		LocalDate localdate = LocalDate.now();  // 本日の日付を取得して年月日の情報を得る
		 this.year = localdate.getYear();  // 年のフィールドにセット
	        this.month = localdate.getMonthValue();  // 月のフィールドにセット
	        this.day = localdate.getDayOfMonth(); // 日のフィールドにセット
	        // 今月が何曜日から開始されているか
	        this.startWeek = LocalDate.of(this.year, this.month, 1).getDayOfWeek().getValue();
	        // 先月が何日までだったか
	        this.beforeMonthLastDay = localdate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
	        // 今月が何日までか
	        this.thisMonthLastDay = localdate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
	        // カレンダーに載せる日数 と 今月は何週あるのか
	        Map<Integer, int[]> map = this.creatCalendarDay( this.startWeek,  this.beforeMonthLastDay,  this.thisMonthLastDay);
	        for(Map.Entry<Integer, int[]> entry : map.entrySet()) {
	            this.calendarDay = entry.getValue();  // カレンダーに載せる日数 int[]
	            this.weekCount = entry.getKey();  // 今月は何週あるのか
	        }	        
	}
	
	   /**
     * 引数ありコンストラクタ 先月 翌月のインスタンスを作成するために使うコンストラクタ
     * @param localdate １ヶ月前のLocalDateインスタンスや、１ヶ月後のLocalDateインスタンスが引数として渡ってくる
     */
    public Monthly(LocalDate localdate) {
        this.year = localdate.getYear();
        this.month = localdate.getMonthValue();
        this.day = localdate.getDayOfMonth();
        this.startWeek = LocalDate.of(this.year, this.month, 1).getDayOfWeek().getValue();
        this.beforeMonthLastDay = localdate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        this.thisMonthLastDay = localdate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        Map<Integer, int[]> map = this.creatCalendarDay( this.startWeek,  this.beforeMonthLastDay,  this.thisMonthLastDay);
        for(Map.Entry<Integer, int[]> entry : map.entrySet()) {
            this.calendarDay = entry.getValue();
            this.weekCount = entry.getKey();
        }
    }

	 /**
     * 先月と今月と来月の日付を格納して返すメソッド キーがint型今週何週あるのか 値が日付を格納したint型配列
     * @param startWeek
     * @param beforeMonthlastDay
     * @param thisMonthlastDay
     * @return Map<Integer, int[]>    Integer: weekCount<br /> int[]: calendarDay
     */
    public Map<Integer, int[]> creatCalendarDay(int startWeek, int beforeMonthlastDay, int thisMonthlastDay) {
        Map<Integer, int[]> map = new LinkedHashMap<>(); // 格納した順番を守る LinkedHashMapを使う
        int weekCount = 0;
        int[] calendarDay = new int[42];  //  最大で7日×6週
        int count = 0;
        /* 先月分の日付を格納する  　　 -2 じゃなくて -1 に変更した　　　　　*/
        for (int i = startWeek - 1 ; i >= 0 ; i--){
          calendarDay[count++] = beforeMonthlastDay - i;
        }
        /* 今月分の日付を格納する */
        for (int i = 1 ; i <= thisMonthlastDay ; i++){
          calendarDay[count++] = i;
        }
        /* 翌月分の日付を格納する */
        int nextMonthDay = 1;
        while (count % 7 != 0){
          calendarDay[count++] = nextMonthDay++;
        }
        weekCount = count / 7;
       map.put(weekCount, calendarDay);
        return map;
    }
    
    // アクセッサ  ゲッターとセッター
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(int startWeek) {
		this.startWeek = startWeek;
	}

	public int getBeforeMonthLastDay() {
		return beforeMonthLastDay;
	}

	public void setBeforeMonthLastDay(int beforeMonthLastDay) {
		this.beforeMonthLastDay = beforeMonthLastDay;
	}

	public int getThisMonthLastDay() {
		return thisMonthLastDay;
	}

	public void setThisMonthLastDay(int thisMonthLastDay) {
		this.thisMonthLastDay = thisMonthLastDay;
	}

	public int[] getCalendarDay() {
		return calendarDay;
	}

	public void setCalendarDay(int[] calendarDay) {
		this.calendarDay = calendarDay;
	}

	public int getWeekCount() {
		return weekCount;
	}

	public void setWeekCount(int weekCount) {
		this.weekCount = weekCount;
	}
    
}
