## 04，05、JVM执行方法调用

### 方法调用指令

- invokestatic
- invokespecial
- invokevirtual
- invokeinterface
- invokedynamic

## 06、JVM处理异常

### Java语言规范定义的异常

 - Error

 - Exception

   - RuntimeException(unchecked exception)

     不需要显示的处理

   - checked exception

     - 需要程序显示的捕获，或者在方法声明中用throws关键字标注

### Java虚拟机如何捕获异常？

- 每个方法附带一个异常表，异常表中的每一个条目代表一个异常处理器，并且由from指针，to指针，target指针以及所捕获的异常类型构成
  - from指针和to指针标示了该异常处理器所监控的范围
  - target指针则指向异常处理器的起始位置

### Suppressed异常

 - 附加异常信息

   ```java
   public class AutoCloseableDemo implements AutoCloseable{
   
       private final String name;
       public AutoCloseableDemo(String name) {
           this.name = name; }
   
       @Override
       public void close() {
           throw new RuntimeException(name);
       }
   
       public static void main(String[] args) {
           try(AutoCloseableDemo demo1 = new AutoCloseableDemo("demo1");
               AutoCloseableDemo demo2 = new AutoCloseableDemo("demo2");
               AutoCloseableDemo demo3 = new AutoCloseableDemo("demo3");
               ){
               throw new RuntimeException("Initial");
           }
       }
   }
   ```

   执行方法异常信息为：

   ```java
   Exception in thread "main" java.lang.RuntimeException: Initial
   	at com.linsen.jvm.exception.AutoCloseableDemo.main(AutoCloseableDemo.java:24)
   	Suppressed: java.lang.RuntimeException: demo3
   		at com.linsen.jvm.exception.AutoCloseableDemo.close(AutoCloseableDemo.java:16)
   		at com.linsen.jvm.exception.AutoCloseableDemo.main(AutoCloseableDemo.java:25)
   	Suppressed: java.lang.RuntimeException: demo2
   		at com.linsen.jvm.exception.AutoCloseableDemo.close(AutoCloseableDemo.java:16)
   		at com.linsen.jvm.exception.AutoCloseableDemo.main(AutoCloseableDemo.java:25)
   	Suppressed: java.lang.RuntimeException: demo1
   		at com.linsen.jvm.exception.AutoCloseableDemo.close(AutoCloseableDemo.java:16)
   		at com.linsen.jvm.exception.AutoCloseableDemo.main(AutoCloseableDemo.java:25)
   ```



## 07、JVM是如何实现反射

### 方法的反射调用

```java
public class TestV1 {

    public static void target(int i) {
        new Exception("#" + i).printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        Class klass = Class.forName("com.linsen.jvm.reflect.TestV1");
        Method method = klass.getMethod("target", int.class);
        method.invoke(null, 0);
    }
}
java.lang.Exception: #0
	at com.linsen.jvm.reflect.TestV1.target(TestV1.java:13)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.linsen.jvm.reflect.TestV1.main(TestV1.java:19)
```



- `Method.invoke()`实际是委派给MethodAccessor处理
- MethodAccessor有两个具体的实现：
  - 通过本地方法直接实现反射调用
  - 使用委派模式
- 反射调用先是调用了 Method.invoke，然后进入委派实现（DelegatingMethodAccessorImpl），再然后进入本地实现（NativeMethodAccessorImpl），最后到达目标方法
- 方法的反射调用还有另一种生成动态字节码的实现，上面提到的委派模式就是为了可以在本地实现和动态实现之间切换
- 本地实现和动态实现切换的设置：
  - Dsun.reflect.inflationThreshold=15：当某个反射调用的调用次数在 15 之下时，采用本地实现；当达到 15 时，便开始动态生成字节码，并将委派实现的委派对象切换至动态实现，这个过程我们称之为 Inflation

### 反射的性能开销

- 变长参数方法导致的Object数组
- 基本类型的自动装箱，拆箱
- 方法内联

### 不能理解的名词概念

- 方法内联
- 逃逸分析
- 类型 profile

