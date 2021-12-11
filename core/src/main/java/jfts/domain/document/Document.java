package jfts.domain.document;

import jfts.domain.field.Field;

import java.util.List;

/**
 * Аналог таблицы в бд
 * хранит мета-информацию:
 * кол-во полей, типы полей
 * МАПА полей?
 */
public interface Document {

    /**
     * получить список полей таблицы, скорее всего мапа
     * @return
     */
    List<Field> getListField();

}
