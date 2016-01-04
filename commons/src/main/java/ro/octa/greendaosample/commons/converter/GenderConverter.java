package ro.octa.greendaosample.commons.converter;

import ro.octa.greendaosample.commons.model.Gender;

/**
 * @author Octa on 1/4/2016.
 */
public class GenderConverter implements PropertyConverter<Gender, String> {

  @Override public Gender convertToEntityProperty(String databaseValue) {
    try {
      return Gender.valueOf(databaseValue);
    } catch (IllegalArgumentException e) {
      return Gender.NONE;
    }
  }

  @Override public String convertToDatabaseValue(Gender entityProperty) {
    return entityProperty.getGender();
  }

}
