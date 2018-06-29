package de.harm.test.mergetest.model;

import java.util.List;
import lombok.Data;

@Data
public class SrcContainer {

  private String name;
  private Integer number;

  private List<String> roles;
}
