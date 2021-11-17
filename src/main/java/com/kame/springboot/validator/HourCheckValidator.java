package com.kame.springboot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.kame.springboot.annotation.HourCheck;
/**
 *  * 日付のパラメータの比較を行うバリデータクラス.2つのパラメータをとる 相関チェック.
 * ConstraintValidatorの<>の中には、<アノテーションクラス, アノテーションを付与する（チェックを行う）エンティティクラス> を指定する.
 * ConstraintValidator<DayCheck, Object>  にする.Employeeを<>に書かないで 汎用的にするには Objectにする.
 * @author skame
 *
 */
public class HourCheckValidator implements ConstraintValidator< HourCheck, Object> {

	
	// フィールド     アノテーションクラスのメソッドと、名前を合わせたフィールド名にするといい
	String startHourProperty; // 入力された値 
    String endHourProperty;  // 入力された値 
    String message;
	
    /**
	 * 初期化処理
	 */
	@Override
	public void initialize(HourCheck constraintAnnotation) {
		// アノテーションの引数情報を設定する。
		this.startHourProperty = constraintAnnotation.startHourProperty();
		this.endHourProperty = constraintAnnotation.endHourProperty();
		this.message = constraintAnnotation.message();
	}

	/**
	 * 比較処理
	 * false を返すとバリデーションエラーを発生
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		
		// valueは、フォームのオブジェクト Formクラスのオブジェクト
		 boolean result = true;
	      if( value == null){ 
	             result = true; // ここでメソッドの終了 呼び出しもとへtrueを返す
	      }else{
	    	  // フォームクラスから比較対象項目の値を得る SpringのBeanWrapperというインタフェースを使います
	    	  BeanWrapper beanWrapper = new BeanWrapperImpl(value);
	    	  
	    	  Integer startTime =  (Integer) beanWrapper.getPropertyValue(startHourProperty);  // 未入力だと null
	    	  Integer endTime =  (Integer) beanWrapper.getPropertyValue(endHourProperty);  // 未入力だと null
	    	  
	    	  if (startTime == null || endTime == null) {
	    		  return true;  // returnさせる理由は、このバリデーションを使わずに、他のバリデーションで対応するためです
	    		  
	    		  // ここでメソッドの終了 引数をメソッドの呼び出しもとに返す returnキーワード      true を返せば、バリデーションエラーにならない	    		  
	    	  }

	    	  // 終わりを前にしているので、終了 >= 開始  の時は、0以上の数値が返り、そうでないときはマイナス数値が返る	    	  
	    	  if(endTime.compareTo(startTime) >= 0) { 
	    		  result = true;  //   true 代入してるだけ、バリデーションエラーにならない
	    	  } else {
	    		  // エラーメッセージを出します。エラーメッセージを生成する
	    		  //メッセージを設定する。エラーメッセージを返すときの手続き
	    	        context.disableDefaultConstraintViolation();  // まず、デフォルトの制約違反情報をクリアします
	    	        // 今回は退社日の下にエラーメッセージを表示したいので  addPropertyNode(retirementDateProperty) で、Formクラスの　退職日の名前をセットしてます
	    	        context.buildConstraintViolationWithTemplate(message).addPropertyNode(this.endHourProperty).addConstraintViolation();
	    		  result = false;  // false代入してるだけエラーメッセージを出す。
	    	  }
	     }
	      return result;
		// 相関チェックのアノテーションは、Formクラスのクラス定義の部分に付与する  引数として項目名を渡す  各項目にはつけません
		
	}

}
