package de.harm.test.mergetest.model;

import lombok.Data;

@Data
public class TargetNested {

  private String one;

  private TargetContainer targetContainer;
}
