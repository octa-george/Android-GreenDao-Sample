package ro.octa.greendaosample.commons.converter;

/**
 * @author Octa on 1/4/2016.
 */
public interface PropertyConverter<P, D> {

  P convertToEntityProperty(D databaseValue);

  D convertToDatabaseValue(P entityProperty);
}
