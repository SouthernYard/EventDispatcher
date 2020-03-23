import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher {

  private volatile boolean isActive;
  private Thread timer;
  private Queue<Event> eventQueue;
  private Thread run;
  private Long interval = 1000L;
  private Map<EventTypeEnum, ConcurrentHashMap<Integer, WeakReference<Object>>> events;

  public EventDispatcher() {
    this.init();
  }

  public EventDispatcher(Long interval) {
    this.interval = interval;
    this.init();
  }

  private void init() {
    eventQueue = new ConcurrentLinkedQueue<>();
    events = new ConcurrentHashMap<>();
    timer = new Thread(() -> {
      while (isActive) {
        try {
          Thread.sleep(interval);
          putEvent(new Event(EventTypeEnum.EVENT_TIMER, new Date()));
        } catch (Exception e) {
          System.out.println("Failed to generate timer event"+ e);
        }
      }
    });
    run = new Thread(() -> {
      while (isActive) {
        try {
          Event event = eventQueue.poll();
          publish(event);
        } catch (Exception e) {
          System.out.println("Failed to publish event"+ e);
        }
      }
    });
  }

  public void start() {
    this.isActive = true;
    this.run.start();
    this.timer.start();
  }

  public void stop() {
    this.isActive = false;
    try {
      this.run.join();
      this.timer.join();
    } catch (Exception e) {
      System.out.println("Failed to stop Event engine and timer event"+ e);
    }
  }

  public void close() {
    stop();
  }

  public void subscribe(EventTypeEnum eventType, Object subscriber) {
    if (!events.containsKey(eventType)) {
      events.put(eventType, new ConcurrentHashMap<>());
    }
    // Add
    events.get(eventType).put(subscriber.hashCode(), new WeakReference<>(subscriber));
  }

  /**
   * Unsubcribe eventType
   *
   * @param eventType
   * @param subscriber
   * @return: true: subscriber has unscribe the eventType sunccessfully.
   * false: there is no this eventType in the events, no need to unscribe.
   */
  public boolean unsubscribe(EventTypeEnum eventType, Object subscriber) {
    if (events.containsKey(eventType)) {
      events.get(eventType).remove(subscriber.hashCode());
      return true;
    }
    return false;
  }

  private void publish(Event event) {
    if (event != null && events.containsKey(event.getType())) {
      for (Map.Entry<Integer, WeakReference<Object>> subs : events.get(event.getType()).entrySet()) {
        WeakReference<Object> subscriberRef = subs.getValue();
        Object subscriberObj = subscriberRef.get();
        assert subscriberObj != null;
        for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
          OnEvent annotation = method.getAnnotation(OnEvent.class);
          if (annotation != null && annotation.eventType().equals(event.getType())) {
//             异步处理事件,但是如果事件处理函数也是阻塞的，虽然能异步处理，但是JVM会创建大量阻塞线程
             CompletableFuture.runAsync(() -> deliverEvent(subscriberObj, method, event));
          }
        }
      }
    }
  }

  public boolean putEvent(Event event) {
    return eventQueue.offer(event);
  }

  private <T> boolean deliverEvent(T subscriber, Method method, Event event) {
    try {
      boolean methodFound = false;
      for (final Class paramClass : method.getParameterTypes()) {
        if (paramClass.equals(event.getClass())) {
          methodFound = true;
          break;
        }
      }
      if (methodFound) {
        method.setAccessible(true);
        method.invoke(subscriber, event);
      }

      return true;
    } catch (Exception e) {
      System.out.println("Deliver message has failed"+ e);
    }
    return false;
  }
}
