# Android RutubeFramework

Поставляется в формате [aar](http://tools.android.com/tech-docs/new-build-system/aar-format).

##Зависимости

* com.android.support:appcompat-v7:22.2.0
* Rutube API

##Gragle

Для подключения библиотек достаточно скопировать в директорию `libs` и внести изменения в `gragle` файл. 

```
repositories{
    flatDir{
        dirs 'libs'
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.mcxiaoke.volley:library:1.0.+'
    compile(name:'api-release', ext:'aar')
    compile(name:'RutubePlayer-release', ext:'aar')
}
```

## Добавление плеера в Activity

Плеер наследуется от визуального класса [Fragment](http://developer.android.com/guide/components/fragments.html), благодаря чему его можно использовать как при димамической программной вставке, так и в визуальном редакторе.

В случае, если объект класса `RutubePlayer` был размещен в визуальном редакторе, получить ссылку на объект можно, например, следующим образом

```
FragmentManager fragmentManager = getFragmentManager();
RutubePlayer player = (RutubePlayer)fragmentManager.findFragmentById(R.id.rutubeFragment);
```
где `R.id.rutubeFragment` идентификатор фрагмента. 

Так же ***необходимо*** добавить в манифест приложения в настройки Activity, содержащего плеер,
свойство `android:configChanges="orientation|screenSize"`.

## Общий концепт взаимодействия с плеером

Взаимодействие с плеером происходит посредством плагинов.
Например, для того, чтобы добавить в плеер свои элементы управления достаточно создать плагин для плеера и подключить его.
Рассмотрим более подробно.

Плагины могут содержать визуальную составляющую, так и без нее.
Объект класса `RutubePlayer` предоставляет два метода через которые и происходит все остальное взаимодействие с плеером.

Сигнатура метода  | Описание
------------- | -------------
boolean attachPlugin( IRutubePlayerPlugin plugin )  | Добавить плагин в плеер
boolean deattachPlugin( IRutubePlayerPlugin plugin )  | Удалить плагин из плеера

Стоит принять во внимание, что как только плагин добавляется в плеер, он активно включается в его жизнь - начинает получать уведомления от `IRutubePlayer`.

###Рассмотрим более подробно интерфейс `IRutubePlayerPlugin`

Сигнатура метода  | Описание
------------- | -------------
void activate(IRutubePlayer player)  | Активирование плагина - добавляется в экосистему плеера. Передается ссылка на объект, реализующий интерфейс `IRutubePlayer`.
void deactivate(IRutubePlayer player)  | Отключение плагина из экосистемы плеера.
boolean hasView()  | Плеер интересуется предоставляет ли плагин визуальную составляющую. В случае отрицательного ответа метод `getView(Context context)` запрашиваться не будет. Невизуальные плагины отлично подходят в случае необходимости сбора статистики.
View getView(Context context)  | Плер запрашивает визуальную составляющую плагина.<BR/> <mark>Важно чтобы метод возвращал один и тот же объект!</mark>
boolean requestOperation( RutubePlayerOperation operation )| Плеер предоставляет плагину определяеть пропустить ли тот или иной запрос от других плагинов или нет. Используется при блокировках.


###Перейдем к рассмотрению интерфейса `IRutubePlayer`.
Часть из методов требуют передачи в качестве параметра ссылку на текущий плагин. Это сделано с целью реализации систем блокировок.

Сигнатура метода| Описание
----------|----------
boolean play(IRutubePlayerPlugin owner)|Запустить проигрывание ролика
boolean pause(IRutubePlayerPlugin owner)|Приостановить проигрывание ролика
boolean stop(IRutubePlayerPlugin owner)|Остановить проигрывание ролика
boolean reset(IRutubePlayerPlugin owner)|Сбросить проигрывание ролика, удалить связанный контекст.
boolean seek(int time, IRutubePlayerPlugin owner)|Перемотать на заданную позицию. Задается в миллисекундах.
boolean changeQuality(Integer qualityIndex, IRutubePlayerPlugin owner)|Изменить качество.
boolean changeVideo(String videoHash, IRutubePlayerPlugin owner)|Загрузить ролик по хешу.
int getQualityIndex()|Текущий индект качества.
String getVideoHash()|Хеш проигрываемого видео
int getDuration()|Длительность ролика
int getCurrentTime()|Текущее местоположение
int getBufferLength()|Количество забуфферизированного
boolean isPlaying()|Проигрывается ли ролик
boolean isAutoPlay()|Установлено ли автопроигрывание. Установка осуществляется методом `boolean play(IRutubePlayerPlugin owner)`  во время загрузки контекста ролика
RutubePlayerError getError()|Ошибка
RutubePlayerState getPlayerState()|Текущее состояние плеера
RutubeAdvertisementState getAdvertisementState()|Состояние рекламы
IPlayerContext getPlayerContext()|Получить контекст видео. Доступны как длительность, так и название ролика, пекшот и т.д.
boolean addListener(IRutubePlayerListener listener)|Подписаться на события от плеера
boolean removeListener(IRutubePlayerListener listener)|Отписатсья от событий от плеера
boolean lock( IRutubePlayerPlugin locker )|
boolean unlock( IRutubePlayerPlugin locker )|
boolean viewBringToTop(IRutubePlayerPlugin owner)|Поднять вьюху на самый верх списка
boolean viewPutBackward(IRutubePlayerPlugin owner)|Убрать вьюху назад


###Время интерфейса `IRutubePlayerListener`
Сигнатура метода| Описание
-----------|---------------
void changeVideo()|Произошла смена видео
void changeDuration(int duration)|Изменилась длительность ролика
void changeCurrentTime(int currentTime)|Изменилось текущее время проигрывания ролика
void changeQuality(Integer qualityIndex)|Изменилось качество ролика
void changePlayerState(RutubePlayerState state)|Изменилось состояние плеера
void changeAdvertisementState(RutubeAdvertisementState state)|Изменилось рекламное состояние плеера
void changeBufferPercents( int percents )|Изменилась забуфферизованная область
void changeBufferingState(boolean buffering)|Состояние буфферизации

##Example plugin creating

Рассмотрим процесс создания простейшего плагина для плеера

```
private Hello plugin;

...

FragmentManager fragmentManager = getFragmentManager();
RutubePlayer player = (RutubePlayer)fragmentManager.findFragmentById(R.id.rutubeFragment);

// Создаем плагин и добаляем в плеер
plugin = new Hello();
player.attachPlugin(plugin);

...

protected class Hello implements IRutubePlayerPlugin {

        private IRutubePlayer player;

        @Override
        public void activate(IRutubePlayer player) {
            this.player = player;
        }

        @Override
        public void deactivate(IRutubePlayer player) {

        }

        @Override
        public boolean hasView() {
            return false;
        }

        @Override
        public View getView(Context context) {
            return null;
        }

        @Override
        public boolean requestOperation( RutubePlayerOperation operation ) {return true;}

        public void runFailed() {
            player.changeVideo("6ecdd81a078bf0c2f7ebdeef997dd496", null);
            player.play(this);
        }

        public void runSuccess() {
            player.changeVideo("c3930488dfc3ee8b433a1c4793e661a5", null);
            player.play(this);
        }

    }
```

Для проигрывания видео остается, например, по тапу на кнопку вызвать метод `plugin.runFailed()` или `plugin.runSuccess()`.
