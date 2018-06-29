package de.harm.test.mergetest;


import java.util.function.BiConsumer;
import lombok.Data;

@Data
public class AccessWriter {

  private final BiConsumer biConsumer;
  private final Class valueClass;


  public void accept(Object obj, Object value) {
    biConsumer.accept(obj, value);
  }
}
