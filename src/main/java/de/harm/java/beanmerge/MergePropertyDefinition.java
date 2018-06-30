package de.harm.java.beanmerge;

import lombok.Data;

@Data
public class MergePropertyDefinition<S, T> {

  private final String leftProp;
  private final AccessReader leftRead;
  private final String rightProp;
  private final AccessWriter rigthWriter;


  public MergePropertyDefinition(String leftProp, AccessReader leftRead, String rightProp, AccessWriter rigthWriter) {
    this.leftProp = leftProp;
    this.leftRead = leftRead;
    this.rightProp = rightProp;
    this.rigthWriter = rigthWriter;
  }


}