package com.kame.springboot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.kame.springboot.annotation.MinutesCheck;
/**
 *  * 日付のパラメータの比較を行うバリデータクラス.2つのパラメータをとる 相関チェック.
 * ConstraintValidatorの<>の中には、<アノテーションクラス, アノテーションを付与する（チェックを行う）エンティティクラス> を指定する.
 * ConstraintValidator<DayCheck, Object>  にする.Employeeを<>に書かないで 汎用的にするには Objectにする.
 * @author skame
 *
 */
public class MinutesCheckValidator implements ConstraintValidator< MinutesCheck, Object> {

	
	// フィールド     アノテーションクラスのメソッドと、名前を合わせたフィールド名にするといい
	String startMinuteProperty; // 入力された値 フォームクラスをみて
    String endMinuteProperty;  // 入力された値 
    String message;
    // 時間のフィールドもチェックする必要があるので、追加
    String startHourProperty; // 入力された値 
    String endHourProperty;  // 入力された値 
	
    /**
	 * 初期化処理
	 */
	@Override
	public void initialize(MinutesCheck constraintAnnotation) {
		// アノテーションの引数情報を設定する。
		this.startMinuteProperty = constraintAnnotation.startMinuteProperty();
		this.endMinuteProperty = constraintAnnotation.endMinuteProperty();
		this.message = constraintAnnotation.message();
		// 追加
		this.startHourProperty = constraintAnnotation.startHourProperty();
		this.endHourProperty = constraintAnnotation.endHourProperty();
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
	    	  
	    	  Integer startMinute =  (Integer) beanWrapper.getPropertyValue(startMinuteProperty);  // 未入力だと null
	    	  Integer endMinute =  (Integer) beanWrapper.getPropertyValue(endMinuteProperty);  // 未入力だと null
	    	  // 追加
	    	  Integer startHour =  (Integer) beanWrapper.getPropertyValue(startHourProperty);  // 未入力だと null
	    	  Integer endHour =  (Integer) beanWrapper.getPropertyValue(endHourProperty);  // 未入力だと null
	    	 
	    	  
	    	  if (startHour == null || endHour == null || startMinute == null || endMinute == null) {
	    		  return true;  // returnをさせてメソッドを即終了させるのは、他のバリデーションで対応してもらうため、です
	    		  // ここはreturnするここでメソッドの終了 引数をメソッドの呼び出しもとに返す returnキーワード      true を返せば、バリデーションエラーにならない	    		  
	    	  }
	    	  // 変数1.compareTo(変数2)  
	    	  // 戻り値 
	    	  // メソッドの呼び出し元の値(変数1)が、引数の値(変数2)より小さい場合は”負の値”
	    	  // メソッドの呼び出し元の値(変数1)が、引数の値(変数2)より大きい場合は”正の値”
	    	  // メソッドの呼び出し元の値(変数1)が、引数の値(変数2)と同じ場合は”0”
	    	  // 終了のHourが同じかそれより大きい なおかつ 終了のMinutesが 開始のMinutesと同じ または　小さい時にエラーは出ない trueにする
	    	  if(endHour.compareTo(startHour) < 0){
	    		  return true;  // returnしてください、
	    		  // returnでこのisValidメソッドを即終了させて、他のバリデーションを使うから trueを引数にすればエラーなしとなる
	    	  }
	    	  
	    	  
	    	  if(endHour.compareTo(startHour) >= 0  &&   endMinute.compareTo(startMinute) >= 0 ) { 
	    		  result = true;  // 代入だけ
	    	  }else {
	    	 
	    		  // エラーメッセージを出します。エラーメッセージを生成する
	    		  //メッセージを設定する。エラーメッセージを返すときの手続き
	    	        context.disableDefaultConstraintViolation();  // まず、デフォルトの制約違反情報をクリアします
	    	        // 今回は終了の分の下にエラーメッセージを表示したいので  addPropertyNode(this.endMinuteProperty) で、Formクラスの　終了の分の名前をセットしてます
	    	        context.buildConstraintViolationWithTemplate(message).addPropertyNode(this.endMinuteProperty).addConstraintViolation();
	    		  result = false;  // エラーメッセージを出す。
	    	  }
	    	  
	     }
	      return result;
		// 相関チェックのアノテーションは、Formクラスのクラス定義の部分に付与する  引数として項目名を渡す  各項目にはつけません
		

	}

}
