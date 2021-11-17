package com.kame.springboot.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity  // データベースと連携するエンティティクラス
@Table(name="schedule") // postgresだと、全て小文字なので小文字にする 子テーブル側になる
public class Schedule {
	
	// 入力フォーム用のフォームクラスも見てください
	// フォームクラスを作ったので、バリデーションのアノテーションはそちらにつければいいのですが一応こっちにもつけた

	@Id  // 主キー  @Entityをつけたら これがないと起動しません
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // カラム名は、postgreSQL 全て小文字なので小文字にする
	private int id;  // 新規の時には、nullで渡っていくので、バリデーションを@NotEmpty を付けない  リレーションのカラム
	
	@Column(name = "userid") // 小文字にする
	private int userId;  // リレーションのあるカラム
	
	@NotNull  // フォームのクラスがあるので、バリデーションはこっちじゃなくてフォームクラスにつければいい
	@Column(name = "scheduledate") // 小文字にする
	private LocalDate scheduleDate; // 年月日の情報
	
	@NotNull
	@Column(name = "starttime") // 小文字にする
    private LocalTime startTime;  // 開始の時間と分 LocalTimeに @NotBlank使えない
	
	@NotNull
	@Column(name = "endtime") // 小文字にする
    private LocalTime endTime; // 終了の時間と分 LocalTimeに @NotBlank使えない
	
	// javax.validation.constraints.NotEmpty
	@NotEmpty  // String型にはこれを使う。@NotNullだと、String型には効かない unique = true つけて、データベースのテーブルのカラムにも一意制約つける
	@Column(name = "schedulesubject") // 小文字にする
	@Size(min=1, max=66 ,message = "スケジュールの件名は1文字以上50文字以下で入力してください") //フォームのクラスがあるので、バリデーションは本当はこのクラスにつけないで、フォームのクラスにだけつければいい
    private String scheduleSubject;  //  件名  PostgreSQL でデータ型は varchar(67) にしてる これだと、日本語の漢字だと、66文字まで保存できます
    
	@Column(name = "schedulememo") // 小文字にする
	@Size(min=1, max=66 ,message = "スケジュールのメモはユーザー名は1文字以上50文字以下で入力してください")  // フォームのクラスがあるので、バリデーションは本当はこのクラスにつけないで、フォームのクラスにだけつければいい
	private String scheduleMemo; // メモ  nullを許可する  PostgreSQL でデータ型は varchar(67) にしてる これだと、日本語の漢字だと、66文字まで保存できます

	// テーブル同志のリレーション  エンティティとして@Entityをつけたクラス同士で対応する記述をする  @Entityをつけたら @Idをつけないと起動しないので注意する
	@ManyToOne  // 他はごちゃごちゃ要らない @ManyToOneにはMappedBy属性が存在しない
	User user;  // これでいいらしい
	
	
	/**
	 * コンストラクタ
	 */
	public Schedule() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	/**
	 * コンストラクタ id以外のフィールドを使う
	 * 新規の時に使う 引数は 6つあるコンストラクタ
	 * idのデータ型はserialです  シーケンス（データ型のserial）とは シーケンスとはINSERTで値を入れなくとも、自動で採番されるカラム
	 * 
	 * @param userId
	 * @param scheduleDate
	 * @param startTime
	 * @param endTime
	 * @param scheduleSubject
	 * @param scheduleMemo
	 */
	public Schedule(int userId, @NotBlank LocalDate scheduleDate, @NotBlank LocalTime startTime,
			@NotBlank LocalTime endTime, @NotEmpty String scheduleSubject, String scheduleMemo) {
		super();
		this.userId = userId;
		this.scheduleDate = scheduleDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.scheduleSubject = scheduleSubject;
		this.scheduleMemo = scheduleMemo;
	}
	
	/**
	 * 編集する時に使用するコンストラクタ 使わなくてもidだけあれば更新できるので今回は使わない
	 * 引数7つあるコンストラクタ
	 * @param id
	 * @param userId
	 * @param scheduleDate
	 * @param startTime
	 * @param endTime
	 * @param scheduleSubject
	 * @param scheduleMemo
	 */
	public Schedule(int id, int userId, @NotNull LocalDate scheduleDate, @NotNull LocalTime startTime,
			@NotNull LocalTime endTime, @NotEmpty String scheduleSubject, String scheduleMemo) {
		super();
		this.id = id;
		this.userId = userId;
		this.scheduleDate = scheduleDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.scheduleSubject = scheduleSubject;
		this.scheduleMemo = scheduleMemo;
	}

	/**
	 * 表示のためにstartTimeを文字列にする
	 * 引数なしです 自分自身のインスタンスのフィールドの値を使います
	 * @return
	 */
    public String createStrStartTime() {
        int hour = this.startTime.getHour(); // 0 とか  17 とかが取れる 自分自身のインスタンスのフィールドの値を使います
        int minute = this.startTime.getMinute(); // 0  とか  30  注意  0 だったら 00 にすべき 分は 00分
        String strHour = String.valueOf(hour);
        String strMinute = "";
        // 分で 0 が取れた時には、00 としないといけない
        if(minute == 0) {
            strMinute = String.format("%02d", minute);
        } else {
            strMinute = String.valueOf(minute);
        }
        return strHour + ":" + strMinute;  // 0:00 とか 17:30    分は 00分
    }



    /**
     * 表示のためにstartTimeを文字列にする
     * 引数なしです 自分自身のインスタンスのフィールドの値を使います
     * @return
     */
    public String createStrEndTime() {
          int hour = this.endTime.getHour(); // 0 とか  17 とかが取れる 自分自身のインスタンスのフィールドの値を使います
          int minute =  this.endTime.getMinute(); // 0  とか  30  注意  0 だったら 00 にすべき 分は 00分
          String strHour = String.valueOf(hour);
          String strMinute = "";
          // 分で 0 が取れた時には、00 としないといけない
          if(minute == 0) {
              strMinute = String.format("%02d", minute);
          } else {
              strMinute = String.valueOf(minute);
          }
          return strHour + ":" + strMinute;  // 0:00 とか 17:30    分は 00分

    }

    // 時間の差分を求める引数なし 自分自身のインスタンスのフィールドの値を使う
    public int rowCount() {
        long minutes = ChronoUnit.MINUTES.between(this.startTime, this.endTime);
        double hours = minutes / 60.0;
        double row =  hours * 2 ;
        return (int)row;
    }


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDate getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(LocalDate scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
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
