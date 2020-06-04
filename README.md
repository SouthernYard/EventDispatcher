# 事件分发器/EventDispatcher
Java实现的事件分发器，有任何疑问可以在当前仓库下给我提issue。具体的分析请参考我的博客：[事件分发器](https://www.itnote.tech/2019/11/06/%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E5%99%A8%EF%BC%88%E8%AE%A2%E9%98%85-%E5%8F%91%E5%B8%83%E6%A8%A1%E5%BC%8F%EF%BC%89%E7%9A%84%E5%AE%9E%E7%8E%B0/)

An implementation of event dispatcher in Java,if you have any question, please propose issues to me in this repo. For the detailed explainations, please refer to my blog article, here is the reference link: [Event dispatcher](https://www.itnote.tech/2019/11/06/%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E5%99%A8%EF%BC%88%E8%AE%A2%E9%98%85-%E5%8F%91%E5%B8%83%E6%A8%A1%E5%BC%8F%EF%BC%89%E7%9A%84%E5%AE%9E%E7%8E%B0/)

## 使用方法

### 事件类型
如下面的代码所示, 我们预先定义了三个事件类型, `EVENT_NONE`, `EVENT_TIMER`和`EVENT_TEST`, 需要新的事件, 则可以在在`EventTypeEnum`中添加.
```Java
public enum EventTypeEnum {
  EVENT_NONE("noneEvent"),
  EVENT_TIMER("timer"),
  EVENT_TEST("test");

  String value;

  EventTypeEnum(String value) {
    this.value = value;
  }
}
```
### 订阅者
订阅者可以是任意一个成员方法, 默认事件类型是NONE, 如果需要订阅具体的事件类型,那就需要在注解OnEvent中设定特定的事件类型参数. 可以参考如下代码.
``` Java
public class Subscriber {

  @OnEvent(eventType = EventTypeEnum.EVENT_TIMER)
  private void getTimer(Event event) {
    System.out.println(Thread.currentThread() 
        + "==" + System.currentTimeMillis() 
        +  " -- Current time:" + event.getData());
  }

  @OnEvent(eventType = EventTypeEnum.EVENT_TEST)
  private void processTest(Event event) throws InterruptedException {
    System.out.println(Thread.currentThread() 
        + "==" + System.currentTimeMillis() 
        + " : s"  + event.getData());
    TimeUnit.SECONDS.sleep(10L);
  }
}
```
在一个类中可以有多个订阅事件的成员方法, 不受限制.

### 发布者(分发器)
发布者则是一个分发器, 在设计原理上可以参考我在文章开头所展示的博文。
1. 需要有订阅对象
2. 分发器让订阅对象订阅事件，否则订阅行为不生效
3. 启动分发器

如下代码所示，`Subscriber`同时订阅了`EVENT_TIMER`和`EVENT_TEST`事件，同时`Subscriber`在处理`EVENT_TEST`是阻塞的，但是这并不影响`EVENT_TIMER`事件的分发，因此事件的分发是非阻塞的。但是需要注意的一点是，
因为订阅者实际是类中的成员方法，方法之间存在对成员变量的修改时，需要注意并发问题。

``` Java
  public static void main(String[] args) throws InterruptedException {

    Subscriber subscriber = new Subscriber();
    EventDispatcher eventDispatcher = new EventDispatcher();
    eventDispatcher.subscribe(EventTypeEnum.EVENT_TIMER, subscriber);
    eventDispatcher.subscribe(EventTypeEnum.EVENT_TEST, subscriber);
    eventDispatcher.start();
    while (true) {
      TimeUnit.SECONDS.sleep(3);
      eventDispatcher.putEvent(new Event(EventTypeEnum.EVENT_TEST, "Block code test"));
    }
  }
```

```
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769005160 -- Current time:Sat May 30 00:16:45 CST 2020
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769006142 -- Current time:Sat May 30 00:16:46 CST 2020
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769007141 : sBlock code test
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769007142 -- Current time:Sat May 30 00:16:47 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769008147 -- Current time:Sat May 30 00:16:48 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769009148 -- Current time:Sat May 30 00:16:49 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769010146 : sBlock code test
Thread[ForkJoinPool.commonPool-worker-3,5,main]==1590769010152 -- Current time:Sat May 30 00:16:50 CST 2020
Thread[ForkJoinPool.commonPool-worker-3,5,main]==1590769011152 -- Current time:Sat May 30 00:16:51 CST 2020
Thread[ForkJoinPool.commonPool-worker-3,5,main]==1590769012152 -- Current time:Sat May 30 00:16:52 CST 2020
Thread[ForkJoinPool.commonPool-worker-3,5,main]==1590769013151 : sBlock code test
Thread[ForkJoinPool.commonPool-worker-4,5,main]==1590769013156 -- Current time:Sat May 30 00:16:53 CST 2020
Thread[ForkJoinPool.commonPool-worker-4,5,main]==1590769014161 -- Current time:Sat May 30 00:16:54 CST 2020
Thread[ForkJoinPool.commonPool-worker-4,5,main]==1590769015166 -- Current time:Sat May 30 00:16:55 CST 2020
Thread[ForkJoinPool.commonPool-worker-4,5,main]==1590769016152 : sBlock code test
Thread[ForkJoinPool.commonPool-worker-5,5,main]==1590769016169 -- Current time:Sat May 30 00:16:56 CST 2020
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769017169 -- Current time:Sat May 30 00:16:57 CST 2020
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769018172 -- Current time:Sat May 30 00:16:58 CST 2020
Thread[ForkJoinPool.commonPool-worker-1,5,main]==1590769019152 : sBlock code test
Thread[ForkJoinPool.commonPool-worker-5,5,main]==1590769019175 -- Current time:Sat May 30 00:16:59 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769020178 -- Current time:Sat May 30 00:17:00 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769021176 -- Current time:Sat May 30 00:17:01 CST 2020
Thread[ForkJoinPool.commonPool-worker-2,5,main]==1590769022153 : sBlock code test
```
