### 扫描条码 私有仓库使用方法
```
// gradle 全局配置增加maven 私有地址
allprojects {
    repositories {
        maven{
            url 'http://10.5.52.101:8081/nexus/content/groups/mobile/'
        }
    }
}
//app
dependencies {
    implementation ('com.codyy.mobile:barcode-scanner:1.0.3'){
            exclude group: 'com.android.support'//主工程有support包,则剔除
    }
}
```