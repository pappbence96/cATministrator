package hu.pappbence.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime

public class JsonJodaTimeAdapter : TypeAdapter<DateTime>() {
    override fun write(writer: JsonWriter?, dateTime: DateTime?) {
        writer?.value(dateTime?.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
    }

    override fun read(reader: JsonReader?): DateTime {
        val dateString = reader?.nextString() ?: throw IllegalArgumentException("No date was given")
        return DateTime.parse(dateString)
    }
}
