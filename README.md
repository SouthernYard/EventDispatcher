# 事件分发器/EventDispatcher
Java实现的事件分发器，有任何疑问可以在当前仓库下给我提issue。具体的分析请参考我的博客：[事件分发器](https://www.itnote.tech/2019/11/06/%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E5%99%A8%EF%BC%88%E8%AE%A2%E9%98%85-%E5%8F%91%E5%B8%83%E6%A8%A1%E5%BC%8F%EF%BC%89%E7%9A%84%E5%AE%9E%E7%8E%B0/)

A implementation of event dispatcher in Java,if you have any question, please propose issue for me in current repo. Please refer to my blog: [Event dispatcher](https://www.itnote.tech/2019/11/06/%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E5%99%A8%EF%BC%88%E8%AE%A2%E9%98%85-%E5%8F%91%E5%B8%83%E6%A8%A1%E5%BC%8F%EF%BC%89%E7%9A%84%E5%AE%9E%E7%8E%B0/)

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
    System.out.println(Thread.currentThread() + "==" + System.currentTimeMillis() +  " -- Current time:" + event.getData());
  }

  @OnEvent(eventType = EventTypeEnum.EVENT_TEST)
  private void processTest(Event event) throws InterruptedException {
    System.out.println(Thread.currentThread() + "==" + System.currentTimeMillis() + " : s"  + event.getData());
    TimeUnit.SECONDS.sleep(10L);
  }
}
```
在一个类中可以有多个订阅事件的成员方法, 不受限制.

### 发布者(分发器)
发布者则是一个分发器, 可以
