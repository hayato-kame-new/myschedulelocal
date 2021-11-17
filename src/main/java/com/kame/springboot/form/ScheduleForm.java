package com.kame.springboot.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.kame.springboot.annotation.HourCheck;
import com.kame.springboot.annotation.MinutesCheck;

// Formクラスに、相関チェック用のアノテーションをつけておきます 相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
@HourCheck(startHourProperty="startHour", endHourProperty="endHour", message = "終了は、開始以降にしてください") 
@MinutesCheck(startHourProperty="startHour", endHourProperty="endHour", startMinuteProperty="startMinute",  endMinuteProperty="endMinute" , message = "終了は、開始以降にしてください") 
public class ScheduleForm {
	
	
	// クラスにアノテーションはない
		// 入力値を保持するためのフォームクラス
		// このフォームクラスにユーザーの入力値を保持してデータのやり取りを行います。
	// コントローラのメソッドの引数に ScheduleForm scheduleForm とか @Validated ScheduleForm scheduleForm などと利用する
		
		// フォームには、入力チェックを行うためのアノテーションを付けていきます
		// Null チェックには、一般的には ＠NotNull を使いますが、
		// ここでは、＠NotBlank を使用しています
		// ＠NotNull 又は ＠NotEmpty を使用した場合、半角スペースのみでもユーザー名として登録ができてしまいますが、この半角スペースのユーザー名ではログインすることができないためです

	// フォームにidはいらない  編集でHTTPメソッドがPOSTアクセスの時必要だけど hiddenでパラメータで送るからコントローラでは@RequestParamで取得するから GETアクセス(表示だけの時は)は必要ない
	// あと、actionもhiddenでパラメータで別に送るので関係なし HTTPメソッドがPOSTアクセスの時コントローラでは@RequestParamで取得する
	// 削除をする時には、actionと　idだけあればいいから、フォームオブジェクトは要らない
	
	// フィールド宣言   各フォームのフィールドのname属性の値と同じ
	private Integer id;  // hiddenフィールドとして送る 本当は無くていい 編集や削除の時に必要 新規の時には nullで送られるからnullを許可する
	
	private Integer userId;  // hiddenフィールドなので、本当は無くていい
	
	@NotNull(message = "年を選択してください")
	private Integer year;  // 年 selectタグからの送信 name属性の値が year  年は、現在の年から前後2年を計算して、selectボックスに表示するようにする
	
	@NotNull(message = "月を選択してください")
	@Range(min= 1 , max= 12 )  // @Rangeは数値の値で、最小値と最大値を指定するものです。@Min、@Maxをまとめて設定するもの
	private Integer month;   // 月 selectタグからの送信 name属性の値が month
	
	@NotNull(message = "日を選択してください")
	@Range(min= 1 , max= 31 )  // @Rangeは数値の値で、最小値と最大値を指定するものです。@Min、@Maxをまとめて設定するもの
	private Integer day;  // 日 selectタグからの送信 name属性の値が day
	
	@NotNull(message = "開始時間の時間を選択してください")
	private Integer startHour;   //  開始時間の時間 selectタグからの送信 name属性の値が startHour
	
	@NotNull(message = "開始時間の分を選択してください")
	private Integer endHour;   // 開始時間の分 selectタグからの送信 name属性の値が startHour
	
	@NotNull(message = "終了時間の時間を選択してください")
	private Integer startMinute;   // 終了時間の時間 selectタグからの送信 name属性の値が startHour
	
	@NotNull(message = "終了時間の分を選択してください")
	private Integer endMinute;   // 終了時間の分 selectタグからの送信 name属性の値が startHour
	
	@Size(min=1, max=66 ,message = "スケジュールの件名は1文字以上66文字以下で入力してください")
	@NotEmpty(message = "スケジュールの件名を入力してください")  // @NotNullはStringには機能しないので @NotEmptyを使う、Null 空文字"" をエラーとする   ただし" " 半角空白は通るエラーとならない
	private String scheduleSubject;  // スケジュールの件名  必須
	
	@Size(min=0, max=66 ,message = "スケジュールのメモはユーザー名は66文字以下で入力してください")
	private String scheduleMemo;  // スケジュールのメモ  Nullを許可する
	
	

	// アクセッサ
	public Integer getId() {  
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getStartHour() {
		return startHour;
	}

	public void setStartHour(Integer startHour) {
		this.startHour = startHour;
	}

	public Integer getEndHour() {
		return endHour;
	}

	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}

	public Integer getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(Integer startMinute) {
		this.startMinute = startMinute;
	}

	public Integer getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(Integer endMinute) {
		this.endMinute = endMinute;
	}

	public String getScheduleSubject() {
		return scheduleSubject;
	}

	public void setScheduleSubject(String scheduleSubject) {
		this.scheduleSubject = scheduleSubject;
	}

	public String getScheduleMemo() {
		return scheduleMemo;
	}

	public void setScheduleMemo(String scheduleMemo) {
		this.scheduleMemo = scheduleMemo;
	}


	

}