## 08，09、JVM实现invokedynamic

### 方法句柄

	- 方法句柄是一个强类型，能够被直接执行的引用
	- 方法句柄的类型（MethodType）是由所指向方法的参数类型以及返回类型组成的。它是用来确认方法句柄是否适配的唯一关键----*灵活同时也变得容易出错？*

### invokedynamic指令

在第一次执行 invokedynamic 指令时，Java 虚拟机会调用该指令所对应的启动方法（BootStrap Method），来生成前面提到的调用点（CallSite），并且将之绑定至该 invokedynamic 指令中。在之后的运行过程中，Java 虚拟机则会直接调用绑定的调用点所链接的方法句柄。

### 方法句柄的增删改操作

方法句柄的增删改操作都是通过生成另一个方法句柄实现的

- 增

  > MethodHandle.bindTo

- 删

  >MethodHandles.dropArguments

- 改

  > MethodHandle.asType

### 方法句柄的实现

​	Java 虚拟机会对 invokeExact 调用做特殊处理，调用至一个共享的、与方法句柄类型相关的特殊适配器中。这个适配器是一个 LambdaForm

	>通过vm参数：-XX:+UnlockDiagnosticVMOptions -XX:+ShowHiddenFrames来开启隐藏的方法

![image-20201116191459376](D:\MyConfiguration\lane.lin\AppData\Roaming\Typora\typora-user-images\image-20201116191459376.png)



***LambdaForm适配器流程***（可以通过在vm参数中，增加配置：-**Djava.lang.invoke.MethodHandle.DUMP_CLASS_FILES=true**来导出class文件）：

- 检查参数类型

  > ```
  > Invokers.checkExactType(var0, var3);
  > ```

- 在方法句柄执行次数超过一个阈值(Djava.lang.invoke.MethodHandle.CUSTOMIZE_THRESHOLD,默认值是127)时进行优化

  > ```
  > Invokers.checkCustomized(var0);
  > ```

- 最后，调用句柄的invokeBasic方法

  > ```
  > (var4 = (MethodHandle)var0).invokeBasic(var1, var2)
  > ```

### Lambda表达式

在 Java 8 中，Lambda 表达式也是借助 invokedynamic 来实现的，*Java 编译器利用 invokedynamic 指令来生成实现了函数式接口的适配器*（方法引用，则不会生成额外的方法）

- 捕获了局部变量(在lambda方法中有额外的变量)每次调用都会构造一个新的适配器实例，应该尽量使用非捕获的Lambda表达式
  - 虽然逃逸分析会解决上述的新建实例的开销，但是必须满足两个条件：**invokedynamic 指令所执行的方法句柄能够内联，和接下来的对 accept 方法的调用也能内联**

## 10、对象的内存布局

### 构造器

- 子类的构造器需要调用父类构造器：
  - 如果父类存在无参数构造器的话，该调用可以是隐式的，也就是说 Java 编译器会自动添加对父类构造器的调用
  - 如果父类没有无参数构造器，那么子类的构造器则需要显式地调用父类带参数的构造器

### 指针压缩

- 每个对象都有一个对象头，对象头由标记字段和类型指针所构成
  - 标记字段用于存储运行数据：哈希码，GC信息以及锁信息
  - 类型指针指向该对象的类
- 压缩指针（对应虚拟机选项：**-XX:+UseCompressedOops**，默认开启）
- 内存对齐（对应虚拟机选项：**-XX:ObjectAlignmentInBytes**，默认值为 8）
  - 默认情况下，Java虚拟机堆中对象的起始地址需要对齐至8的倍数
  - 对齐的目的：让字段只出现在同一个缓存行中
- 填充（padding）：如果一个对象用不到8N个字节，就需要填充

### 字段重排列

- Java虚拟机重新分配字段的先后顺序，以达到内存对齐的目的
- 重排列的原则：
  - 如果一个字段占据C个字节，那么该字段的偏移量需要对齐至NC。（举例：long类型字段占据8个字节，那么偏移量就只能8N=16>12个字节）
  - 字类所继承字段的偏移量，需要与父类对应字段的偏移量保持一致
