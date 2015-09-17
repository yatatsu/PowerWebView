# PowerWebView

PowerWebView is a wrapper of android.webkit.WebView.

## Feature

- WebSetting in xml. (also you can create instance in code.)
- Simple Callback of page loading event.
- Delegate SSL/BasicAuth handling.
- Intercept page loading. (evaluating and preventing.)
- RxBinding for page load event. (with rx plugin)

## See also

[kazy1991/LxWebView](https://github.com/kazy1991/LxWebView)

Basically same as LxWebView. The differences are...

- no UI parts like container in PowerWebView.
- Rx support.
- SSL/BasicAuth support.
- instance made by code will be same as one made by xml with no option.

## Licence

```
Copyright 2015 KITAGAWA, Tatsuya

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```