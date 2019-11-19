public class Subscriber {
  @OnEvent(eventType = EventTypeEnum.EVENT_TIMER)
  private void getTimer(Event event) {
    System.out.println("Current time:" + event.getData());
  }
}