- @Contended注释（Java 8引入）
  - 用来解决对象字段之间的虚共享（false sharing），Java虚拟机会让不同的@Contended字段处于独立的缓存行中



## 11,12、垃圾回收

### 如何判断“垃圾”

- 可达性分析，选定GC Roots对象(简单理解为由堆外指向堆内的引用)，从GC Roots对象出发，探索能够被引用到的对象
- GC Roots(包括但不限于):
  - Java方法栈帧中的局部变量；
  - 已加载类的静态变量；
  - JNI handles；
  - 已启动且未停止的Java线程；

### 如何回收

 - 清除(标记-清理)
    - 缺点：
      	- 会产生内存碎片
      	- 内存分配效率较低（需要借助于空闲列表来查找能够放入新建对象的内存）
 - 压缩(标记-整理)
   	- 可以解决内存碎片的问题，但是压缩算法的性能开销
 - 复制(标记-复制)
   	- 把内存划分，每次只使用一块空间(对应hotspot中新生代的eden，s1，s2的划分)
      	- 能够解决内存碎片的问题，但是堆空间的利用率不高

### Stop the world

- 在GC Roots出发探索的过程中，可能会存在误报（将引用设置为null）或者漏报（将引用设置为未被访问过的对象），一旦出现漏报，意味者仍在使用的对象被回收了
- 为了解决漏报的问题，传统的垃圾回收算法采用了**Stop-the-world**，停止其他非垃圾回收线程的工作
- **安全点**：Java 虚拟机中的 Stop-the-world 是通过安全点（safepoint）机制来实现的。当 Java 虚拟机收到 Stop-the-world 请求，它便会等待所有的线程都到达安全点，才允许请求 Stop-the-world 的线程进行独占的工作

### 分代理论

- 大部分的Java对象只存活一小段时间，存活下来的小部分对象则会存活很长一段时间
  - 堆分为新生代和老年代，新生代又分为Eden区以及大小相同的两个Survivor区
  - 默认情况，Java虚拟机采取的是一种动态分配策略（-XX:+UsePSAdaptiveSurvivorSizePolicy）,根据生成对象的速率，以及Survivor区的使用情况动态调整Eden区和Survivor的比例
  - 可以使用参数：-XX:SurvivorRatio来固定Eden区和Survivor区的比例

### TLAB（Thread Local Allocation Buffer）

- 由于堆是共享的，创建对象申请内存时需要同步，为了减少这中同步，引入了TLAB
- 具体是指：每个线程可以向Java虚拟机申请一段连续的内存(加锁)，作为线程私有的TLAB（对应的参数：**-XX:+UseTLAB**,默认开启）
- TLAB，线程需要维护两个重要的指针，一个指向TLAB中空余内存的起始位置，一个指向TLAB末尾(如果分配的字节加上内存起始位置小于末尾，则分配成功，否则需要线程重新申请新的TLAB)

### GC

#### Minor GC

- 当Eden区的空间耗尽了Java虚拟机便会触发一次Minor GC
- Minor GC时，Eden区和from指向的Survivor区中的存活对象会被复制(**标记-复制**)到to指向的Survivor区，并交换from和to指针，保证to指向的Survivor区是空的
- **晋升**：如果一个对象被复制的次数为15(虚拟机参数：-XX:+MaxTenuringThreshold)，那么该对象将被晋升至老年代，**另外**，如果单个Survivor区已经被占用了50%（虚拟机参数：-XX:TargetSurvivorRatio），那么较高复制次数的对象也将晋升到老年代

#### 卡表（Card Table）

- 引入解决问题：在Minor GC时，老年代的对象可能引用新生代的对象，所以在标记的过程中，需要扫描老年代中的对象，会引起全堆扫描，所以引入了卡表的概念
- 将整个堆划分为一个个大小为512字节的卡，并且维护一个卡表，用来存储每张卡的一个标识位，这个标识位代表对应的卡是否可能存有指向新生代对象的引用。
- 卡表的维护：
  - 清零：在Minor GC的时候，寻找脏卡，完成所有扫描后，将标志位清零
  - 增加：在复制阶段，复制需要更新指向对象的引用，所以在复制阶段，更新引用的同时，又会更新卡表的标志位，可以确保脏卡中必然包含指向新生代对象的引用
