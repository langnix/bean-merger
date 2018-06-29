package de.harm.test.mergetest.model;

import lombok.Data;

@Data
public class SrcNested {

  private String fire;

  private SrcContainer srcContainer;
}
