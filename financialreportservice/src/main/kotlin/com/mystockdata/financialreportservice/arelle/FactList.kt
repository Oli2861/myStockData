package com.mystockdata.financialreportservice.arelle

import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.bind.annotation.*

@XmlRootElement(name = "factList")
data class FactList(
    var item: List<Item>? = null
)

/**
 * Contains values the type property of an Item can take.
 * @property MONETARY_ITEM The value is a monetary value.
 * @property PER_SHARE_ITEM The value is a per share value (decimal).
 * @property STRING_ITEM The value is a string.
 * @property DATE_ITEM The value is a date.
 */
enum class TYPE(val str: String){
    MONETARY_ITEM("xbrli:monetaryItemType"),
    PER_SHARE_ITEM("num:perShareItemType"),
    STRING_ITEM("xbrli:stringItemType"),
    DATE_ITEM("xbrli:dateItemType")
}

/**
 * Item retrieved via from the Arelle Webservice.
 * @param name Tag of a taxonomy identifying the item / value of the item e.g."ifrs-full:Assets". Does not distinguish between the same report item for different years (date has to be used to achieve that).
 * @param label Description, usually same text as in the name but in a more readable form.
 * @param unitRef Currency for monetary values.
 * @param value The value of the fact. Can be text, numeric or something else.
 * @param entityScheme Standard used to reference the reporting entity.
 * @param entityIdentifier Identifier uniquely identifying the reporting entity.
 * @param start Start of the period the value is measured over.
 * @param endInstant End of the period the value is measured over. (I guess for instant values there is only an end but no start date)
 * @param periodType Type of the period the value is measured over. Either duration or instant.
 * @param lang Language on items containing text as a value.
 * @param type Type of the value e.g. xbrli:monetaryItemType xbrli:stringItemType
 * @param balance Balance of monetary values. Either credit or debit.
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
data class Item(
    @XmlAttribute(name = "name")
    var name: String? = null,

    @XmlElement(name = "label")
    var label: String? = null,
/*
    @XmlElement(name = "contextRef")
    var contextRef: String? = null,
*/
    @XmlElement(name = "unitRef")
    var unitRef: String? = null,
/*
    @XmlElement(name = "dec")
    var dec: Int = 0,
*/
    @XmlElement(name = "value")
    var value: String? = null,

    @XmlElement(name = "entityScheme")
    var entityScheme: String? = null,

    @XmlElement(name = "entityIdentifier")
    var entityIdentifier: String? = null,

    @XmlElement(name = "start")
    var start: Date? = null,

    @XmlElement(name = "endInstant")
    var endInstant: Date? = null,

    @XmlElement(name = "periodType")
    var periodType: String? = null,

    @XmlElement(name = "lang")
    var lang: String? = null,

    @XmlElement(name = "type")
    var type: String? = null,

    @XmlElement(name = "balance")
    var balance: String? = null,

    /*
    @XmlElement(name = "dimension")
    var dimension: List<String>? = null
    */
) {
    val valueNumeric: BigDecimal?
        get() = try {
            value?.toBigDecimal()
        } catch (e: NumberFormatException) {
            //TODO: Proper logging
            println("tried to convert $value of type $type into a BigDecimal")
            e.printStackTrace()
            null
        }
    // 2020-12-31
    val valueDate: Date?
        get() = try {
            value?.let {
                val formatter = SimpleDateFormat("yyyy-mm-dd")
                formatter.parse(value)
            }

        }catch (e: ParseException){
            //TODO: Proper logging
            println("tried to convert $value of type $type into a date")
            e.printStackTrace()
            null
        }
}
