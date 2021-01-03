## 垃圾收集器

### Serial

- 工作在新生代，采用标记复制算法，适用于客户端模式
- 单线程

### Serial Old

- Serial收集器的老年代版本，单线程收集器，采用标记整理算法

### ParNew

- Serial的多线程并行版本(并行是指多个垃圾收集线程协同工作）
- 除了Serial，目前只有ParNew可以和CMS一起配合工作

### Parallel Scavenge

- JDK1.4已经存在

- 工作在新生代，基于标记-复制算法实现
- 更关注于吞吐量（吞吐量=运行用户代码时间/（运行用户代码时间+运行垃圾收集时间））

- 吞吐量和停顿时间的关系：不是说停顿时间越短，吞吐量越高，这个里面垃圾收集的频次也是很关键的一点

### Parallel Old

- JDK 6提供
- Parallel Scavenge的老年代版本，基于标记-整理算法实现
- （Parallel Scavenge + Parallel Old）适用于注重吞吐量或者处理器资源较为稀缺

### CMS（Concurrent Mark Sweep）

- JDK1.5
- 基于标记-清除算法实现
- 由于关注点不同（CMS关注更短的停顿时间，Parallel Scavenge更关注吞吐量），还有实现框架不同（Parallel Scavenge，G1都没有使用垃圾收集器的分代框架），无法和Parallel Scavenge配合使用
- 适用于互联网网站或者基于浏览器的B/S系统
- 垃圾收集过程：
  - 初始标记（CMS initial mark）:
    - 标记一下GC Roots能直接关联到的对象
    - 仍然需要Stop The World,但是时间很短
  - 并发标记（CMS concurrent mark）
    - 从GC roots的直接关联对象开始遍历整个对象图的过程
    - 用时较长但是不需要停顿用户线程
  - 重新标记（CMS remark）
    - 为了修正并发标记阶段，用户线程继续工作而产生变化的那部分对象的标记记录
    - 需要Stop The World，耗时比初始标记时间长，但是远比并发标记阶段耗时短
  - 并发清除（CMS concurrent sweep）
    - 清理删除掉标记阶段判断的已经死亡的对象
    - 由于采用的是清除算法，不需要移动，所以不需要停顿用户线程（***如果此时用户线程继续工作又将对象的引用发生变化怎么办？***）
- 缺点：
  - 对处理器资源非常敏感（CMS默认启动的回收线程数是（处理器核心数量 + 3）/ 4），当处理器核心数量不足4个时，对用户程序的影响可能会变得很大
  - 由于CMS无法处理“浮动垃圾”,有可能出现“Concurrent Model Failure”失败而导致一次完全的“Stop The World”的Full GC产生
    - 由于并发标记和并发清理阶段，用户线程是继续运行的，还会产生新的垃圾，所以，CMS不能等到老年代满了之后才进行垃圾回收（-XX:CMSInitiatingOccu-pancyFraction控制触发CMS的百分比）
  - 基于清除算法实现垃圾回收，所以会产生内存碎片