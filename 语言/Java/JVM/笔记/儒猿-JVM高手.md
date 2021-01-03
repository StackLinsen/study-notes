### 2020-11-26

- GC Roots
- finialize()方法
- 引用类型
  - 强引用
  - 软引用：SoftReference<ReplicaManager>
    - 如果GC之后内存还是不够，回收软引用对象
  - 弱引用：WeakReference<ReplicaManager>
    - 如果发生GC就会回收弱以用
  - 虚引用：

### 2020-11-30

- 垃圾回收算法：
  - 复制：Eden区，Survivor区(Survivor0,Survivor1)
    - 为什么需要Survivor区域?  为什么需要两个？
- -XX:MaxTenuringThreshold：新生代进入老年代的年龄设置
- 动态年龄
- -XX:PretenureSizeThreshold：大对象直接进入老年代
- 老年代分配单保规则：
  - 新生代存活对象太多，Survivor区不够，往老年代迁移过程：
    - 在Minor GC之前，JVM老年代可用的内存空间，是否大于新生代所有对象的总大小
    - 如果老年代可用内存空间小于先生带所有对象总大小，判断**-XX:HandlePromotionFailure**(**JDK1.6之后废弃**)参数
    - 如果设置了**-XX:HandlePromotionFailure**参数，判断老年内的大小是否大于之前每一次Minor GC后进入老年代的对象的平均大小
    - 如果以上检查失败或者没有设置**-XX:HandlePromotionFailure**参数，则会触发一次Full GC，然后再执行Minor GC
    - 如果上面检查成功了，则会执行Minor GC：
      - Minor GC过后，剩余的存活对象小于Survivor大小，直接进入Survivor区域
      - 大于Survivor大小，但是小于老年代的大小，则直接进入老年代
      - 大于Survivor大小，也大于老年代的大小，则会触发一次Full GC
      - 如果Full GC之后，内存还是不够，则会导致OOM内存溢出了
- 标记整理算法