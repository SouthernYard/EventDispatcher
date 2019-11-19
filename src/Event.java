
/**
 * @author: Shawn
 * @Date: 10/24/2019
 * @Description:
 */
public class Event {

  private EventTypeEnum type;

  private Object data;

  public Event(EventTypeEnum type, Object data) {
    this.type = type;
    this.data = data;
  }
  public Event(EventTypeEnum type) {
    this.type = type;
  }

  public EventTypeEnum getType() {
    return type;
  }

  public void setType(EventTypeEnum type) {
    this.type = type;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
