/**
 * 
 */
package com.kame.springboot.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.kame.springboot.validator.HourCheckValidator;

@Documented
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE})
@Constraint(validatedBy = {HourCheckValidator.class}) // バリデータクラスを指定する
/**
 * 注釈型のクラス(アノテーション型のクラス)
 * @author skame
 * 
 * @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) この書き方でもいい
 * @Retention(RetentionPolicy.RUNTIME) この書き方でもいい
 */
public @interface HourCheck {

	// String message() default "終了時間は、開始時間以降の時間にしてください";

	 Class<?>[] groups() default {};

	  Class<? extends Payload>[] payload() default {};
	  // つけたし相関チェック  @Targetに、「TYPE」を指定します  相関チェックの場合は項目にアノテーションはつけられません
	  // 単一の項目と違い、相関チェックの場合は項目にアノテーションをつけることができません。ひとつしか、項目がとれませんから

	  // だから、アノテーションをFormクラスにつける必要があり、そのためには@TargetはTYPEにしないといけないわけです。
	  // あと、単項目と違ってアノテーションが受け取るオブジェクトがFormクラスそのものになるので、実装クラスではFormクラス内の各項目を参照することになります。
	  // Formクラスの宣言の上に  @DayCheck(hireDateProperty="hireDate", retirementDateProperty="retirementDate", message = "退社日は、入社日の後の日付にしてください")  をつけてください
	  
	  // インタフェースにその項目を取得するためのメソッド定義だけしておく フィールドの名前を取得する？
	  String startHourProperty();
      String endHourProperty();
      String message();

      @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
      @Retention(RetentionPolicy.RUNTIME)
      @Documented
      public @interface List {
    	  HourCheck[] value();
       }


}
