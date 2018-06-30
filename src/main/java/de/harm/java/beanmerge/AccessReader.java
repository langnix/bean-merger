package de.harm.java.beanmerge;


import lombok.Data;

import java.util.function.Function;

@Data
public class AccessReader implements Function {


  private final Function reader;
  private final Class valueClass;


  @Override
  public Object apply(Object o) {
    return reader.apply(o);
  }
}
