package de.harm.java.beanmerge.model;

import lombok.Data;

@Data
public class TargetNested {

  private String one;

  private TargetContainer targetContainer;
}
