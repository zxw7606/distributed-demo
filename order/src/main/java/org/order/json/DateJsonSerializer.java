package org.order.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;


/**
 * @description: 仅仅是针对Jackson对Date序列化的配置
 * @create: 2019-08-13 11:10
 * @author: zxw
 **/
@Component
public class DateJsonSerializer extends JsonSerializer<Date> {

    @Autowired
    private DateFormatter dateFormatter;
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(dateFormatter.print(value, Locale.getDefault()));
    }
}
