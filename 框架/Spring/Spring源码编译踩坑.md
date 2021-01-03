# 依赖工具
    - gradle 4.9
    - git spring 5.0.x
    - jdk1.8+
    - IDEA

# 问题

1.  版本号的选择

    最新的Spring源码对jdk的要求大于jdk1.8，所以要将spring的版本切换到5.1.x分支上

2.  文件目录权限不足
- 现象：Could not open/create prefs root node Software\JavaSoft\Prefs at root 0x80000002
- 解决：这个是由于windows操作系统引起的问题，打开REGEDIT，然后找到HKEY_CURRENT_USER\Software\JavaSoft\Prefs，右键权限，修改为完全控制即可

3.  kotlin版本冲突
- 现象：Could not initialize class org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSetKt
- 解决：这个是因为gradle中定义的kotlin插件版本与idea中定义的插件版本冲突造成的，查看idea(setting->plugin->kotlin)中设置的版本号，然后修改build.gradle文件中的版本号：
  
  plugins {
    id "io.spring.dependency-management" version "1.0.7.RELEASE" apply false
	id "org.jetbrains.kotlin.jvm" version "1.3.72" apply false
	id "org.jetbrains.dokka" version "0.9.18"
	id "org.asciidoctor.convert" version "1.5.8"
	}
    
    kotlinVersion        = "1.3.72"

4.  循环依赖以及找不到依赖
- 现象：循环依赖+ D:\work\spring-framework\spring-beans\src\main\kotlin\org\springframework\beans\factory\BeanFactoryExtensions.kt: (25, 30): Unresolved reference: BeanFactory
- 解决：没有太好的解决办法，直接换了一套版本号，现在最终的版本是：==Spring 5.0.x,gradle:4.9==