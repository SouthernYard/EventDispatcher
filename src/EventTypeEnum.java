/**
 * @author: Shawn
 * @Date: 10/24/2019
 * @Description:
 */
public enum EventTypeEnum {
  EVENT_NONE("noneEvent"),
  EVENT_TIMER("timer"),
  EVENT_TEST("test");

  String value;

  EventTypeEnum(String value) {
    this.value = value;
  }
}
