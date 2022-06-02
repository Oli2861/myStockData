package com.mystockdata.stockdataservice.livestockdata

import com.google.protobuf.ByteString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class YahooProtoParser {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    // Search Yahoo.Yaticker class
    private val yaTicker = Class.forName("Yahoo\$Yaticker")

    // Search parseFrom method whicht takes nothing but a ByteString
    private val parseFromByteStringMethod = yaTicker.methods.find { method ->
        method.name.equals("parseFrom")
                && method.parameterTypes.contains(ByteString::class.java)
                && method.parameters.size == 1
    }

    /**
     *  @param byteString The proto-byteString to be parsed.
     *  @return The parsed object.
     */
    fun parseProto(byteString: ByteString): Any? = parseFromByteStringMethod?.invoke(this, byteString)

}
