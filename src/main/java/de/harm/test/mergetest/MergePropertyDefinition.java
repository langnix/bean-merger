package de.harm.test.mergetest;

import java.util.function.Function;
import lombok.Data;

@Data
public class MergePropertyDefinition<S, T> {

  private final String leftProp;
  private final Function<S, ?> leftRead;
  private final String rightProp;


  public MergePropertyDefinition(String leftProp, Function<S, ?> leftRead, String rightProp) {
    this.leftProp = leftProp;
    this.leftRead = leftRead;
    this.rightProp = rightProp;
  }


}