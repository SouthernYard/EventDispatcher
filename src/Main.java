/**
 * @author: Shawn
 * @Date: 10/18/2019
 * @Description:
 */
public class Main {

  public static void main(String[] args) {

    Subscriber subscriber = new Subscriber();
    EventDispatcher eventDispatcher = new EventDispatcher();
    eventDispatcher.subscribe(EventTypeEnum.EVENT_TIMER, subscriber);
    eventDispatcher.start();
  }
}
