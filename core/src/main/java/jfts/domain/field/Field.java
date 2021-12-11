package jfts.domain.field;

/**
 * Поле, аналог поля в таблице
 */
public interface Field {

    /**
     * Тип поля
     */
    public enum Type {
        CharSequence,
        Number
    }
}
