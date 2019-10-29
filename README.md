### 扫描二维码/条码 [![](https://jitpack.io/v/codyy-mobile/barcode-scanner.svg)](https://jitpack.io/#codyy-mobile/barcode-scanner)
```
// gradle 全局配置增加maven地址
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
//app
dependencies {
    implementation 'com.github.codyy-mobile:barcode-scanner:1.1.1'
    api 'com.google.zxing:core:3.3.3'//dependencies zxing core library
}
```