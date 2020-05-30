import java.util.concurrent.TimeUnit;

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