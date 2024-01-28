DateFragment 以降の処理を追加しています。<br>
DailyCalendarFragment とそのアダプタは DateFragment に吸収しました。<br>
rooms 関係は未完成のようだったので Json でローカルファイルに保存するように変更、 Gson を使っています。<br>
データ処理は MainViewModel で行いデータクラスもそちらで定義しています。ファイルの読み込みや変更は Executor で別スレッド動作としています。<br>
データの入力・修正は EntryDialogFragment で行っています。修正時にタイトルを消すと削除です。
