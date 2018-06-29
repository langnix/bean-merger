package de.harm.test.mergetest;


import lombok.Data;

import java.util.function.BiConsumer;

@Data
public class AccessWriter {

  private final BiConsumer biConsumer;
  private final Class valueClass;


  public void accept(Object obj, Object value) {
    biConsumer.accept(obj, value);
  }
}