- -XX:+UseCondCardMark：减少写卡表的操作（卡表中不同卡的标识位之间的虚共享问题）

## 13、Java内存模型



- as-if-serial
- Java内存模型
- happens-before：如果操作 X happens-before 操作Y，那么X的结果对于Y可见
  - 解锁操作happens-before之后对同一把锁的加锁操作
  - volatile字段的写操作happens-before之后对同一字段的读操作
  - 线程的启动操作happens-before该线程的第一个操作
  - 线程的最后一个操作happens-before它的终止事件（既其它线程通过Thread.isAlive()或Thread.join()判断该线程是否终止）
  - 线程对其它线程的中断操作happens-before被中断线程所收到的中断事件（即被中断线程的InterruptedException异常，或者第三个线程针对被中断线程的Thread.interrupted或者Thread.isInterrupted调用）
  - 构造器中的最后一个操作happens-before析构器的第一个操作



## 14、Java虚拟机实现Synchronized

- synchronized
  - 修饰代码块：monitorenter和monitorexit指令（计数器的方式可以允许同一个线程重复获取同一把锁）
  - 修饰方法：ACC_SYNCHRONIZED(隐式的的锁对象)
    - 实例方法：锁对象是this
    - 静态方法：所在类的Class实例
- 锁
  - 重量级锁：对于符合 posix 接口的操作系统（如 macOS 和绝大部分的 Linux），Java线程的阻塞以及唤醒是通过 pthread 的互斥锁（mutex）来实现的，需要从操作系统的用户态切换至内核态
  - 轻量级锁（CAS锁）：多个线程在不同的时间段请求同一把锁
  - 偏向锁：只有一个线程请求某一把锁
    - 加锁的过程：
      - 该锁对象支持偏向锁，那么 Java 虚拟机会通过 **CAS** 操作，将当前线程的地址记录在锁对象的标记字段之中，并且将标记字段的最后三位设置为 101
      - 每当有线程请求这把锁，Java 虚拟机只需判断锁对象标记字段中：最后三位是否为 101，是否包含当前线程的地址，以及 **epoch** 值是否和锁对象的类的 epoch 值相同（**只是判断相等，不会再进行CAS**）。如果都满足，那么当前线程持有该偏向锁，可以直接返回
    - -XX:BiasedLockingBulkRebiasThreshold：某一类锁对象的总撤销数(上述的**epoch**值来维护撤销的次数)超过了一个阈值，Java虚拟机会宣布这个类的偏向锁失效
    - -XX:BiasedLockingBulkRevokeThreshold：如果总撤销数超过另一个阈值，Java虚拟机会认为这个类已经不再适合偏向锁，并且在之后的加锁过程中直接为该类实例设置轻量级锁

## 15、Java语法糖与Java编译器

- 桥接方法
  - 解决问题：泛型的类型擦除带来不少问题，其中一个便是方法重写，经过类型擦除后，父亲的方法描述符和字类的方法描述符不一致而导致不符合方法重写的语义，引入的桥接方法
  - 实现：Java编译器额外添加了一个桥接方法，桥接方法在字节码层面重写了父类的方法，并调用子类的方法
  - 桥接方法在字节码的描述中会有ACC_SYNTHETIC,表示对源码不可见，所以想尝试直接调用会产生编译错误

## 16、即时编译上

- C1，C2，Graal

  - C1：对于执行时间较短，或者对启动性能有要求的程序，采用编译效率较快的C1，对应参数：-client
  - C2：对于执行时间较长，或者对峰值性能有要求的程序，采用生成代码执行效率较快的C2，对应参数：-server

