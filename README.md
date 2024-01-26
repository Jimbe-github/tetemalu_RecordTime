修正したファイルだけ置いています。<br>
(MonthFragment のアダプタは回答同様 MonthFrament に含めました。)

日付のクリックの検出用に Adapter に setDateClickListener メソッドを追加しています。<br>
クリックからの画面遷移には Fragment Result を使っています。

https://developer.android.com/guide/fragments/communicate?hl=ja#fragment-result

このようにすることで MonthFragment には親が何なのか/遷移なのかすら記述は無くなります。

DateFragment 内に配置する DailyCalendarFragment は DateFragment のコンストラクタで設定しています。
