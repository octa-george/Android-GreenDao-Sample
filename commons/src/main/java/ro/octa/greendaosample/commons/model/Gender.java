package ro.octa.greendaosample.commons.model;

/**
 * @author Octa on 1/4/2016.
 */
public enum Gender {
  MALE("male"), FEMALE("female"), NONE("none");

  private String value;

  Gender(String value) {
    this.value = value;
  }

  public String getGender() {
    return value;
  }
}
