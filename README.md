修正したファイルだけ置いています。<br>
(MonthFragment のアダプタは回答同様 MonthFragment に含めました。)

**パッケージ名が変わっています。**
こちらの都合と、 java では一般的にパッケージ名は小文字のためです。
(ついでにクラス名や変数名にアンダーバーは使いません。定数名は全部大文字にするのでアンダーバーを使います。)

日付のクリックの検出用に Adapter に setDateClickListener メソッドを追加しています。<br>
クリックからの画面遷移には Fragment Result を使っています。

https://developer.android.com/guide/fragments/communicate?hl=ja#fragment-result

このようにすることで MonthFragment には親が何なのか/遷移なのかすら記述は無くなります。<br>
MonthFragment にも Argments が必要になったため、生成用メソッド(getInstance) を DateFragment と共に作りました。

DateFragment 内に配置する DailyCalendarFragment は DateFragment のコンストラクタで設定しています。<br>
アクティビティがフラグメントマネージャ持っていて getSupportFragmentManager() で得られるように、
フラグメントも自身のフラグメントマネージャを持っていて getChildFragmentManager() で得られ、自身のレイアウトに配置するのに使えます。
