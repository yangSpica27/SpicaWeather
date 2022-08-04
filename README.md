
# SpicaWeather

## Screenshots
<p align="center">
<img src="/img/preview1.jpg" width="32%"/>
<img src="/img/preview2.jpg" width="32%"/>
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
  



MIT License

Copyright (c) 2022 杨为智

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

