package com.kame.springboot.component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // コンポーネントにする。Bean化して使うクラスになる
public class ViewBean { // 他のクラスで@Autowiredをつけてフィールドに宣言するだけで、自動でBeanのインスタンスが生成されるようになる
    
	// ビューコンポーネントのクラス
	// フィールド宣言あればする

	// このクラスのインスタンスがBeanとして登録される際には、この@Autowiredがついたコンストラクタによってインスタンス生成されます
	@Autowired // このクラスを コンポーネントにするには、 コンストラクタに、@Autowired を付けます
	public ViewBean() {
		super();
	}

	// あとは、インスタンスメソッドを定義すればいい

	/**
	 * monthlyDisplay.htmlで使う 月のカレンダーの表示に使うMapを作る
	 * その月のカレンダーの日付が入っている配列をリストに変換した後で、リストを分割してMapにして返す
	 * 
	 * @param array
	 * @return Map<Integer, List<Integer>>
	 */
	public Map<Integer, List<Integer>> createWeekMap(int[] array) {

		// まず、引数で渡ってきた配列をリストに変換する
		List<Integer> list = new ArrayList<Integer>();
		for (int day : array) {
			list.add(day);
		}
		// 次にリストを6つに分割する 月のカレンダーの６週間分となる
		// リストをsubListメソッドを使うことにより、Listから必要な部分のみを新しいListとして取り出すことができます。
		List<Integer> firstWeek = list.subList(0, 7); // 0から6までのインデックス指定してます 7つの要素 例 [31, 1, 2, 3, 4, 5, 6]

		List<Integer> secondWeek = list.subList(7, 14); // 7から13までのインデックス指定してます 7つの要素  例 [7, 8, 9, 10, 11, 12, 13]
		List<Integer> thirdWeek = list.subList(14, 21); // 14から20までのインデックス指定してます 7つの要素  例[14, 15, 16, 17, 18, 19, 20]
		List<Integer> fourthWeek = list.subList(21, 28); // 21から27までのインデックス指定してます 7つの要素  例[21, 22, 23, 24, 25, 26, 27]
		List<Integer> fifthWeek = list.subList(28, 35); // 28から34までのインデックス指定してます 7つの要素  例[28, 29, 30, 1, 2, 3, 4]
		List<Integer> sixthWeek = list.subList(35, 42); // 35から41までのインデックス指定してます 7つの要素  例[0, 0, 0, 0, 0, 0, 0]

		// 次に キーが 月の週の何番目の週かを表し、 値が その週の日付のリスト となるマップを作る
		Map<Integer, List<Integer>> weekMap = new LinkedHashMap<>(); // 格納した順番を記憶するLinkedHashMap
		weekMap.put(1, firstWeek);
		weekMap.put(2, secondWeek);
		weekMap.put(3, thirdWeek);
		weekMap.put(4, fourthWeek);
		weekMap.put(5, fifthWeek);
		weekMap.put(6, sixthWeek);

		// マップを呼び出し元へ返す
		return weekMap;  // {1=[31, 1, 2, 3, 4, 5, 6], 2=[7, 8, 9, 10, 11, 12, 13], 3=[14, 15, 16, 17, 18, 19, 20], 4=[21, 22, 23, 24, 25, 26, 27], 5=[28, 29, 30, 1, 2, 3, 4], 6=[0, 0, 0, 0, 0, 0, 0]}
	}

	/**
	 * shedule.htmlで使う
	 * 
	 * LinkedListは格納する順番を記憶するリスト
	 * 
	 * @return
	 */
	public LinkedList<String> createTimeList() {
		// 格納順を記憶する LinkedList順番を保持する
		LinkedList<String> timeList = new LinkedList<String>();

		double doubleNumber = 0.0;
		String justAt = ""; // 00分を表す
		String halfAnHour = ""; // 30分を表す

		for (doubleNumber = 0.0; doubleNumber < 24.0; doubleNumber = doubleNumber + 0.5) {
			BigDecimal bigDecimal = new BigDecimal(String.valueOf(doubleNumber));
			int intValue = bigDecimal.intValue();
			// System.out.println(intValue);
			String strIntValue = String.format("%02d", intValue);  // 0で穴埋めする
			String deci = bigDecimal.subtract(new BigDecimal(intValue)).toPlainString();
			if (deci.equals("0.0")) {
				// System.out.println("0.0です");
				
				// justAt = intValue + ":00";
				justAt = strIntValue + ":00";  // 0埋め２桁にしたものと連結
				timeList.push(justAt);
			} else if (deci.equals("0.5")) {
				// System.out.println("0.5です");
				// halfAnHour = intValue + ":30";
				halfAnHour = strIntValue + ":30"; // 0埋め２桁にしたものと連結
				
				timeList.push(halfAnHour);
			}
		}
		Collections.reverse(timeList);
		// System.out.println(timeList.toString());
		
		 
		//  [00:00, 00:30, 01:00, 01:30, 02:00, 02:30, 03:00, 03:30, 04:00, 04:30, 05:00, 05:30, 06:00, 06:30, 07:00, 07:30, 08:00, 08:30, 09:00, 09:30, 10:00, 10:30, 11:00, 11:30, 12:00, 12:30, 13:00, 13:30, 14:00, 14:30, 15:00, 15:30, 16:00, 16:30, 17:00, 17:30, 18:00, 18:30, 19:00, 19:30, 20:00, 20:30, 21:00, 21:30, 22:00, 22:30, 23:00, 23:30]
		return timeList;
	}

	// monthlyDisplay.html schedule.htmlで使う
	// 現在の年と前後2年間のリストを作って返す
	public List<Integer> yearList() {
		LocalDate localDate = LocalDate.now();
		int year = localDate.getYear();

		List<Integer> yearList = new ArrayList<>(); // newして確保
		for (int i = -2; i <= 2; i++) {
			yearList.add(year + i);
		}
		return yearList;
	}
	
	// monthlyDisplay.html schedule.htmlで使う
		// フォームの分
		public List<Integer> minuteList() {			
			List<Integer> minuteList = Arrays.asList(0, 30);		
			return minuteList;    // [0, 30]
		}
}