- 分层编译

  - JDK1.7引入了分层编译（-XX:+TieredCompilation）

  - JDK1.8默认开启分层编译(-client，-server参数无效)，在打开分层编译下，使用参数(-XX:TieredStopAtLevel=1),直接由1层的C1进行编译

  - 分层：

    0. 解释执行
    1. 执行不带profiling的C1代码（由C1生成的机器码）---终止状态
    2. 执行仅带方法调用次数以及循环回边执行次数的profiling的C1代码
    3. 执行带所有profiling的C1代码
    4. 执行C2代码（由C2生成的机器码）---终止状态

  - 通常：C2代码的执行效率要比C1代码的高出30%以上，对于C1的三种状态：执行效率从高到低是：1>2>3

  - profiling是指在程序执行过程中，手机能够反映程序执行状态的数据，profiling越多，其额外的性能开销越大

  - 不同的编译路径：

    ![image-20201201110218839](D:\学习\study-notes\语言\Java\JVM\笔记\image\image-20201201110218839.png)
    - 

  - 即时编译：

    - 触发时机：

      - 不启用分层编译：-XX:CompileThreshold（当方法的调用次数和循环回边的次数的和超时参数值，C1:1500，C2:10000）

      - 启用分层编译：-XX:CompileThreshold失效，阈值动态调整：Java虚拟机会将阈值与某个系数s相乘，**该系数与当前待编译的方法数目成正相关，与编译线程的数目成负相关**

        ```java
        系数的计算方法为：
        s = queue_size_X / (TierXLoadFeedback * compiler_count_X) + 1其中X是执行层次，可取3或者4；
        queue_size_X是执行层次为X的待编译方法的数目；
        TierXLoadFeedback是预设好的参数，其中Tier3LoadFeedback为5，Tier4LoadFeedback为3；
        compiler_count_X是层次X的编译线程数目。
        ```

      - 在 64 位 Java 虚拟机中，默认情况下编译线程的总数目是根据处理器数量来调整的（对应参数 -XX:+CICompilerCountPerCPU，默认为 true；当通过参数 -XX:+CICompilerCount=N 强制设定总编译线程数目时，CICompilerCountPerCPU 将被设置为 false）

      - Java会将这些编译线程按照1:2的比例分配给C1和C2（至少各为1个）

  - 热点方法：

    - 方法的调用次数，循环回边的执行次数

  - OSR编译：

    - On-Stack-Replacement(OSR)编译

  - 去优化

    - -XX:+PrintCompilation(打印即时编译情况)
    - made not entrant:表示方法不能再被进入
    - made zombie:表示可以回收这段代码所占据的空间

## 17、即时编译下

- Profile
  - 分类：
    - 分支跳转字节码的分支profile(branch profile)
    - 包括跳转次数和不跳转次数，以及非私有实例方法调用指令，强制类型转换checkcast执行，类型测试instanceof指令，和引用类型的数组存储aastore指令的类型profile（receiver type profile）
  - 作用：
    - C2可以根据搜集得到的数据进行**猜测**，**假设**接下来的执行同样会按照所搜集的profile进行，从而做出比较激进的优化
- 优化
  - 基于分支profile的优化
    - 根据条件跳转指令的分支profile，即时编译器可以将从未执行过的分支剪掉，以避免编译这些很有可能不会用到的代码
    - 还会根据分支profile，计算每一条程序执行路径的概率，以便某些编译器优化优先处理概率较高的路径
  - 基于类型profile的优化
    - instanceof：
      - 如果instanceof的目标类型是final类型，那么Java虚拟机仅需比较测试对象的动态类型是否为该final类型
      - 如果目标类型不是final类型，虚拟机需要从测试对象的动态类型开始，依次测试该类，该类的父类，祖先类，该类所直接实现或者间接实现的接口是否与目标类型一致
- 去优化
  - 如果优化的假设失败了，则需要去优化，即从执行即时编译生成的机器码切换回解释执行
    - 在生成的机器码中，即时编译器将在假设失败的位置上插入一个陷阱(trap)，该陷阱实际上是一条call指令，调用至Java虚拟机里专门负责去优化的方法。与普通的call指令不一样的是，去优化方法将更改栈上的返回地址，并不再返回即时编译器生成的机器码中。

