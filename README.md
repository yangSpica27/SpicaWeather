
# SpicaWeather

## Screenshots
<p align="center">
<img src="/img/screen.jpg" width="32%"/>
<img src="/img/s2.jpg" width="32%"/>
</p>

## 技术栈及其使用的开源库
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/), [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- [Hilt](https://dagger.dev/hilt/) 依赖注入
- Jetpack
    - ViewModel
    - Room 
    - app-startup 
- ViewBinding
- 架构
    - MVVM为主，混合MVC
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - 网络请求框架
- [Sandwich](https://github.com/skydoves/Sandwich) - 网络请求相关处理的封装框架
- [Moshi](https://github.com/square/moshi/) Json解析
- [Coil](https://github.com/coil-kt/coil) 用于加载图片
- [Timber](https://github.com/JakeWharton/timber) -日志打印
- [Material-Components](https://github.com/material-components/material-components-android) - MD相关组件如recyclerview，cardView
- 自定义view
    - [SunriseView] - 自定义的日出日落view
    - [templeLineView] - 自定义的折线图
  
## 已知bug：
## 首次进入不能正常添加城市
## 二级进入后默认选中的城市会被覆盖
## 城市文本显示异常